package es.udit.uditflix;

import java.util.Queue;
import java.util.Random;

/**
 * Simula la descarga de un módulo con una latencia aleatoria.
 * Al terminar, registra el módulo en la cola compartida de finalización.
 */
public class TareaDescarga implements Runnable {

    private static final int LATENCIA_BASE_MS = 1000;
    private static final int LATENCIA_RANGO_MS = 3001;
    private static final long NANOS_POR_MS = 1_000_000L;
    /** Trabajo fijo de la fase --cpu: igual para todos los módulos, sea cual sea el tiempo que tarde. */
    private static final long ITERACIONES_CPU = 1_100_000_000L;
    private static final long SEMILLA_CPU = 88_172_645_463_325_252L;

    private final Modulo modulo;
    private final Queue<String> ordenFinalizacion;
    private final boolean conCpu;
    private final Random azar = new Random();

    // No necesitan sincronización: main solo los lee después de join().
    private int latenciaMs;
    private long duracionMs;

    public TareaDescarga(Modulo modulo, Queue<String> ordenFinalizacion, boolean conCpu) {
        this.modulo = modulo;
        this.ordenFinalizacion = ordenFinalizacion;
        this.conCpu = conCpu;
    }

    @Override
    public void run() {
        Thread hilo = Thread.currentThread();
        long inicio = System.nanoTime();
        latenciaMs = LATENCIA_BASE_MS + azar.nextInt(LATENCIA_RANGO_MS);
        System.out.printf("--> [%s] prio=%d latencia=%d ms%n",
                hilo.getName(), hilo.getPriority(), latenciaMs);

        try {
            Thread.sleep(latenciaMs);
        } catch (InterruptedException e) {
            hilo.interrupt();
            System.out.printf("xxx [%s] descarga interrumpida%n", hilo.getName());
            return;
        }

        if (conCpu) {
            long inicioCpu = System.nanoTime();
            long resultado = calcular();
            // Imprimir el resultado evita que el JIT elimine el cálculo.
            System.out.printf("... [%s] prio=%d cálculo=%d ms resultado=%d%n", hilo.getName(), hilo.getPriority(),
                    (System.nanoTime() - inicioCpu) / NANOS_POR_MS, resultado);
        }

        duracionMs = (System.nanoTime() - inicio) / NANOS_POR_MS;
        ordenFinalizacion.add(modulo.getNombre());
        System.out.printf("<-- [%s] prio=%d LISTO (duración=%d ms)%n",
                hilo.getName(), hilo.getPriority(), duracionMs);
    }

    /** Generador xorshift con un número constante de iteraciones. */
    private static long calcular() {
        long x = SEMILLA_CPU;
        for (long i = 0; i < ITERACIONES_CPU; i++) {
            x ^= x << 13;
            x ^= x >>> 7;
            x ^= x << 17;
        }
        return x;
    }

    public int getLatenciaMs() {
        return latenciaMs;
    }

    public long getDuracionMs() {
        return duracionMs;
    }
}
