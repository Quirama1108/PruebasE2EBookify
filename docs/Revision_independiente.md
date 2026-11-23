# Revisión independiente de la suite E2E, Sprint 3

Fecha: 23/11/2026. Revisado: `3f6c97c` más los cambios sin commit de `docs/` (matriz, plan e informe) y `evidencias/local`. El revisor no escribió la suite. Rutas relativas a `e2e/`; las clases están en `src/*/java/co/edu/udea/certificacion/bookify/`.

**Veredicto:** aprobada con correcciones. Los 18 `@aceptacion` son de fiar: entre ellos detectaron los 15 defectos inyectados. Los 4 `@defecto` fallan por el producto. T3 tiene un oráculo débil demostrado, y el informe tiene dos datos que no coinciden con la evidencia.

## 1. Prueba de mutación

Copia del producto (`5b2e445`) en 8092 con base propia en `bookify_qa`; suite en una copia, con `-Dbookify.api.url` y `-Ddb.NAME_DATABASE`. Control sin mutar: 18 pasan y 4 fallan, igual que lo reportado. Fueron 23 corridas seguidas sobre la misma base, sin limpiar.

| Mutación | Resultado |
|---|---|
| M1: no valida que esté disponible (la base lo frena con un 500) | Detectada por T2 |
| M2: M1 y sin ocupación (la base lo frena con un 500) | Detectada por T2 |
| M22: doble reserva real, la de Luis se acepta | Detectada por T2 y T1 |
| M3: la doble reserva responde 500 | Detectada por T2: un 500 no cuenta como rechazo |
| M4: no valida el cruce de horarios | Detectada: 5 de 5 casos |
| M5: responde 409 pero guarda el horario | Detectada: 5 de 5 |
| M6: rechaza horarios contiguos | Detectada por el valor límite |
| M7: permite asistencia anticipada | Detectada: 4 de 4 |
| M8: compara con el inicio y no con el fin | Detectada: 2 de 2 «en curso» |
| M9: no valida el negocio en asistencia | Detectada por HU-17.5 |
| M12: responde 403 pero cambia el estado | Detectada por HU-17.5 |
| M10: permite clasificar canceladas | Detectada: 2 de 2 |
| M11: no guarda el registro de la operación | Detectada por T1 y T2 |
| M13: el horario sigue en la oferta | Detectada por T1 y T2 |
| M21: guarda «atendida» al marcar «no asistió» | Detectada por T2 |
| **M19: toda reserva responde 400 «Solicitud invalida.»** | **T3 pasa a verde: falso positivo.** T1, T2 y siete casos de HU-17 sí fallan |

Correcciones simuladas, para ver si los `@defecto` se ponen en verde:

| Corrección | Resultado |
|---|---|
| M14, #291: titular tomado del token | T3 en verde, correcto |
| M15, #189: 403 «La agenda no pertenece a su comercio.» | T4 en verde, correcto |
| M16, #187: guardar el recurso al publicar | T5 en verde, correcto |
| M18, #183: 400 «El horario ya inicio.» | En verde, correcto |
| **M17, #183: 400 «El inicio del horario debe ser posterior a la hora actual.»** | **Sigue en rojo: falso negativo** |
| **M20, #189: 403 «No tiene permiso para usar esta agenda.»** | **T4 sigue en rojo: falso negativo** |

## 2. Hallazgos

### Deben corregirse antes de entregar

