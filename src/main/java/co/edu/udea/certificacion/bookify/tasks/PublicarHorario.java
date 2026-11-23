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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Map;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class PublicarHorario implements Task {

    private final Negocio duenio;
    private final LocalDate dia;
    private final String horaInicial;
    private final String horaFinal;

    public PublicarHorario(Negocio duenio, LocalDate dia, String horaInicial, String horaFinal) {
        this.duenio = duenio;
        this.dia = dia;
        this.horaInicial = horaInicial;
        this.horaFinal = horaFinal;
    }

    @Override
    @Step("{0} publica un horario de #horaInicial a #horaFinal el #dia")
    public <T extends Actor> void performAs(T actor) {
        Sesion sesion = actor.recall(Memoria.SESION);
        OffsetDateTime inicio = a(horaInicial);
        OffsetDateTime fin = a(horaFinal);
        actor.attemptsTo(Post.to("/disponibilidades").with(peticion -> peticion
                .contentType(ContentType.JSON)
                .header("Authorization", sesion.autorizacion())
                .body(Map.of("agendaServicioId", duenio.agenda(),
                        "inicioAt", inicio.toString(), "finAt", fin.toString()))));
        Response respuesta = SerenityRest.lastResponse();
        actor.remember(Memoria.RESPUESTA, respuesta);
        if (respuesta.statusCode() == 201) {
            actor.remember(Memoria.HORARIO, new Horario(respuesta.path("id"), inicio, fin));
        }
    }

    private OffsetDateTime a(String hora) {
        return dia.atTime(LocalTime.parse(hora)).atZone(Horario.BOGOTA).toOffsetDateTime();
    }

    public static PublicarHorario enLaAgendaDe(Negocio duenio, LocalDate dia, String horaInicial, String horaFinal) {
        return instrumented(PublicarHorario.class, duenio, dia, horaInicial, horaFinal);
    }
}
