package es.udit.uditflix;

/**
 * Módulos que hay que descargar antes de habilitar el login.
 * Para añadir un módulo basta con añadir una línea a este enum.
 */
public enum Modulo {
    CATALOGO("catalogo-metadatos", "DL-catalogo", Thread.MAX_PRIORITY),
    SESION("sesion-y-perfil", "DL-sesion", Thread.MAX_PRIORITY),
    PORTADAS("portadas-assets", "DL-portadas", Thread.NORM_PRIORITY),
    ANALITICA("analiticas", "DL-analitica", Thread.MIN_PRIORITY);

    private final String nombre;
    private final String nombreHilo;
    private final int prioridad;

    Modulo(String nombre, String nombreHilo, int prioridad) {
        this.nombre = nombre;
        this.nombreHilo = nombreHilo;
        this.prioridad = prioridad;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNombreHilo() {
        return nombreHilo;
    }

    public int getPrioridad() {
        return prioridad;
    }
}
