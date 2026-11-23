package co.edu.udea.certificacion.bookify.tasks;

import co.edu.udea.certificacion.bookify.models.Horario;
import co.edu.udea.certificacion.bookify.models.Momento;
import co.edu.udea.certificacion.bookify.utils.Memoria;
import co.edu.udea.certificacion.bookify.utils.connetiondb.BaseDatos;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

import java.time.OffsetDateTime;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/** Simula el paso del tiempo: mueve en la base el horario del negocio y la ocupación de su recurso. */
public class TrasladarCita implements Task {

    private final Momento momento;

    public TrasladarCita(Momento momento) { this.momento = momento; }

    @Override
    @Step("Pasa el tiempo y la cita queda #momento")
    public <T extends Actor> void performAs(T actor) {
        Horario horario = actor.recall(Memoria.HORARIO);
        OffsetDateTime inicio = momento.inicio();
        OffsetDateTime fin = inicio.plusHours(1);
        BaseDatos.qa().executeQueryAffectRows("""
                UPDATE tbl_disponibilidad SET inicio_at = ?, fin_at = ? WHERE id_disponibilidad = ?::uuid;
                UPDATE tbl_ocupacion_recurso o SET inicio_at = ?, fin_at = ? FROM tbl_reserva r
                 WHERE r.id_reserva = o.id_reserva AND r.id_disponibilidad = ?::uuid""",
                inicio, fin, horario.id(), inicio, fin, horario.id());
        actor.remember(Memoria.HORARIO, horario.trasladadoA(inicio, fin));
    }

    public static TrasladarCita a(Momento momento) { return instrumented(TrasladarCita.class, momento); }
}
