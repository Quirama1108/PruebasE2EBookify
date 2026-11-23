package co.edu.udea.certificacion.bookify.questions;

import co.edu.udea.certificacion.bookify.models.EstadoCita;
import co.edu.udea.certificacion.bookify.utils.connetiondb.BaseDatos;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

import java.util.Collection;

/** Cambios de asistencia guardados para una cita: estado anterior, nuevo, usuario y si la fecha es reciente. */
public class RegistroDeAsistencia implements Question<Collection<String>> {

    private final String cita;

    public RegistroDeAsistencia(String cita) { this.cita = cita; }

    @Override
    public Collection<String> answeredBy(Actor actor) {
        return BaseDatos.qa().executeQuery("""
                SELECT concat_ws(' ', a.codigo, n.codigo, h.id_usuario_cambio,
                       CASE WHEN h.fecha_cambio BETWEEN now() - interval '5 minutes' AND now() + interval '1 minute'
                            THEN 'reciente' ELSE 'fecha incorrecta' END) AS cambio
                  FROM tbl_historial_estado_reserva h
                  JOIN tbl_estado_reserva n ON n.id_estado_reserva = h.id_estado_nuevo
                  LEFT JOIN tbl_estado_reserva a ON a.id_estado_reserva = h.id_estado_anterior
                 WHERE h.id_reserva = ?::uuid AND n.codigo IN ('ATENDIDA', 'NO_ASISTIO')""", cita)
                .stream().map(fila -> fila.get("cambio")).toList();
    }

    public static RegistroDeAsistencia de(String cita) { return new RegistroDeAsistencia(cita); }

    public static String cambio(EstadoCita anterior, EstadoCita nuevo, String idUsuario) {
        return anterior + " " + nuevo + " " + idUsuario + " reciente";
    }
}
