package es.udit.uditflix;

/** Indicador de progreso: imprime "Cargando…" periódicamente hasta que muere con la JVM. */
public class Spinner implements Runnable {

    private static final int INTERVALO_MS = 300;

    @Override
    public void run() {
        while (true) {
            System.out.println("Cargando…");
            try {
                Thread.sleep(INTERVALO_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
