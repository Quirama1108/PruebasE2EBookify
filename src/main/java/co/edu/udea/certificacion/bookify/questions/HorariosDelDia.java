package co.edu.udea.certificacion.bookify.questions;

import co.edu.udea.certificacion.bookify.models.Negocio;
import co.edu.udea.certificacion.bookify.utils.Memoria;
import co.edu.udea.certificacion.bookify.utils.connetiondb.BaseDatos;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

import java.time.LocalDate;
import java.util.Collection;

/** Horarios guardados en la agenda del negocio el día del escenario, como "10:00-11:00 DISPONIBLE". */
public class HorariosDelDia implements Question<Collection<String>> {

    @Override
    public Collection<String> answeredBy(Actor actor) {
        LocalDate dia = actor.recall(Memoria.DIA);
        return BaseDatos.qa().executeQuery("""
                SELECT to_char(d.inicio_at AT TIME ZONE 'America/Bogota', 'HH24:MI') || '-' ||
                       to_char(d.fin_at AT TIME ZONE 'America/Bogota', 'HH24:MI') || ' ' || e.codigo AS horario
                  FROM tbl_disponibilidad d JOIN tbl_estado_disponibilidad e USING (id_estado_disponibilidad)
                 WHERE d.id_agenda_servicio = ?::uuid
                   AND (d.inicio_at AT TIME ZONE 'America/Bogota')::date = ?""",
                Negocio.llamado(actor.getName()).agenda(), dia)
                .stream().map(fila -> fila.get("horario")).toList();
    }

    public static HorariosDelDia publicados() { return new HorariosDelDia(); }
}
