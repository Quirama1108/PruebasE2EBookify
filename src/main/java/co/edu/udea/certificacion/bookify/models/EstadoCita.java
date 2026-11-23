package co.edu.udea.certificacion.bookify.models;

public enum EstadoCita {
    CONFIRMADA, CANCELADA, ATENDIDA, NO_ASISTIO;

    public static EstadoCita de(String texto) {
        return switch (texto) {
            case "confirmada" -> CONFIRMADA;
            case "cancelada" -> CANCELADA;
            case "atendida" -> ATENDIDA;
            case "no asistió" -> NO_ASISTIO;
            default -> throw new IllegalArgumentException("Estado desconocido: " + texto);
        };
    }
}
