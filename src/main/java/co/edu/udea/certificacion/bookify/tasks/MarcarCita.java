package co.edu.udea.certificacion.bookify.tasks;

import co.edu.udea.certificacion.bookify.interactions.Patch;
import co.edu.udea.certificacion.bookify.models.EstadoCita;
import co.edu.udea.certificacion.bookify.models.Sesion;
import co.edu.udea.certificacion.bookify.utils.Memoria;
import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

import java.util.Map;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class MarcarCita implements Task {

    private final String cita;
    private final EstadoCita estado;

    public MarcarCita(String cita, EstadoCita estado) {
        this.cita = cita;
        this.estado = estado;
    }

    @Override
    @Step("{0} marca la cita como #estado")
    public <T extends Actor> void performAs(T actor) {
        Sesion sesion = actor.recall(Memoria.SESION);
        actor.attemptsTo(Patch.to("/comercio/reservas/" + cita + "/asistencia").with(peticion -> peticion
                .contentType(ContentType.JSON)
                .header("Authorization", sesion.autorizacion())
                .body(Map.of("estado", estado.name()))));
        actor.remember(Memoria.RESPUESTA, SerenityRest.lastResponse());
    }

    public static MarcarCita como(EstadoCita estado, String cita) {
        return instrumented(MarcarCita.class, cita, estado);
    }
}
