package es.udit.uditflix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Motor de descarga paralela: lanza un hilo por módulo y habilita el login
 * solo cuando han terminado todos.
 */
public class MainUDITflix {

    private static final long NANOS_POR_MS = 1_000_000L;
    private static final String FLAG_SPINNER = "--spinner";
    private static final String FLAG_CPU = "--cpu";

    public static void main(String[] args) {
        List<String> flags = Arrays.asList(args);
        boolean conCpu = flags.contains(FLAG_CPU);
        System.out.println("[MAIN] Procesadores disponibles: " + Runtime.getRuntime().availableProcessors());

        Queue<String> ordenFinalizacion = new ConcurrentLinkedQueue<>();
        List<TareaDescarga> tareas = new ArrayList<>();
        ArrayList<Thread> hilos = new ArrayList<>();

        for (Modulo modulo : Modulo.values()) {
            TareaDescarga tarea = new TareaDescarga(modulo, ordenFinalizacion, conCpu);
            Thread hilo = new Thread(tarea, modulo.getNombreHilo());
            hilo.setPriority(modulo.getPrioridad());
            tareas.add(tarea);
            hilos.add(hilo);
        }

        System.out.printf("[MAIN] Descargando %d módulos en paralelo...%n", hilos.size());
        if (flags.contains(FLAG_SPINNER)) {
            lanzarSpinner();
        }
        long inicio = System.nanoTime();

        for (Thread hilo : hilos) {
            hilo.start();
        }

        try {
            for (Thread hilo : hilos) {
                hilo.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[MAIN] Espera interrumpida -> login NO habilitado");
            return;
        }

        long totalRealMs = (System.nanoTime() - inicio) / NANOS_POR_MS;

        if (ordenFinalizacion.size() != tareas.size()) {
            System.out.println("[MAIN] Faltan módulos por descargar -> login NO habilitado");
            return;
        }
        imprimirInforme(tareas, ordenFinalizacion, totalRealMs, conCpu);
    }

    private static void lanzarSpinner() {
        Thread spinner = new Thread(new Spinner(), "spinner");
        // La JVM termina cuando solo quedan hilos demonio: al acabar main y las
        // descargas, este bucle infinito no impide que el programa finalice.
        spinner.setDaemon(true);
        spinner.start();
    }

    /** Muestra el login habilitado junto con los tiempos y el orden de finalización. */
    private static void imprimirInforme(List<TareaDescarga> tareas, Queue<String> orden, long totalRealMs,
                                        boolean conCpu) {
        long masLentoMs = 0;
        long sumaLatenciasMs = 0;
        long sumaDuracionesMs = 0;
        for (TareaDescarga tarea : tareas) {
            masLentoMs = Math.max(masLentoMs, tarea.getDuracionMs());
            sumaLatenciasMs += tarea.getLatenciaMs();
            sumaDuracionesMs += tarea.getDuracionMs();
        }

        System.out.println("[MAIN] Todos los módulos listos -> LOGIN HABILITADO");
        System.out.printf("Tiempo total real: ≈ %d ms / Duración del más lento: %d ms / Suma de todas las latencias: %d ms%n",
                totalRealMs, masLentoMs, sumaLatenciasMs);
        if (conCpu) {
            System.out.printf("Suma de duraciones reales (latencia + cálculo): %d ms%n", sumaDuracionesMs);
        }
        // Con --cpu la suma de latencias no incluye el cálculo: se compara con la de duraciones.
        long sumaComparada = conCpu ? sumaDuracionesMs : sumaLatenciasMs;
        System.out.printf("Comparación: total real ≈ más lento (diferencia %d ms) y no la suma (diferencia %d ms)%n",
                totalRealMs - masLentoMs, sumaComparada - totalRealMs);
        System.out.println("Orden de finalización: " + String.join(" -> ", orden));
    }
}
