package co.edu.udea.certificacion.bookify.models;

import java.util.UUID;

import static co.edu.udea.certificacion.bookify.utils.Configuracion.claveNegocios;

public record Cuenta(String nombreCompleto, String correo, String clave) {

    /** Correo y apellido únicos: cada escenario usa clientes propios. */
    public static Cuenta nueva(String nombre) {
        String unico = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        return new Cuenta(nombre + " Prueba" + unico,
                nombre.toLowerCase() + "." + unico + "@bookify-qa.test", "Clave-Segura-Qa-" + unico);
    }

    public static Cuenta de(Negocio negocio) {
        return new Cuenta(negocio.nombre(), negocio.correo(), claveNegocios());
    }
}
