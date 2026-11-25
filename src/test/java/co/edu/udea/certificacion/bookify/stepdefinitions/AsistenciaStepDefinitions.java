package co.edu.udea.certificacion.bookify.stepdefinitions;

import co.edu.udea.certificacion.bookify.models.EstadoCita;
import co.edu.udea.certificacion.bookify.models.Motivo;
import co.edu.udea.certificacion.bookify.models.Sesion;
import co.edu.udea.certificacion.bookify.questions.HistorialDelNegocio;
import co.edu.udea.certificacion.bookify.questions.RegistroDeAsistencia;
import co.edu.udea.certificacion.bookify.tasks.MarcarCita;
import co.edu.udea.certificacion.bookify.utils.Memoria;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;

import static co.edu.udea.certificacion.bookify.stepdefinitions.Escenario.actor;
import static co.edu.udea.certificacion.bookify.stepdefinitions.Escenario.rechazoPor;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

public class AsistenciaStepDefinitions {

    @Cuando("{negocio} marca la cita de {cliente} como {string}")
    @Cuando("{negocio} intenta marcar la cita de {cliente} como {string}")
    public void marca(String negocio, String cliente, String estado) {
        actor(negocio).attemptsTo(MarcarCita.como(EstadoCita.de(estado), cita(cliente)));
    }

    @Entonces("{negocio} recibe un rechazo porque la cita no ha terminado")
    public void rechazaPorNoTerminada(String negocio) {
        actor(negocio).should(rechazoPor(Motivo.CITA_SIN_TERMINAR));
    }

    @Entonces("{negocio} recibe un rechazo porque la cita está cancelada")
    public void rechazaPorCancelada(String negocio) {
        actor(negocio).should(rechazoPor(Motivo.CITA_CANCELADA));
    }

    @Entonces("{negocio} recibe un rechazo porque la cita es de otro negocio")
    public void rechazaPorCitaAjena(String negocio) {
        actor(negocio).should(rechazoPor(Motivo.DE_OTRO_NEGOCIO));
    }

    @Entonces("la cita de {cliente} figura como {string} en el historial de {negocio}")
    public void figuraComo(String cliente, String estado, String negocio) {
        actor(negocio).should(seeThat(HistorialDelNegocio.paraLasCitasDe(actor(cliente)),
                contains(EstadoCita.de(estado).name())));
    }

    @Entonces("queda registrado que {negocio} cambió la cita de {cliente} de {string} a {string}")
    public void quedaRegistrado(String negocio, String cliente, String anterior, String nuevo) {
        Sesion sesion = actor(negocio).recall(Memoria.SESION);
        actor(negocio).should(seeThat(RegistroDeAsistencia.de(cita(cliente)), contains(
                RegistroDeAsistencia.cambio(EstadoCita.de(anterior), EstadoCita.de(nuevo), sesion.idUsuario()))));
    }

    @Entonces("no queda registro de asistencia de la cita de {cliente}")
    public void noQuedaRegistro(String cliente) {
        actor(cliente).should(seeThat(RegistroDeAsistencia.de(cita(cliente)), empty()));
    }

    private static String cita(String cliente) { return actor(cliente).recall(Memoria.CITA); }
}
