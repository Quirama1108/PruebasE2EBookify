package co.edu.udea.certificacion.bookify.tasks;

import co.edu.udea.certificacion.bookify.models.Horario;
import co.edu.udea.certificacion.bookify.models.Momento;
import co.edu.udea.certificacion.bookify.models.Negocio;
import co.edu.udea.certificacion.bookify.utils.Memoria;
import co.edu.udea.certificacion.bookify.utils.connetiondb.BaseDatos;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/** Precondición en base de datos: horario libre con recurso propio (la API no guarda el recurso, #187). */
public class PrepararHorario implements Task {

    private final Momento momento;

    public PrepararHorario(Momento momento) { this.momento = momento; }

    @Override
    @Step("{0} tiene un horario libre para su servicio")
    public <T extends Actor> void performAs(T actor) {
        Negocio negocio = Negocio.llamado(actor.getName());
        OffsetDateTime inicio = momento == null ? enDiaLibre(actor) : momento.inicio();
        OffsetDateTime fin = inicio.plusHours(1);
        String recurso = BaseDatos.qa().executeQuery("""
                INSERT INTO tbl_recurso(id_proveedor, id_tipo_recurso, nombre)
                SELECT ?::uuid, id_tipo_recurso, ? FROM tbl_tipo_recurso WHERE codigo = 'HUMANO'
                RETURNING id_recurso""",
                negocio.proveedor(), "Recurso " + UUID.randomUUID()).get(0).get("id_recurso");
        String id = BaseDatos.qa().executeQuery("""
                INSERT INTO tbl_disponibilidad(id_agenda_servicio, id_estado_disponibilidad, inicio_at, fin_at, id_recurso)
                SELECT ?::uuid, id_estado_disponibilidad, ?, ?, ?::uuid
                  FROM tbl_estado_disponibilidad WHERE codigo = 'DISPONIBLE'
                RETURNING id_disponibilidad""",
                negocio.agenda(), inicio, fin, recurso).get(0).get("id_disponibilidad");
        actor.remember(Memoria.HORARIO, new Horario(id, inicio, fin));
    }

    private OffsetDateTime enDiaLibre(Actor actor) {
        actor.attemptsTo(ElegirDiaLibre.enSuAgenda());
        LocalDate dia = actor.recall(Memoria.DIA);
        return dia.atTime(10, 0).atZone(Horario.BOGOTA).toOffsetDateTime();
    }

    public static PrepararHorario futuro() { return instrumented(PrepararHorario.class, (Momento) null); }

    public static PrepararHorario enCurso() { return instrumented(PrepararHorario.class, Momento.EN_CURSO); }
}
