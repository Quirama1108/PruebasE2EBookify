package co.edu.udea.certificacion.bookify.tasks;

import co.edu.udea.certificacion.bookify.interactions.Post;
import co.edu.udea.certificacion.bookify.models.Cuenta;
import co.edu.udea.certificacion.bookify.models.Sesion;
import co.edu.udea.certificacion.bookify.questions.Respuesta;
import co.edu.udea.certificacion.bookify.utils.Memoria;
import co.edu.udea.certificacion.bookify.utils.connetiondb.BaseDatos;
import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

import java.util.Map;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class IniciarSesion implements Task {

    private final Cuenta cuenta;

    public IniciarSesion(Cuenta cuenta) { this.cuenta = cuenta; }

    @Override
    @Step("{0} inicia sesión en Bookify")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Post.to("/auth/login").with(peticion -> peticion
                .contentType(ContentType.JSON)
                .body(Map.of("correo", cuenta.correo(), "password", cuenta.clave()))));
        actor.remember(Memoria.RESPUESTA, SerenityRest.lastResponse());
        // La subconsulta da una fila aunque el usuario no exista: el rechazo lo informa el paso, no esta tarea.
        String idUsuario = BaseDatos.qa().executeQuery(
                "SELECT (SELECT id_usuario FROM tbl_usuario WHERE correo = ?) AS id_usuario", cuenta.correo())
                .get(0).get("id_usuario");
        actor.remember(Memoria.SESION,
                new Sesion(idUsuario, cuenta.nombreCompleto(), actor.asksFor(Respuesta.campo("accessToken"))));
    }

    public static IniciarSesion con(Cuenta cuenta) { return instrumented(IniciarSesion.class, cuenta); }
}
