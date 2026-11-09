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

    private final Modulo modulo;
    private final Queue<String> ordenFinalizacion;
    private final Random azar = new Random();

    // No necesitan sincronización: main solo los lee después de join().
    private int latenciaMs;
    private long duracionMs;

    public TareaDescarga(Modulo modulo, Queue<String> ordenFinalizacion) {
        this.modulo = modulo;
        this.ordenFinalizacion = ordenFinalizacion;
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

        duracionMs = (System.nanoTime() - inicio) / NANOS_POR_MS;
        ordenFinalizacion.add(modulo.getNombre());
        System.out.printf("<-- [%s] prio=%d LISTO (duración=%d ms)%n",
                hilo.getName(), hilo.getPriority(), duracionMs);
    }

    public int getLatenciaMs() {
        return latenciaMs;
    }

    public long getDuracionMs() {
        return duracionMs;
    }
}
