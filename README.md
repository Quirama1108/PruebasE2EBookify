# Bookify E2E

Pruebas de punta a punta de la API con Screenplay, Serenity y Cucumber. Alcance y criterios: [docs/Plan_E2E.md](docs/Plan_E2E.md) y [docs/Matriz_trazabilidad.md](docs/Matriz_trazabilidad.md).

## Correr

Requisitos: JDK 17, Docker y Git Bash. Este repositorio trae solo las pruebas. Antes de correr:

1. Compile Bookify (commit `5b2e445`) con `./gradlew bootJar` y exporte `BOOKIFY_JAR` con la ruta del jar.
2. Genere `ambiente/01_esquema.sql`, que no se publica: ejecute `ambiente/extraer_esquema.sql` en Supabase, descargue el CSV y corra `python ambiente/generar_esquema.py <csv> ambiente`.

Desde la raíz:

```bash
export JAVA_HOME='C:\Program Files\Java\jdk-17'
export BOOKIFY_JAR=/ruta/a/bookify-0.0.1-SNAPSHOT.jar
bash ambiente/levantar.sh        # base limpia (127.0.0.1:5544) y API (8090)
./gradlew test                   # completo
./gradlew aceptacion             # @aceptacion
./gradlew defectos               # @defecto: deben fallar mientras el bug siga abierto
./gradlew test -Dcucumber.filter.tags=@T1
./gradlew aleatorio              # orden aleatorio; -Dsemilla=N lo repite
```

Reporte: `target/site/serenity/index.html` (cada corrida borra el anterior; `aleatorio` solo deja `build/aleatorio/resultado.xml`). `levantar.sh` borra los datos de `bookify_qa` y reutiliza la API si ya responde en 8090: tras recompilar el producto, deténgala antes. Su registro queda en `${TMPDIR:-/tmp}/bookify_qa_api.log`.

## Entregables

- [Informe E2E](docs/Informe_E2E_Sprint3.docx), [plan](docs/Plan_E2E.md), [matriz](docs/Matriz_trazabilidad.md) y [revisión independiente](docs/Revision_independiente.md)
- [Reporte de Quality Gates](docs/Reporte_Quality_Gates_Sprint3.md)
- [Resumen de ejecución](evidencias/local/Resumen_ejecucion.md) y reporte Serenity: descomprima `evidencias/local/reporte_serenity.zip` y abra `index.html`

## Configuración

Propiedad de sistema (`-D`) o variable de entorno; si faltan, se usa el valor local.

| Propiedad | Entorno | Local |
|---|---|---|
| `bookify.api.url` | `BOOKIFY_API_URL` | `http://localhost:8090/api/v1` |
| `bookify.negocios.clave` | `BOOKIFY_NEGOCIOS_CLAVE` | la ficticia de `03_semilla.sql` |
| `db.HOST`, `db.PORT`, `db.NAME_DATABASE`, `db.USER` | `DB_HOST`, … | `postgresql.properties` |
| `PASSPSQL` | `PASSPSQL` | `bookify_qa` |

## Decisiones

- **La cita termina:** se reserva un horario futuro y luego se traslada al pasado por JDBC (el horario y la ocupación del recurso, en una transacción). No se siembra un horario ya pasado porque reservarlo depende del bug #183 y la prueba se rompería al corregirlo.
- **Horario reservable (T1 a T3, HU-03, HU-17):** se siembra por JDBC con recurso propio, porque la API no lo guarda (#187). En T4, T5 y HU-01 se publica por la API.
- **Cita cancelada (HU-17.4):** se siembra por JDBC. Cancelar por la API responde 500: al confirmar se borra el recurso del horario y el trigger `fn_validar_reserva_servicio_recurso` rechaza la actualización («No fue posible validar el servicio y el recurso de la reserva»). Es una falla de HU-04, fuera del alcance de estos features.
- **Independencia:** clientes, recurso y día nuevos por escenario. El día es el siguiente al último de la agenda, así que los escenarios van en serie, en cualquier orden.
- Un rechazo exige respuesta 4xx distinta de 401 cuyo mensaje explique el motivo; un 500 nunca cuenta. Para otro negocio basta el 403; en T3 solo cuenta un 403 o la cita a nombre propio.

## Scaffold

Generado con `io.github.jumosqu12.screenplayarchitecture` 2.0.1 y Gradle 8.14: `spa` (REST), `gri` (Post, Get, Patch), `grun` (Aceptacion, Defectos, Completo), `gtk` (ReservarHorario) y `dbc` (PostgreSQL). Cambios a lo generado:

- Se agregó `net.serenity-bdd.serenity-gradle-plugin` 4.1.3 para el reporte (no existe la 4.1.0). Las demás versiones no cambiaron: Serenity sigue en 4.1.0.
- Se quitaron Lombok y `hamcrest-all` 1.3, sin uso (Serenity ya trae Hamcrest).
- `Patch` tenía constructor privado y Serenity no podía instanciarlo; ahora es público.
- `Post`, `Get` y `Patch` ya no escriben la petición completa en consola: incluía claves y tokens.
- `ExecuteQueryDb` acepta parámetros y conserva la causa del error; `PostgreSQLConnection` lee la configuración de arriba.
- Las clases de muestra vacías se borraron donde hay clases reales; quedan en `exceptions`, `integrations` y `userinterfaces` para conservar la estructura.
