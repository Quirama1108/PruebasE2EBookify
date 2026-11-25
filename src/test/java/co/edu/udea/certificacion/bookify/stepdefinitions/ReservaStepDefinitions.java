package co.edu.udea.certificacion.bookify.stepdefinitions;

import co.edu.udea.certificacion.bookify.models.Horario;
import co.edu.udea.certificacion.bookify.models.Momento;
import co.edu.udea.certificacion.bookify.models.Motivo;
import co.edu.udea.certificacion.bookify.models.Negocio;
import co.edu.udea.certificacion.bookify.models.Sesion;
import co.edu.udea.certificacion.bookify.questions.HistorialDelNegocio;
import co.edu.udea.certificacion.bookify.questions.OfertaDeHorarios;
import co.edu.udea.certificacion.bookify.questions.Respuesta;
import co.edu.udea.certificacion.bookify.questions.TitularesDelHorario;
import co.edu.udea.certificacion.bookify.tasks.PrepararCancelacion;
import co.edu.udea.certificacion.bookify.tasks.PrepararHorario;
import co.edu.udea.certificacion.bookify.tasks.ReservarHorario;
import co.edu.udea.certificacion.bookify.tasks.TrasladarCita;
import co.edu.udea.certificacion.bookify.utils.Memoria;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.screenplay.Actor;

import java.util.Collection;

import static co.edu.udea.certificacion.bookify.stepdefinitions.Escenario.barberia;
import static co.edu.udea.certificacion.bookify.stepdefinitions.Escenario.aceptada;
import static co.edu.udea.certificacion.bookify.stepdefinitions.Escenario.actor;
import static co.edu.udea.certificacion.bookify.stepdefinitions.Escenario.horario;
import static co.edu.udea.certificacion.bookify.stepdefinitions.Escenario.rechazoPor;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

public class ReservaStepDefinitions {

    private Collection<Horario> ofertaConsultada;

    @Cuando("{cliente} consulta la oferta de la Barbería")
    public void consultaLaOferta(String nombre) {
        ofertaConsultada = actor(nombre).asksFor(OfertaDeHorarios.de(Negocio.BARBERIA));
    }

    @Entonces("{cliente} encuentra ese horario en la oferta")
    public void encuentraElHorario(String nombre) {
        actor(nombre).should(seeThat("la oferta consultada", actor -> ofertaConsultada, hasItem(horario())));
    }

    @Cuando("{cliente} reserva ese horario")
    @Cuando("{cliente} intenta reservar ese horario")
    public void reserva(String nombre) {
        Actor cliente = actor(nombre);
        cliente.attemptsTo(ReservarHorario.aNombreDe(cliente, horario()));
    }

    @Cuando("{cliente} intenta reservar ese horario a nombre de {cliente}")
    public void reservaANombreDe(String nombre, String titular) {
        actor(nombre).attemptsTo(ReservarHorario.aNombreDe(actor(titular), horario()));
    }

    @Dado("que {cliente} tiene una cita confirmada con la Barbería que no ha comenzado")
    public void tieneUnaCitaConfirmada(String nombre) {
        barberia().wasAbleTo(PrepararHorario.futuro());
        reserva(nombre);
        quedaConfirmada(nombre);
    }

    @Dado("que {cliente} tiene una cita confirmada con la Barbería que está en curso")
    public void tieneUnaCitaEnCurso(String nombre) {
        tieneUnaCitaConfirmada(nombre);
        barberia().wasAbleTo(TrasladarCita.a(Momento.EN_CURSO));
    }

    @Dado("que {cliente} tiene una cita confirmada con la Barbería que ya terminó")
    public void tieneUnaCitaTerminada(String nombre) {
        tieneUnaCitaConfirmada(nombre);
        terminaLaHora();
    }

    @Dado("que {cliente} tenía una cita con la Barbería que fue cancelada y cuya hora ya pasó")
    public void teniaUnaCitaCancelada(String nombre) {
        tieneUnaCitaConfirmada(nombre);
        actor(nombre).wasAbleTo(PrepararCancelacion.deSuCita());
        terminaLaHora();
    }

    @Cuando("termina la hora de la cita")
    public void terminaLaHora() {
        barberia().wasAbleTo(TrasladarCita.a(Momento.TERMINADO));
    }

    @Entonces("la reserva de {cliente} queda confirmada")
    public void quedaConfirmada(String nombre) {
        Actor cliente = actor(nombre);
        cliente.should(aceptada("la respuesta de Bookify"));
        barberia().should(seeThat(HistorialDelNegocio.paraLasCitasDe(cliente), contains("CONFIRMADA")));
    }

    @Entonces("Bookify rechaza la reserva de {cliente} porque el horario ya no está disponible")
    public void rechazaPorNoDisponible(String nombre) {
        actor(nombre).should(rechazoPor(Motivo.HORARIO_NO_DISPONIBLE));
    }

    @Entonces("Bookify rechaza la reserva de {cliente} porque el horario ya comenzó")
    public void rechazaPorHorarioComenzado(String nombre) {
        actor(nombre).should(rechazoPor(Motivo.HORARIO_YA_COMENZO));
    }

    @Entonces("Bookify rechaza la solicitud o registra la cita a nombre de {cliente}")
    public void rechazaORegistraANombreDe(String nombre) {
        Actor cliente = actor(nombre);
        Sesion sesion = cliente.recall(Memoria.SESION);
        cliente.should(seeThat(Respuesta.rechazoOReservaDe(sesion), equalTo(Respuesta.CUMPLE)));
    }

    @Entonces("el horario conserva una única reserva, a nombre de {cliente}")
    public void conservaUnaUnicaReserva(String nombre) {
        Sesion sesion = actor(nombre).recall(Memoria.SESION);
        barberia().should(seeThat(TitularesDelHorario.reservado(), contains(sesion.nombreCompleto())));
    }

    @Entonces("{cliente} no tiene ninguna cita en ese horario")
    public void noTieneCita(String nombre) {
        barberia().should(seeThat(HistorialDelNegocio.paraLasCitasDe(actor(nombre)), empty()));
    }
}
