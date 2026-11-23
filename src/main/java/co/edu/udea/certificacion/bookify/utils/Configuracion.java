package co.edu.udea.certificacion.bookify.utils;

/** Orden de lectura: propiedad de sistema, variable de entorno (bookify.api.url -> BOOKIFY_API_URL), valor local. */
public final class Configuracion {

    private Configuracion() {}

    public static String valor(String clave, String porDefecto) {
        String entorno = System.getenv(clave.toUpperCase().replace('.', '_'));
        return System.getProperty(clave, entorno == null ? porDefecto : entorno);
    }

    public static String urlApi() { return valor("bookify.api.url", "http://localhost:8090/api/v1"); }

    // Ficticia: es la clave que siembra ambiente/03_semilla.sql en la base desechable.
    public static String claveNegocios() { return valor("bookify.negocios.clave", "Calidad-QA-Local-2026!"); }
}
