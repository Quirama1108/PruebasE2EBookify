package co.edu.udea.certificacion.bookify.models;

/** Negocios de la semilla; el mismo id identifica proveedor, servicio y agenda del servicio. */
public enum Negocio {
    BARBERIA("la Barbería", "barberia@bookify-qa.test", "aaaaaaaa-0000-4000-8000-000000000001"),
    SPA("el Spa", "spa@bookify-qa.test", "bbbbbbbb-0000-4000-8000-000000000001");

    private final String nombre;
    private final String correo;
    private final String id;

    Negocio(String nombre, String correo, String id) {
        this.nombre = nombre;
        this.correo = correo;
        this.id = id;
    }

    public String nombre() { return nombre; }

    public String correo() { return correo; }

    public String servicio() { return id; }

    public String agenda() { return id; }

    public String proveedor() { return id; }

    public static Negocio llamado(String nombre) {
        for (Negocio negocio : values()) {
            if (negocio.nombre.equals(nombre)) {
                return negocio;
            }
        }
        throw new IllegalArgumentException("Negocio desconocido: " + nombre);
    }
}
