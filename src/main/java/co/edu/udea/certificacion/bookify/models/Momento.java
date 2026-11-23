package co.edu.udea.certificacion.bookify.models;

import java.time.OffsetDateTime;

/** Franja de una hora relativa al instante actual; los microsegundos la hacen única en la agenda. */
public enum Momento {
    EN_CURSO(-30),
    TERMINADO(-120);

    private final int minutos;

    Momento(int minutos) { this.minutos = minutos; }

    public OffsetDateTime inicio() { return OffsetDateTime.now(Horario.BOGOTA).plusMinutes(minutos); }
}
