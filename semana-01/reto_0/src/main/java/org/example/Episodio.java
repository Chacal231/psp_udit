package org.example;

public class Episodio {

    // Atriubutos
    private String titulo;
    private int duracion;

    // Constructores
    public Episodio(String titulo, int duracion){
        this.titulo = titulo;
        this.duracion = duracion;
    }

    // Metodo
    public void procesar (){
        System.out.println("Iniciando procesamiento de: " + this.titulo + " ...");
        // El bloque try-catch es obligatorio en java al pausar el hilo(thread)
        // Evita que el programa explote (Crash) si el SO interrumple la pausa
        try{
            // Pausamos la ejecucion en 2000 milisegundos(2s)
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Error al procesar episodio");
        }
        System.out.println("Completado: " + this.titulo);
    }



}
