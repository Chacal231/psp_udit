package org.example;

import java.io.IOException;

public class LanzadorVideo {
    public static void main (String[] args){
        System.out.println("Solicitaremos al SO la creacion de proceso nativo");

        //1. ProcessBuilder es el puente entre java y el sistema Operativo
        //Preparamos el comando (en windows, usamos cmd /c start para abrir la URL con el navegador predeterminado)

        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start", "https://www.youtube.com/watch?v=LXb3EKWsInQ");

        try {
            Process proceso = pb.start();
            System.out.println("proceso en ejecucion. PID:" + proceso.pid());
            System.out.println("Video lanzado con exito");

        }catch (IOException e){
            System.out.println("Error: el sistema operativo no encuentra el programa.");
        }
    }
}