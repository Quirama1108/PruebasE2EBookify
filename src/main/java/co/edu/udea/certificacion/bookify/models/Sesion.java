package co.edu.udea.certificacion.bookify.models;

public record Sesion(String idUsuario, String nombreCompleto, String token) {

    public String autorizacion() { return "Bearer " + token; }
}
