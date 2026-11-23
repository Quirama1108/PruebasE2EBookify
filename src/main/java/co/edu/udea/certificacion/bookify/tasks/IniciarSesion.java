package co.edu.udea.certificacion.bookify.tasks;

import co.edu.udea.certificacion.bookify.interactions.Post;
import co.edu.udea.certificacion.bookify.models.Negocio;
import co.edu.udea.certificacion.bookify.models.Sesion;
import co.edu.udea.certificacion.bookify.utils.Memoria;
import co.edu.udea.certificacion.bookify.utils.connetiondb.BaseDatos;
import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

import java.util.Map;

import static co.edu.udea.certificacion.bookify.utils.Configuracion.claveNegocios;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class IniciarSesion implements Task {

    private final String correo;
    private final String clave;
    private final String nombreCompleto;

    public IniciarSesion(String correo, String clave, String nombreCompleto) {
        this.correo = correo;
        this.clave = clave;
        this.nombreCompleto = nombreCompleto;
    }

    @Override
    @Step("{0} inicia sesión en Bookify")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Post.to("/auth/login").with(peticion -> peticion
                .contentType(ContentType.JSON)
                .body(Map.of("correo", correo, "password", clave))));
        String token = SerenityRest.lastResponse().then().statusCode(200).extract().path("accessToken");
        String idUsuario = BaseDatos.qa().executeQuery(
                "SELECT id_usuario FROM tbl_usuario WHERE correo = ?", correo).get(0).get("id_usuario");
        actor.remember(Memoria.SESION, new Sesion(idUsuario, nombreCompleto, token));
    }

    public static IniciarSesion como(Negocio negocio) {
        return instrumented(IniciarSesion.class, negocio.correo(), claveNegocios(), negocio.nombre());
    }

    public static IniciarSesion con(String correo, String clave, String nombreCompleto) {
        return instrumented(IniciarSesion.class, correo, clave, nombreCompleto);
    }
}
