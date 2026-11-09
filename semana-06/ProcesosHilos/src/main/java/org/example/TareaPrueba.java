package org.example;

public class TareaPrueba implements Runnable {
    //nombre que identifica la tarea, ej HILO-A
    private final String nombre;

    public TareaPrueba (String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void run() {
        //repetimos el bloque 3 veces
        for (int i = 1 ; i <= 3 ; i++) {
            //mostrar el nombre de la tarea, el hilo que ejecuta esta instrucciÃ³n y el paso
            //con start() podremos ver el HILO-A o HILO-B
            //con run() llamado desde main, veremos "main"

            System.out.println(
                    nombre + " [" + Thread.currentThread().getName() + "] - Paso: " + i
            );
            try {
                //detiene temporalmente el hilo actual durante 100 milisegundos
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}