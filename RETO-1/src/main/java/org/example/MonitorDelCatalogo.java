package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MonitorDelCatalogo {

    public static void main(String[] args) {
        System.out.println("=========================");
        System.out.println("===UDITFLIX - CATÁLOGO===");
        System.out.println("=========================");

        String[][] contenidos= {
                {"Animacion 3D", "127.0.0.x"},
                {"Videojuegos", "127.0.0.x"},
                {"Kotlin", "127.0.0.x"},
                {"Android", "127.0.0.1"},
                {"Flutter", "127.0.0.1"}
        };

        for (int i = 0; i < contenidos.length; i++) {
            String nombre = contenidos[i][0];
            String direccion = contenidos [i][1];
            System.out.println("[VIDEO]" + nombre);
            try {


                ProcessBuilder pb = new ProcessBuilder(
                        "ping", "-n", "1", direccion
                );


                pb.redirectErrorStream(true);

                Process proceso = pb.start();

                System.out.println("PID: " + proceso.pid());

                int codigo = proceso.waitFor();

                if (codigo == 0) {
                    System.out.println("ESTADO: ACTIVO");
                } else {
                    System.out.println("ESTADO: CAÍDO");
                }

            } catch (IOException e) {
                System.out.println("No se pudo lanzar el proceso");
            } catch (InterruptedException e) {
                System.out.println("La ejecucion fue interrumpida");
            }

        }
        System.out.println("=========================");
        System.out.println("COMPROBACION - FINALIZADA");
        System.out.println("=========================");

    }


}
