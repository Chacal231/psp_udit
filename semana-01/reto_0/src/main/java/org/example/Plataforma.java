package org.example;

import java.util.ArrayList;

public class Plataforma {

    private ArrayList<Episodio> catalogo = new ArrayList<>();

    public Plataforma(ArrayList catalogo) {
        this.catalogo = new ArrayList<>();
    }

    public void agregarEpisodio(Episodio e) {
        catalogo.add(new Episodio ("Episodio1" ,50));
        catalogo.add(new Episodio ("Episodio2" ,20));
        catalogo.add(new Episodio ("Episodio3" ,30));
        catalogo.add(new Episodio ("Episodio4" ,40));
        catalogo.add(new Episodio ("Episodio5" ,10));
    }
    public void procesarCatalogo() {
        for(Episodio e : catalogo) {
            e.procesar();
        }
    }
}
