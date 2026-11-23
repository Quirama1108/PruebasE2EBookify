package co.edu.udea.certificacion.bookify.models;

import java.util.regex.Pattern;

/** Sentido, no texto literal, de cada rechazo. */
public enum Motivo {
    CRUCE_DE_HORARIOS("traslap|solap|cruz|conflicto"),
    HORAS_NO_VALIDAS("inicio.*anterior|fin.*posterior|hora.*no.*v.lid"),
    HORARIO_NO_DISPONIBLE("ya fue reservad|no est. disponible|no disponible|ya no puede reservarse|ocupad"),
    HORARIO_YA_COMENZO("ya (comenz|inici|pas|termin)|pasad[oa]|venci|futur|posterior"),
    CITA_SIN_TERMINAR("no ha (finaliz|termin)|a.n no (finaliz|termin)"),
    CITA_CANCELADA("cancelad"),
    DE_OTRO_NEGOCIO("otro negocio|otro comercio|otro proveedor|no pertenece|no (est. )?autorizad");

    private final Pattern sentido;

    Motivo(String sentido) {
        this.sentido = Pattern.compile(sentido, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
    }

    public boolean explicadoEn(String mensaje) { return sentido.matcher(mensaje).find(); }
}
