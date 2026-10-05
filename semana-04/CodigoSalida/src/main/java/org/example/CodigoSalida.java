package org.example;

import jdk.swing.interop.SwingInterOpUtils;

import java.io.IOException;

public class CodigoSalida {
    public static void main (String[] args) {
        System.out.println("=================");
        System.out.println("        COMPROBACIÓN DE SERVIDOR        ");
        System.out.println("=================");

        try {
            // 1. PREPARAMOS EL PROCESO EXTERNO
            // Vamos a ejecutar el comando "ping"
            // En Windows: ping -n 1 8.8.8.8
            // -n 1 -> realiza una sola comprobación
            // 8.8.8.8 -> dirección
            ProcessBuilder pb = new ProcessBuilder(
                    "ping", "-n", "1", "8.8.8.8"
            );
            // 2. LANZAMOS EL PROCESO
            // start() ejecuta el proceso externo
            // El resultado de start() es un objeto Proceso
            Process proceso = pb.start();

            // 3. OBTENEMOS EL PID
            //pid() nos permite conocer el identificador
            System.out.println("PID: " + proceso.pid());

            // 4. ESPERAMOS A QUE TERMINE
            //waitFor() detiene nuestro programa Java hasta que el proceso externo termina
            //además devuelve un número entero

            int codigoSalida = proceso.waitFor();

            // 5. MOSTRAMOS EL CODIGO
            System.out.println("Código de salida: " + codigoSalida);

            //6. INTERPRETAMOS EL RESULTADO
            // codigo 0 - resultado correcto
            // otro codigo - resultado no satisfactorio

            if (codigoSalida == 0){
                System.out.println("ESTADO: ACTIVO");
            } else {
                System.out.println("ESTADO: CAÍDO");
            }
        } catch (IOException e) {
            System.out.println("Error al lanzar el proceso");
        } catch (InterruptedException e) {
            System.out.println("La espera del proceso fue interrumpida");
        }

        System.out.println("====================");
        System.out.println("    FIN DE LA COMPROBACIÓN      ");
        System.out.println("====================");
    }
}