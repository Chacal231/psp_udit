package org.example;

import java.io.IOException;

public class PipelineAuditoria {
    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("🚀 PIPELINE AUDITORIA EN PARALELO");
        System.out.println("==========================================\n");


        try {
            long inicioParalelo = System.currentTimeMillis();

            System.out.println(" Lanzando proceso 1 ");
            Process p1 = new ProcessBuilder("ping", "-n", "1", "-w", "3000", "127.0.0.1").start();
            System.out.println(" Lanzando proceso 2 ");
            Process p2 = new ProcessBuilder("ping", "-n", "1", "-w", "3000", "192.0.2.1").start();

            int codigo1 = p1.waitFor();
            int codigo2 = p2.waitFor();
            System.out.println("Código de salida del primer proceso: " + codigo1);
            System.out.println("Código de salida del segundo proceso: " + codigo2);

            int intentos1 = 0;
            int intentos2 = 0;

            while (codigo1 != 0 && intentos1 < 3) {
                intentos1++;
                System.out.println("Reintento " + intentos1 + "/3");
                Thread.sleep(1000);
            }

            while (codigo2 != 0 && intentos2 < 3) {
                intentos2++;
                System.out.println("Reintento " + intentos2 + "/3");
                Thread.sleep(1000);
                Process reintento = new ProcessBuilder("ping", "-n", "1", "-w", "3000", "129.0.2.1").start();
                codigo1 = reintento.waitFor();
            }

            if (codigo1 == 0 && codigo2 == 0) {
                System.out.println("Los 2 procesos han terminado correctamente, abriendo el Block de Notas");
                new ProcessBuilder("notepad.exe").start();
            } else {
                System.out.println("Uno de los procesos ha fallado");
                System.out.println("Abriendo la calculadora");
                new ProcessBuilder("calc.exe").start();
            }
            long finParalelo = System.currentTimeMillis();
            System.out.println(" ⏱⏱ TIEMPO TOTAL PARALELO: " + (finParalelo - inicioParalelo) + " ms\n");


        } catch (IOException e) {
            System.out.println("Error: No se pudo lanzar el proceso");
        } catch (InterruptedException e) {
            System.out.println("Error: La espera fue interrumpida de forma inesperada");
        }

    }
}
