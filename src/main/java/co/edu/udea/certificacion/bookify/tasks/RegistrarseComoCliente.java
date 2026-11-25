package co.edu.udea.certificacion.bookify.tasks;

import co.edu.udea.certificacion.bookify.interactions.Post;
import co.edu.udea.certificacion.bookify.models.Cuenta;
import co.edu.udea.certificacion.bookify.utils.Memoria;
import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

import java.util.Map;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class RegistrarseComoCliente implements Task {

    private final Cuenta cuenta;

    public RegistrarseComoCliente(Cuenta cuenta) { this.cuenta = cuenta; }

    @Override
    @Step("{0} se registra como cliente")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Post.to("/auth/register").with(peticion -> peticion
                .contentType(ContentType.JSON)
                .body(Map.of("nombre", cuenta.nombreCompleto(), "correo", cuenta.correo(),
                        "contrasena", cuenta.clave(), "rol", "CLIENTE"))));
        actor.remember(Memoria.RESPUESTA, SerenityRest.lastResponse());
    }

    public static RegistrarseComoCliente con(Cuenta cuenta) {
        return instrumented(RegistrarseComoCliente.class, cuenta);
    }
}
