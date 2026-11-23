package co.edu.udea.certificacion.bookify.models;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public record Horario(String id, OffsetDateTime inicio, OffsetDateTime fin) {

    public static final ZoneId BOGOTA = ZoneId.of("America/Bogota");

    public LocalDate dia() { return inicio.atZoneSameInstant(BOGOTA).toLocalDate(); }

    public Horario trasladadoA(OffsetDateTime nuevoInicio, OffsetDateTime nuevoFin) {
        return new Horario(id, nuevoInicio, nuevoFin);
    }
}
