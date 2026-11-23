package co.edu.udea.certificacion.bookify.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.rest.interactions.RestInteraction;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.rest.abilities.CallAnApi.as;

public class Patch extends RestInteraction {

    private final String resource;

    public Patch(String resource) {
        this.resource = resource;
    }

    @Step("{0} executes a PATCH on the resource #resource")
    @Override
    public <T extends Actor> void performAs(T actor) { rest().patch(as(actor).resolve(resource)); }

    public static Patch to(String resource) { return instrumented(Patch.class, resource); }

}