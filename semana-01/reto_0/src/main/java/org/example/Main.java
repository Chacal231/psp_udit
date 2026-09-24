package org.example;

import java.util.ArrayList;// Herramientas nativas para listas

public class Main {


    public static void main(String[] args) {

        Plataforma plataforma = new Plataforma(new ArrayList<>());
            plataforma.agregarEpisodio(null);
            plataforma.procesarCatalogo();

        }/*
        System.out.println("--- INICIANDO UDITVERSUM ---");

        ArrayList<Episodio> catalogo = new ArrayList<>();
        // Mala practica: Toda la logica de negocio tirada al main.
        // los diamantes <> obligan a que la lista SOLO acepte episodios
        catalogo.add(new Episodio("Diseño 3D - Intro", 45));
        catalogo.add(new Episodio("Animacion", 60));
        catalogo.add(new Episodio("Texturas", 50));

        // Medicion de tiempo
        // Usamos long porque los milisegundos son desde 1970 forman un numero
        // Tan giantesco que no cabe en la memoria int normal

        long inicio = System.currentTimeMillis();
        // Esto es un bucle for each "Por cada espiodio dentro del catalogo"
        for(Episodio ep : catalogo) {
            ep.procesar(); // Aqui ocurre el bloqueo de 2s
        }

        long fin = System.currentTimeMillis();

        // Resultado: restamos fin menos inicio y dividiemos entre mil (segundos)

        long totalSegundos = (fin - inicio) / 1000;

        System.out.println("Tiempo total: " + totalSegundos + " segundos de bloqueo. ");
        */


    }