| # | Severidad | Evidencia | Recomendación |
|---|---|---|---|
| H1 | Alta | `questions/Respuesta.java:39`: `rechazoOReservaDe` acepta cualquier 4xx salvo 401, sin motivo (400 de validación, 404, 403 vacío). Lo demuestra M19. Contradice el informe §9 y el README («cuyo mensaje explique el motivo») | Exigir 403, o un motivo de identidad, o un 201 propio. Si es propio, comprobar además que el titular guardado es Luis (`TitularesDelHorario`) |
| H2 | Alta | Informe §6: falta la fila HU-03.1b (T5, #187), que sí está en la matriz | Agregarla. Decir en la conclusión que se **automatizan** 9 de 9 pero HU-01.1 y HU-03.1 se cumplen solo en parte |
| H3 | Alta | Informe §10 y matriz: «solo se reprodujo #187» y «no se probaron #183, #189 ni #291 en Render». En `evidencias/render/resultado.json` un proveedor recién creado publica (201) en la agenda del Sprint 1 (`sonda_render.py:5`, `:33`): es el comportamiento de #189 | Confirmar el dueño de la agenda y declarar que la sonda también lo mostró. Declarar lo que quedó en Render: dos cuentas y el horario del 28/09/2027 |
| H4 | Media | `models/Motivo.java:10` y `:13`: el texto exigido es más estricto que el bug. #183 pide 400 y #189 pide 403; ninguno fija texto. M17 y M20 seguirían en rojo tras una corrección válida | #183: aceptar «posterior». #189: aceptar el 403; `sigueSinHorarios` ya comprueba el estado. O declararlo como limitación |
| H5 | Media | Los tres «hallazgos adicionales» (matriz e informe §8.2) dicen «reproducidos» y no hay archivo en `evidencias/` | Guardar petición y respuesta. Este revisor reprodujo cancelar (500; el horario queda sin recurso) y servicio de otro negocio (500; trigger «El servicio no pertenece al comercio del recurso»; HU-13 pide 400 con ese texto). El historial no se comprobó |

### Recomendados

| # | Severidad | Evidencia | Recomendación |
|---|---|---|---|
| H6 | Media | HU-13 (Azure #148) dice «Los horarios disponibles se registran por recurso, no por servicio» y «Cambia el comportamiento de HU-01». La suite fija el cruce por agenda (HU-01.2a) y publica sin recurso (`tasks/PublicarHorario.java:45`). Si #187 se corrige exigiendo el recurso, HU-01, T4 y T5 fallarían por la prueba. No está en «Ambigüedades» | Declararlo |
| H7 | Media | El criterio de HU-13 que cita #189 habla de un **recurso** ajeno; T4 usa una **agenda** ajena | Precisar en HU-01.1d que es analogía |
| H8 | Media | T3 se apoya en «estoy autenticado como cliente». HU-07 (Azure #92) lo dice mejor: «Incluye tomar el usuario del token en HU-01, HU-03 y HU-05» | Citar HU-07 y la tarea #97 |
| H9 | Media | `Resumen_ejecucion.md` y `logs/*.json` guardan solo «Expected: …» (`evidencias/local/parse.py:12`). El valor obtenido solo está en los logs aleatorios y en Serenity | Agregar «but: was» y guardar el registro de la API, porque la causa de T5 no se ve en la evidencia |
| H10 | Media | `evidencias/local` (Serenity pesa 19 MB) y el informe estaban sin versionar al revisar | Incluirlos en la entrega |
| H11 | Media | HU-03.1a y HU-17 pasan solo con horario sembrado por JDBC. Está declarado, pero `POST /disponibilidades` es la única ruta de publicación y no produce un horario reservable | Repetirlo junto a «18 pasan» en la conclusión |
| H12 | Baja | `models/Motivo.java:9` acepta «La disponibilidad no existe o ya no puede reservarse», que es otro caso | Quitar «ya no puede reservarse» |
| H13 | Baja | `models/Motivo.java:11-12`: HU-17.3 y HU-17.4 solo piden rechazar; la prueba exige el motivo | Declararlo |
| H14 | Baja | `stepdefinitions/DisponibilidadStepDefinitions.java:112`: `not(hasItem(horario()))` compara id y horas; si la API cambia las horas, pasa | Comparar por id |
| H15 | Baja | `questions/HistorialDelNegocio.java:30` y `OfertaDeHorarios.java:25`: `then().statusCode(200)` en la Question da AssertionError, igual que una falla de negocio | Lanzar otro tipo de error |
| H16 | Baja | La base se usa por `BaseDatos.qa()` estático, no como Ability. Pasos que llaman a otros pasos y afirman (`ReservaStepDefinitions.java:61-84`); `ofertaConsultada` (`:36`) no es memoria del actor | Mejora opcional |
| H17 | Baja | Sin casos sin sesión o con rol equivocado (HU-07). No se prueba el cruce entre servicios distintos | Declararlo fuera de alcance |

## 3. Los defectos fallan por el producto

- **T3:** 201 con la cita a nombre de Ana. Respaldo: #291 y HU-07.
- **T4:** 201. Respaldo: #189 (ver H7).
- **T5:** 500; el registro de la API de control dice «No fue posible validar el servicio y el recurso de la reserva». Con M16 pasa completo. Respaldo: HU-01.1 y HU-03.1. La reproducción original de #187, sin servicio, también responde 500; la suite no la cubre (declarado).
- **#183:** aceptada. Ningún criterio de HU-03 lo dice de forma literal; el respaldo es el bug (declarado en HU-03.1d).

## 4. Cobertura

Los 9 criterios leídos en Azure (#2, #5, #201) coinciden con la matriz y cada uno tiene escenario. 13 escenarios y 22 casos (18 y 4): verificado en los features.

| Criterio | Escenarios | ¿Se cumple? |
|---|---|---|
| HU-01.1 | Horario válido, horas no válidas, T4, T5 | En parte (#187, #189) |
| HU-01.2 | Cinco cruces y contiguo | Sí |
| HU-03.1 | T1, T3, T5, horario comenzado | En parte (#187, #183, #291) |
| HU-03.2 | T2 | Sí, en secuencia |
| HU-17.1 a HU-17.5 | T1, T2, cuatro, dos y un caso | Sí, con horario sembrado |

## 5. FIRST

| Letra | Juicio | Evidencia |
|---|---|---|
| Rápidas | Cumple | `logs/*.json`: máximo 3.44 s (T2 sola); suma de 12.9 a 14.0 s |
| Independientes | Cumple | Semillas 11, 222 y 3333; T1 a T5 solas. Los 8 escenarios cortos no se corrieron solos. Solo en serie (`ElegirDiaLibre.java:14`) |
| Repetibles | Cumple | c1 a c3 y 23 corridas de esta revisión sin limpiar |
| Autoverificables | Cumple, salvo H1 | Mutaciones |
| Oportunas | Débil | Features 15:23 y código desde 15:30, según la fecha de los archivos. Git no lo prueba: todo entra en `3f6c97c` |

## 6. Lenguaje, Screenplay y scaffold

- Features sin URL, JSON, HTTP, ids ni base de datos. Cumple.
- Paquetes del scaffold respetados y cambios listados en `README.md`. Quedan tres clases de muestra vacías (`Exception`, `Integration`, `UserInterface`), declaradas. No se regeneró el scaffold para comparar.

## 7. Documentos frente a lo ejecutado

Las 13 corridas de `Resumen_ejecucion.md` coinciden con `logs/*.json` en totales, códigos y tiempos. `c1.json` se regeneró a las 15:43:21 (c1 terminó a las 15:42:31), tras corregir `parse.py`; sus conteos coinciden con `c1.log`. En Serenity no se encontraron tokens ni claves. Matriz y plan coinciden, salvo H3 y H5. El informe no coincide en H2 y H3.

## 8. No comprobado

Render, concurrencia, historial del cliente (HU-05), regeneración del scaffold, dueño de la agenda usada en Render y mutaciones sobre los triggers de la base.

## Nota del coordinador, 23/11/2026

Atendidos: H1 a H5 (el dueño de la agenda de Render sigue sin confirmar), H6 a H8 (declarados en el informe), H9 (obtenido frente a esperado; el log de la API solo para los adicionales), H10 y H11. H13 y H17, declarados. Pendientes, menores: H12, H14, H15 y H16.
