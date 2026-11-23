package co.edu.udea.certificacion.bookify.questions;

import co.edu.udea.certificacion.bookify.models.Horario;
import co.edu.udea.certificacion.bookify.utils.Memoria;
import co.edu.udea.certificacion.bookify.utils.connetiondb.BaseDatos;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

import java.util.Collection;

/** Nombre del titular de cada reserva que tiene el horario del negocio. */
public class TitularesDelHorario implements Question<Collection<String>> {

    @Override
    public Collection<String> answeredBy(Actor actor) {
        Horario horario = actor.recall(Memoria.HORARIO);
        return BaseDatos.qa().executeQuery("""
                SELECT u.nombres || ' ' || u.apellidos AS titular
                  FROM tbl_reserva r JOIN tbl_usuario u ON u.id_usuario = r.id_cliente
                 WHERE r.id_disponibilidad = ?::uuid""", horario.id())
                .stream().map(fila -> fila.get("titular")).toList();
    }

    public static TitularesDelHorario reservado() { return new TitularesDelHorario(); }
}
