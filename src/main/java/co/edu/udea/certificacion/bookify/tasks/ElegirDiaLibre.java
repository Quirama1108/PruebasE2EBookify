package co.edu.udea.certificacion.bookify.tasks;

import co.edu.udea.certificacion.bookify.models.Negocio;
import co.edu.udea.certificacion.bookify.utils.Memoria;
import co.edu.udea.certificacion.bookify.utils.connetiondb.BaseDatos;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

import java.time.LocalDate;

import static net.serenitybdd.screenplay.Tasks.instrumented;

// shortcut: el día siguiente al último publicado solo es propio si los escenarios van en serie; reservar días antes de paralelizar.
public class ElegirDiaLibre implements Task {

    @Override
    @Step("{0} elige un día sin horarios publicados")
    public <T extends Actor> void performAs(T actor) {
        String dia = BaseDatos.qa().executeQuery("""
                SELECT (greatest(now(), max(fin_at)) AT TIME ZONE 'America/Bogota')::date + 1 AS dia
                  FROM tbl_disponibilidad WHERE id_agenda_servicio = ?::uuid""",
                Negocio.llamado(actor.getName()).agenda()).get(0).get("dia");
        actor.remember(Memoria.DIA, LocalDate.parse(dia));
    }

    public static ElegirDiaLibre enSuAgenda() { return instrumented(ElegirDiaLibre.class); }
}
