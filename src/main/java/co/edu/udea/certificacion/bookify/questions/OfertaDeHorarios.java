package co.edu.udea.certificacion.bookify.questions;

import co.edu.udea.certificacion.bookify.interactions.Get;
import co.edu.udea.certificacion.bookify.models.Horario;
import co.edu.udea.certificacion.bookify.models.Negocio;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/** Horarios que la oferta pública muestra para el servicio de un negocio. */
public class OfertaDeHorarios implements Question<Collection<Horario>> {

    private final Negocio negocio;

    public OfertaDeHorarios(Negocio negocio) { this.negocio = negocio; }

    @Override
    public Collection<Horario> answeredBy(Actor actor) {
        actor.attemptsTo(Get.resource("/catalog/services"));
        List<Map<String, Object>> oferta = SerenityRest.lastResponse().then().statusCode(200).extract().path("$");
        return oferta.stream()
                .filter(servicio -> negocio.servicio().equals(servicio.get("serviceId")))
                .flatMap(servicio -> ((List<Map<String, String>>) servicio.get("availableSchedules")).stream())
                .map(h -> new Horario(h.get("availabilityId"), enBogota(h.get("inicioAt")), enBogota(h.get("finAt"))))
                .toList();
    }

    private static OffsetDateTime enBogota(String instante) {
        return OffsetDateTime.parse(instante).atZoneSameInstant(Horario.BOGOTA).toOffsetDateTime();
    }

    public static OfertaDeHorarios de(Negocio negocio) { return new OfertaDeHorarios(negocio); }
}
