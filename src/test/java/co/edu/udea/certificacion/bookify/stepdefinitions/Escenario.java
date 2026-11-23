package co.edu.udea.certificacion.bookify.stepdefinitions;

import co.edu.udea.certificacion.bookify.models.Horario;
import co.edu.udea.certificacion.bookify.models.Negocio;
import co.edu.udea.certificacion.bookify.tasks.IniciarSesion;
import co.edu.udea.certificacion.bookify.tasks.RegistrarseComoCliente;
import co.edu.udea.certificacion.bookify.utils.Configuracion;
import co.edu.udea.certificacion.bookify.utils.Memoria;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.ParameterType;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.Cast;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

/** Reparto nuevo en cada escenario; cada actor entra con su propia sesión la primera vez que participa. */
public class Escenario {

    @Before
    public void abrirTelon() {
        OnStage.setTheStage(Cast.whereEveryoneCan(CallAnApi.at(Configuracion.urlApi())));
    }

    @After
    public void cerrarTelon() { OnStage.drawTheCurtain(); }

    @ParameterType("Ana|Luis")
    public String cliente(String nombre) { return nombre; }

    static Actor actor(String nombre) {
        Actor actor = OnStage.theActorCalled(nombre);
        if (actor.recall(Memoria.SESION) == null) {
            actor.wasAbleTo(RegistrarseComoCliente.nuevo());
        }
        return actor;
    }

    static Actor negocio(Negocio negocio) {
        Actor actor = OnStage.theActorCalled(negocio.nombre());
        if (actor.recall(Memoria.SESION) == null) {
            actor.wasAbleTo(IniciarSesion.como(negocio));
        }
        return actor;
    }

    static Actor barberia() { return negocio(Negocio.BARBERIA); }

    static Horario horario() { return barberia().recall(Memoria.HORARIO); }
}
