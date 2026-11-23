package co.edu.udea.certificacion.bookify.tasks;

import co.edu.udea.certificacion.bookify.utils.Memoria;
import co.edu.udea.certificacion.bookify.utils.connetiondb.BaseDatos;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/** Precondición en base de datos: cancelar por la API responde 500 (ver README, hallazgo de HU-04). */
public class PrepararCancelacion implements Task {

    @Override
    @Step("{0} canceló su cita")
    public <T extends Actor> void performAs(T actor) {
        String cita = actor.recall(Memoria.CITA);
        BaseDatos.qa().executeQueryAffectRows("""
                DELETE FROM tbl_ocupacion_disponibilidad WHERE id_reserva = ?::uuid;
                INSERT INTO tbl_historial_estado_reserva(id_reserva, id_estado_anterior, id_estado_nuevo, motivo)
                SELECT r.id_reserva, r.id_estado_reserva, e.id_estado_reserva, 'Cancelación solicitada por el cliente'
                  FROM tbl_reserva r, tbl_estado_reserva e WHERE r.id_reserva = ?::uuid AND e.codigo = 'CANCELADA';
                UPDATE tbl_disponibilidad SET id_estado_disponibilidad =
                       (SELECT id_estado_disponibilidad FROM tbl_estado_disponibilidad WHERE codigo = 'DISPONIBLE')
                 WHERE id_disponibilidad = (SELECT id_disponibilidad FROM tbl_reserva WHERE id_reserva = ?::uuid);
                UPDATE tbl_reserva SET id_estado_reserva =
                       (SELECT id_estado_reserva FROM tbl_estado_reserva WHERE codigo = 'CANCELADA')
                 WHERE id_reserva = ?::uuid""", cita, cita, cita, cita);
    }

    public static PrepararCancelacion deSuCita() { return instrumented(PrepararCancelacion.class); }
}
