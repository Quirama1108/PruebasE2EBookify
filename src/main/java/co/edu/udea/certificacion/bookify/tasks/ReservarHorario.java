package co.edu.udea.certificacion.bookify.tasks;

import co.edu.udea.certificacion.bookify.interactions.Post;
import co.edu.udea.certificacion.bookify.models.Horario;
import co.edu.udea.certificacion.bookify.models.Negocio;
import co.edu.udea.certificacion.bookify.models.Sesion;
import co.edu.udea.certificacion.bookify.utils.Memoria;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

import java.util.Map;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class ReservarHorario implements Task {

    private final Horario horario;
    private final Actor titular;

    public ReservarHorario(Horario horario, Actor titular) {
        this.horario = horario;
        this.titular = titular;
    }

    @Override
    @Step("{0} reserva el horario a nombre de #titular")
    public <T extends Actor> void performAs(T actor) {
        Sesion sesion = actor.recall(Memoria.SESION);
        Sesion sesionTitular = titular.recall(Memoria.SESION);
        actor.attemptsTo(Post.to("/bookings").with(peticion -> peticion
                .contentType(ContentType.JSON)
                .header("Authorization", sesion.autorizacion())
                .body(Map.of("clienteId", sesionTitular.idUsuario(), "disponibilidadId", horario.id(),
                        "servicioId", Negocio.BARBERIA.servicio()))));
        Response respuesta = SerenityRest.lastResponse();
        actor.remember(Memoria.RESPUESTA, respuesta);
        if (respuesta.statusCode() == 201) {
            actor.remember(Memoria.CITA, respuesta.<String>path("id"));
        }
    }

    public static ReservarHorario aNombreDe(Actor titular, Horario horario) {
        return instrumented(ReservarHorario.class, horario, titular);
    }
}
