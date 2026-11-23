package co.edu.udea.certificacion.bookify.stepdefinitions;

import co.edu.udea.certificacion.bookify.models.Horario;
import co.edu.udea.certificacion.bookify.models.Motivo;
import co.edu.udea.certificacion.bookify.models.Negocio;
import co.edu.udea.certificacion.bookify.questions.HorariosDelDia;
import co.edu.udea.certificacion.bookify.questions.OfertaDeHorarios;
import co.edu.udea.certificacion.bookify.questions.Respuesta;
import co.edu.udea.certificacion.bookify.tasks.ElegirDiaLibre;
import co.edu.udea.certificacion.bookify.tasks.PrepararHorario;
import co.edu.udea.certificacion.bookify.tasks.PublicarHorario;
import co.edu.udea.certificacion.bookify.utils.Memoria;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Consequence;

import java.time.LocalTime;

import static co.edu.udea.certificacion.bookify.stepdefinitions.Escenario.barberia;
import static co.edu.udea.certificacion.bookify.stepdefinitions.Escenario.actor;
import static co.edu.udea.certificacion.bookify.stepdefinitions.Escenario.horario;
import static co.edu.udea.certificacion.bookify.stepdefinitions.Escenario.negocio;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

public class DisponibilidadStepDefinitions {

    @Dado("que la Barbería tiene un día sin horarios publicados")
    public void tieneUnDiaSinHorarios() {
        barberia().wasAbleTo(ElegirDiaLibre.enSuAgenda());
    }

    @Dado("que la Barbería tiene un horario libre para su servicio")
    public void tieneUnHorarioLibre() {
        barberia().wasAbleTo(PrepararHorario.futuro());
    }

    @Dado("que la Barbería tiene un horario sin reservar que ya comenzó")
    public void tieneUnHorarioQueYaComenzo() {
        barberia().wasAbleTo(PrepararHorario.enCurso());
    }

    @Dado("la Barbería publicó ese día un horario de {string} a {string}")
    public void publicoUnHorario(String inicio, String fin) {
        publica(inicio, fin);
        barberia().should(seeThat("Precondición: el horario quedó publicado", Respuesta.recibida(), equalTo("ACEPTADA")));
    }

    @Cuando("la Barbería publica ese día un horario de {string} a {string}")
    @Cuando("la Barbería intenta publicar ese día un horario de {string} a {string}")
    public void publica(String inicio, String fin) {
        Actor barberia = barberia();
        barberia.attemptsTo(PublicarHorario.enLaAgendaDe(
                Negocio.BARBERIA, barberia.recall(Memoria.DIA), inicio, fin));
    }

    @Cuando("el Spa intenta publicar un horario ese día en la agenda de la Barbería")
    public void elSpaPublicaEnAgendaAjena() {
        negocio(Negocio.SPA).attemptsTo(PublicarHorario.enLaAgendaDe(
                Negocio.BARBERIA, barberia().recall(Memoria.DIA), "10:00", "11:00"));
    }

    @Entonces("el horario de {string} a {string} queda guardado como disponible")
    public void quedaGuardado(String inicio, String fin) {
        barberia().should(
                seeThat(Respuesta.recibida(), equalTo("ACEPTADA")),
                seeThat(HorariosDelDia.publicados(), hasItem(inicio + "-" + fin + " DISPONIBLE")));
    }

    @Entonces("los clientes ven el horario de {string} a {string} en la oferta de la Barbería")
    public void losClientesVenElHorario(String inicio, String fin) {
        Horario publicado = horario();
        Horario esperado = publicado.trasladadoA(
                publicado.dia().atTime(LocalTime.parse(inicio)).atZone(Horario.BOGOTA).toOffsetDateTime(),
                publicado.dia().atTime(LocalTime.parse(fin)).atZone(Horario.BOGOTA).toOffsetDateTime());
        actor("Ana").should(seeThat(OfertaDeHorarios.de(Negocio.BARBERIA), hasItem(esperado)));
    }

    @Entonces("Bookify rechaza la publicación e informa un conflicto de horarios")
    public void rechazaPorConflicto() {
        barberia().should(rechazoPor(Motivo.CRUCE_DE_HORARIOS));
    }

    @Entonces("Bookify rechaza la publicación porque las horas no son válidas")
    public void rechazaPorHoras() {
        barberia().should(rechazoPor(Motivo.HORAS_NO_VALIDAS));
    }

    @Entonces("Bookify rechaza la publicación porque la agenda es de otro negocio")
    public void rechazaPorAgendaAjena() {
        negocio(Negocio.SPA).should(rechazoPor(Motivo.DE_OTRO_NEGOCIO));
    }

    @Entonces("ese día la Barbería conserva únicamente el horario de {string} a {string}")
    public void conservaUnicamente(String inicio, String fin) {
        barberia().should(seeThat(HorariosDelDia.publicados(), contains(inicio + "-" + fin + " DISPONIBLE")));
    }

    @Entonces("la Barbería sigue sin horarios publicados ese día")
    public void sigueSinHorarios() {
        barberia().should(seeThat(HorariosDelDia.publicados(), empty()));
    }

    @Entonces("ese horario ya no aparece en la oferta de la Barbería")
    public void yaNoApareceEnLaOferta() {
        actor("Ana").should(seeThat(OfertaDeHorarios.de(Negocio.BARBERIA), not(hasItem(horario()))));
    }

    static Consequence<String> rechazoPor(Motivo motivo) {
        return seeThat(Respuesta.rechazoPor(motivo), equalTo(Respuesta.rechazo(motivo)));
    }
}
