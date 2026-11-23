package co.edu.udea.certificacion.bookify.tasks;

import co.edu.udea.certificacion.bookify.interactions.Post;
import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

import java.util.Map;
import java.util.UUID;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/** Crea una cuenta propia del escenario, con correo y apellido únicos, e inicia sesión con ella. */
public class RegistrarseComoCliente implements Task {

    @Override
    @Step("{0} se registra como cliente")
    public <T extends Actor> void performAs(T actor) {
        String unico = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String nombreCompleto = actor.getName() + " Prueba" + unico;
        String correo = actor.getName().toLowerCase() + "." + unico + "@bookify-qa.test";
        String clave = "Clave-Segura-Qa-" + unico;
        actor.attemptsTo(Post.to("/auth/register").with(peticion -> peticion
                .contentType(ContentType.JSON)
                .body(Map.of("nombre", nombreCompleto, "correo", correo, "contrasena", clave, "rol", "CLIENTE"))));
        SerenityRest.lastResponse().then().statusCode(201);
        actor.attemptsTo(IniciarSesion.con(correo, clave, nombreCompleto));
    }

    public static RegistrarseComoCliente nuevo() { return instrumented(RegistrarseComoCliente.class); }
}
