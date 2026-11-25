package co.edu.udea.certificacion.bookify.stepdefinitions;

import co.edu.udea.certificacion.bookify.models.Cuenta;
import co.edu.udea.certificacion.bookify.models.Horario;
import co.edu.udea.certificacion.bookify.models.Motivo;
import co.edu.udea.certificacion.bookify.models.Negocio;
import co.edu.udea.certificacion.bookify.questions.Respuesta;
import co.edu.udea.certificacion.bookify.tasks.IniciarSesion;
import co.edu.udea.certificacion.bookify.tasks.RegistrarseComoCliente;
import co.edu.udea.certificacion.bookify.utils.Configuracion;
import co.edu.udea.certificacion.bookify.utils.Memoria;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.ParameterType;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Consequence;
import net.serenitybdd.screenplay.actors.Cast;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.equalTo;

/** Reparto nuevo en cada escenario; cada actor entra con su propia sesión la primera vez que participa. */
public class Escenario {

    private static final String CLIENTES = "Ana|Luis";

    @Before
    public void abrirTelon() {
        OnStage.setTheStage(Cast.whereEveryoneCan(CallAnApi.at(Configuracion.urlApi())));
    }

    @After
    public void cerrarTelon() { OnStage.drawTheCurtain(); }

    @ParameterType(CLIENTES)
    public String cliente(String nombre) { return nombre; }

    @ParameterType("la Barbería|el Spa")
    public String negocio(String nombre) { return nombre; }

    /** Única puerta a los actores: ningún paso crea el suyo. */
    static Actor actor(String nombre) {
        Actor actor = OnStage.theActorCalled(nombre);
        if (actor.recall(Memoria.SESION) == null) {
            entrar(actor, nombre.matches(CLIENTES) ? registrar(actor) : Cuenta.de(Negocio.llamado(nombre)));
        }
        return actor;
    }

    static Actor barberia() { return actor(Negocio.BARBERIA.nombre()); }

    static Horario horario() { return barberia().recall(Memoria.HORARIO); }

    static Consequence<String> aceptada(String descripcion) {
        return seeThat(descripcion, Respuesta.recibida(), equalTo("ACEPTADA"));
    }

    static Consequence<String> rechazoPor(Motivo motivo) {
        return seeThat(Respuesta.rechazoPor(motivo), equalTo(Respuesta.rechazo(motivo)));
    }

    private static Cuenta registrar(Actor actor) {
        Cuenta cuenta = Cuenta.nueva(actor.getName());
        actor.wasAbleTo(RegistrarseComoCliente.con(cuenta));
        actor.should(aceptada("Precondición: el registro"));
        return cuenta;
    }

    private static void entrar(Actor actor, Cuenta cuenta) {
        actor.wasAbleTo(IniciarSesion.con(cuenta));
        actor.should(aceptada("Precondición: el inicio de sesión"));
        // Sin esto, un paso posterior podría tomar el 200 del inicio de sesión como respuesta a su acción.
        actor.forget(Memoria.RESPUESTA);
    }
}
