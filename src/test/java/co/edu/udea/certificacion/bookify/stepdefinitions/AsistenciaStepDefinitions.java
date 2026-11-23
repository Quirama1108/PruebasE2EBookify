package co.edu.udea.certificacion.bookify.stepdefinitions;

import co.edu.udea.certificacion.bookify.models.EstadoCita;
import co.edu.udea.certificacion.bookify.models.Motivo;
import co.edu.udea.certificacion.bookify.models.Negocio;
import co.edu.udea.certificacion.bookify.models.Sesion;
import co.edu.udea.certificacion.bookify.questions.HistorialDelNegocio;
import co.edu.udea.certificacion.bookify.questions.RegistroDeAsistencia;
import co.edu.udea.certificacion.bookify.tasks.MarcarCita;
import co.edu.udea.certificacion.bookify.utils.Memoria;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;

import static co.edu.udea.certificacion.bookify.stepdefinitions.DisponibilidadStepDefinitions.rechazoPor;
import static co.edu.udea.certificacion.bookify.stepdefinitions.Escenario.barberia;
import static co.edu.udea.certificacion.bookify.stepdefinitions.Escenario.actor;
import static co.edu.udea.certificacion.bookify.stepdefinitions.Escenario.negocio;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

public class AsistenciaStepDefinitions {

    @Cuando("la Barbería marca la cita de {cliente} como {string}")
    @Cuando("la Barbería intenta marcar la cita de {cliente} como {string}")
    public void laBarberiaMarca(String nombre, String estado) {
        barberia().attemptsTo(MarcarCita.como(EstadoCita.de(estado), cita(nombre)));
    }

    @Cuando("el Spa intenta marcar la cita de {cliente} como {string}")
    public void elSpaMarca(String nombre, String estado) {
        negocio(Negocio.SPA).attemptsTo(MarcarCita.como(EstadoCita.de(estado), cita(nombre)));
    }

    @Entonces("Bookify rechaza el registro porque la cita no ha terminado")
    public void rechazaPorNoTerminada() {
        barberia().should(rechazoPor(Motivo.CITA_SIN_TERMINAR));
    }

    @Entonces("Bookify rechaza el registro porque la cita está cancelada")
    public void rechazaPorCancelada() {
        barberia().should(rechazoPor(Motivo.CITA_CANCELADA));
    }

    @Entonces("Bookify rechaza el registro porque la cita es de otro negocio")
    public void rechazaPorCitaAjena() {
        negocio(Negocio.SPA).should(rechazoPor(Motivo.DE_OTRO_NEGOCIO));
    }

    @Entonces("la cita de {cliente} figura como {string} en el historial de la Barbería")
    public void figuraComo(String nombre, String estado) {
        barberia().should(seeThat(HistorialDelNegocio.paraLasCitasDe(actor(nombre)),
                contains(EstadoCita.de(estado).name())));
    }

    @Entonces("queda registrado que la Barbería cambió la cita de {string} a {string}")
    public void quedaRegistrado(String anterior, String nuevo) {
        Sesion sesion = barberia().recall(Memoria.SESION);
        barberia().should(seeThat(RegistroDeAsistencia.de(cita("Ana")), contains(
                RegistroDeAsistencia.cambio(EstadoCita.de(anterior), EstadoCita.de(nuevo), sesion.idUsuario()))));
    }

    @Entonces("no queda registro de asistencia de esa cita")
    public void noQuedaRegistro() {
        barberia().should(seeThat(RegistroDeAsistencia.de(cita("Ana")), empty()));
    }

    private static String cita(String nombre) { return actor(nombre).recall(Memoria.CITA); }
}
