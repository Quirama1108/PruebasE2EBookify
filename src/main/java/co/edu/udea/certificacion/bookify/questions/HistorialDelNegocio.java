package co.edu.udea.certificacion.bookify.questions;

import co.edu.udea.certificacion.bookify.interactions.Get;
import co.edu.udea.certificacion.bookify.models.Horario;
import co.edu.udea.certificacion.bookify.models.Sesion;
import co.edu.udea.certificacion.bookify.utils.Memoria;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

import java.util.Collection;
import java.util.List;

/** Estados de las citas del cliente, de apellido único, en el historial del negocio el día del horario. */
public class HistorialDelNegocio implements Question<Collection<String>> {

    private final Actor cliente;

    public HistorialDelNegocio(Actor cliente) { this.cliente = cliente; }

    @Override
    public Collection<String> answeredBy(Actor actor) {
        Sesion sesion = actor.recall(Memoria.SESION);
        Sesion sesionCliente = cliente.recall(Memoria.SESION);
        Horario horario = actor.recall(Memoria.HORARIO);
        actor.attemptsTo(Get.resource("/comercio/reservas").with(peticion -> peticion
                .header("Authorization", sesion.autorizacion())
                .queryParam("desde", horario.dia().toString())
                .queryParam("hasta", horario.dia().toString())));
        List<String> estados = SerenityRest.lastResponse().then().statusCode(200).extract()
                .path("reservas.findAll { it.cliente == '" + sesionCliente.nombreCompleto() + "' }.estado");
        return estados;
    }

    public static HistorialDelNegocio paraLasCitasDe(Actor cliente) { return new HistorialDelNegocio(cliente); }
}
