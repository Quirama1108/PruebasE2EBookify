package co.edu.udea.certificacion.bookify.stepdefinitions;

import co.edu.udea.certificacion.bookify.models.Horario;
import co.edu.udea.certificacion.bookify.models.Motivo;
import co.edu.udea.certificacion.bookify.models.Negocio;
import co.edu.udea.certificacion.bookify.questions.HorariosDelDia;
import co.edu.udea.certificacion.bookify.questions.OfertaDeHorarios;
import co.edu.udea.certificacion.bookify.tasks.ElegirDiaLibre;
import co.edu.udea.certificacion.bookify.tasks.PrepararHorario;
import co.edu.udea.certificacion.bookify.tasks.PublicarHorario;
import co.edu.udea.certificacion.bookify.utils.Memoria;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.screenplay.Actor;

import java.time.LocalTime;

import static co.edu.udea.certificacion.bookify.stepdefinitions.Escenario.aceptada;
import static co.edu.udea.certificacion.bookify.stepdefinitions.Escenario.actor;
import static co.edu.udea.certificacion.bookify.stepdefinitions.Escenario.horario;
import static co.edu.udea.certificacion.bookify.stepdefinitions.Escenario.rechazoPor;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

public class DisponibilidadStepDefinitions {

    @Dado("que {negocio} tiene un día sin horarios publicados")
    public void tieneUnDiaSinHorarios(String negocio) {
        actor(negocio).wasAbleTo(ElegirDiaLibre.enSuAgenda());
    }

    @Dado("que {negocio} tiene un horario libre para su servicio")
    public void tieneUnHorarioLibre(String negocio) {
        actor(negocio).wasAbleTo(PrepararHorario.futuro());
    }

    @Dado("que {negocio} tiene un horario sin reservar que ya comenzó")
    public void tieneUnHorarioQueYaComenzo(String negocio) {
        actor(negocio).wasAbleTo(PrepararHorario.enCurso());
    }

    @Dado("{negocio} publicó ese día un horario de {string} a {string}")
    public void publicoUnHorario(String negocio, String inicio, String fin) {
        publica(negocio, inicio, fin);
        actor(negocio).should(aceptada("Precondición: el horario quedó publicado"));
    }

    @Cuando("{negocio} publica ese día un horario de {string} a {string}")
    @Cuando("{negocio} intenta publicar ese día un horario de {string} a {string}")
    public void publica(String negocio, String inicio, String fin) {
        Actor proveedor = actor(negocio);
        proveedor.attemptsTo(PublicarHorario.enLaAgendaDe(
                Negocio.llamado(negocio), proveedor.recall(Memoria.DIA), inicio, fin));
    }

    @Cuando("{negocio} intenta publicar un horario ese día en la agenda de {negocio}")
    public void publicaEnAgendaAjena(String negocio, String duenio) {
        actor(negocio).attemptsTo(PublicarHorario.enLaAgendaDe(
                Negocio.llamado(duenio), actor(duenio).recall(Memoria.DIA), "10:00", "11:00"));
    }

    @Entonces("el horario de {string} a {string} queda guardado como disponible en la agenda de {negocio}")
    public void quedaGuardado(String inicio, String fin, String negocio) {
        actor(negocio).should(
                aceptada("la respuesta de Bookify"),
                seeThat(HorariosDelDia.publicados(), hasItem(inicio + "-" + fin + " DISPONIBLE")));
    }

    @Entonces("{cliente} ve el horario de {string} a {string} en la oferta de la Barbería")
    public void veElHorario(String cliente, String inicio, String fin) {
        Horario publicado = horario();
        Horario esperado = publicado.trasladadoA(
                publicado.dia().atTime(LocalTime.parse(inicio)).atZone(Horario.BOGOTA).toOffsetDateTime(),
                publicado.dia().atTime(LocalTime.parse(fin)).atZone(Horario.BOGOTA).toOffsetDateTime());
        actor(cliente).should(seeThat(OfertaDeHorarios.de(Negocio.BARBERIA), hasItem(esperado)));
    }

    @Entonces("{negocio} recibe un rechazo por conflicto de horarios")
    public void rechazaPorConflicto(String negocio) {
        actor(negocio).should(rechazoPor(Motivo.CRUCE_DE_HORARIOS));
    }

    @Entonces("{negocio} recibe un rechazo porque las horas no son válidas")
    public void rechazaPorHoras(String negocio) {
        actor(negocio).should(rechazoPor(Motivo.HORAS_NO_VALIDAS));
    }

    @Entonces("{negocio} recibe un rechazo porque la agenda es de otro negocio")
    public void rechazaPorAgendaAjena(String negocio) {
        actor(negocio).should(rechazoPor(Motivo.DE_OTRO_NEGOCIO));
    }

    @Entonces("ese día {negocio} conserva únicamente el horario de {string} a {string}")
    public void conservaUnicamente(String negocio, String inicio, String fin) {
        actor(negocio).should(seeThat(HorariosDelDia.publicados(), contains(inicio + "-" + fin + " DISPONIBLE")));
    }

    @Entonces("{negocio} sigue sin horarios publicados ese día")
    public void sigueSinHorarios(String negocio) {
        actor(negocio).should(seeThat(HorariosDelDia.publicados(), empty()));
    }

    @Entonces("{cliente} ya no ve ese horario en la oferta de la Barbería")
    public void yaNoVeElHorario(String cliente) {
        actor(cliente).should(seeThat(OfertaDeHorarios.de(Negocio.BARBERIA), not(hasItem(horario()))));
    }
}
