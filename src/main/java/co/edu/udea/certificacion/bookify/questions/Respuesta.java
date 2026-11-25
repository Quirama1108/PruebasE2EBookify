package co.edu.udea.certificacion.bookify.questions;

import co.edu.udea.certificacion.bookify.models.Motivo;
import co.edu.udea.certificacion.bookify.utils.Memoria;
import co.edu.udea.certificacion.bookify.models.Sesion;
import io.restassured.response.Response;
import net.serenitybdd.screenplay.Question;

/** Lo que Bookify respondió a la última acción que el actor guardó. */
public final class Respuesta {

    public static final String CUMPLE = "RECHAZADA O A NOMBRE PROPIO";

    private Respuesta() {}

    public static Question<String> recibida() {
        return Question.about("la respuesta de Bookify").answeredBy(actor -> {
            Response respuesta = actor.recall(Memoria.RESPUESTA);
            return describir(respuesta);
        });
    }

    /** Valor del campo, o null si la respuesta no es JSON o no lo trae. */
    public static Question<String> campo(String nombre) {
        return Question.about("el campo " + nombre + " de la respuesta").answeredBy(actor -> {
            Response respuesta = actor.recall(Memoria.RESPUESTA);
            return respuesta.asString().startsWith("{") ? respuesta.path(nombre) : null;
        });
    }

    /** Devuelve "RECHAZO: motivo" solo si es una respuesta 4xx que explica el motivo; si no, lo que respondió Bookify. */
    public static Question<String> rechazoPor(Motivo motivo) {
        return Question.about("el rechazo por " + motivo).answeredBy(actor -> {
            Response respuesta = actor.recall(Memoria.RESPUESTA);
            // #189 y HU-13 piden 403 sin fijar texto.
            boolean prohibido = motivo == Motivo.DE_OTRO_NEGOCIO && respuesta.statusCode() == 403;
            return prohibido || esRechazo(respuesta) && motivo.explicadoEn(mensaje(respuesta))
                    ? rechazo(motivo) : describir(respuesta);
        });
    }

    public static Question<String> rechazoOReservaDe(Sesion sesion) {
        return Question.about("el rechazo o la cita a nombre propio").answeredBy(actor -> {
            Response respuesta = actor.recall(Memoria.RESPUESTA);
            if (respuesta.statusCode() == 201) {
                return sesion.idUsuario().equals(respuesta.path("clienteId"))
                        ? CUMPLE : "CITA REGISTRADA A NOMBRE DE OTRO CLIENTE";
            }
            // #291 exige la identidad del token: solo un 403 es un rechazo por ese motivo.
            return respuesta.statusCode() == 403 ? CUMPLE : describir(respuesta);
        });
    }

    public static String rechazo(Motivo motivo) { return "RECHAZO: " + motivo; }

    /** Un 401 o un 5xx no son decisiones de negocio y «El correo ya está registrado» es el mensaje equivocado de #184. */
    private static boolean esRechazo(Response respuesta) {
        int codigo = respuesta.statusCode();
        return codigo >= 400 && codigo < 500 && codigo != 401
                && !mensaje(respuesta).contains("correo ya est");
    }

    private static String mensaje(Response respuesta) {
        String cuerpo = respuesta.asString();
        if (!cuerpo.startsWith("{")) {
            return "";
        }
        return respuesta.path("error") + " " + respuesta.path("mensaje") + " " + respuesta.path("message");
    }

    private static String describir(Response respuesta) {
        return respuesta.statusCode() == 201 || respuesta.statusCode() == 200
                ? "ACEPTADA" : "HTTP " + respuesta.statusCode() + " " + respuesta.asString();
    }
}
