package co.edu.udea.certificacion.bookify.utils.connetiondb;

public final class BaseDatos {

    private static final ExecuteQueryDb QA = new PostgreSQLConnection("postgresql.properties");

    private BaseDatos() {}

    public static ExecuteQueryDb qa() { return QA; }
}
