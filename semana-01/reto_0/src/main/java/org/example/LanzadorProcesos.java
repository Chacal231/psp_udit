package org.example;


import java.io.IOException;

public class LanzadorProcesos {
    public static void main(String[] args) {
        System.out.println("Solicitando al SO la creacion de un proceso");

        //1. ProcessBuilder es el puente entre Java y el Sistema Operativo
        // Preparamos el comando (en Windows, notepad.exe es el block de notas).

        ProcessBuilder pb = new ProcessBuilder("notepad.exe");
        try {
            //2. start () es el gatillo. Pide al planificador del SO que cree el proceso
            Process proceso = pb.start();
            System.out.println("Proceso en ejecucion. PID: " + proceso.pid());
            //3. waitFor() pausa nuestro hilo de Java hasta que el usuario cierre el block de notas
            int salida = proceso.waitFor();
            // 0 significa cierre limpio. Cualquier otro numero indica error o cierre forzoso
            System.out.println("El proceso externo finalizo con codigo: " + salida);


        } catch (IOException e){
            System.out.println("Error: el sistema operatico no encuentra el programa ");
        }catch (InterruptedException e){
            System.out.println("Error: el proceso fue interrumpido bruscamente");

        }
    }
}
