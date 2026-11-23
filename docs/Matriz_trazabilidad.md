# Matriz de trazabilidad E2E, Sprint 3

Criterios leídos en Azure DevOps el 23/11/2026: HU-01 (#2), HU-03 (#5) y HU-17 (#201).
«Resultado esperado» es lo que pide Azure o el bug citado. «Resultado real» es lo medido en local el 23/11/2026 (código `5b2e445`, 6 corridas completas, orden aleatorio y cada transacción por separado, siempre igual). Detalle en `evidencias/local/Resumen_ejecucion.md`. En los casos de defecto no se evalúan las afirmaciones posteriores a la primera falla.

## HU-01 Registrar disponibilidad del proveedor

| Id | Criterio textual de Azure | Escenario | Etiquetas | Resultado esperado | Resultado real | Bug |
|---|---|---|---|---|---|---|
| HU-01.1a | «When registro una fecha con hora inicial y hora final válidas Then el sistema guarda el horario como disponible And los clientes pueden verlo […]» | Un horario válido queda disponible y los clientes lo ven | `@HU-01 @aceptacion` | Guardado como disponible y visible en la oferta | Pasa | Ninguno |
| HU-01.1b | «[…] como reservable» | T5: Ana reserva un horario que la Barbería acaba de publicar | `@T5 @HU-01 @HU-03 @defecto @bug-187` | La reserva queda confirmada | Falla: la reserva responde 500 | #187 |
| HU-01.1c | «hora inicial y hora final válidas» (caso contrario) | Se rechaza un horario cuya hora final… (2 casos) | `@HU-01 @aceptacion` | Rechazo y ningún horario nuevo | Pasa | Ninguno |
| HU-01.1d | «Given que soy un proveedor autenticado And tengo un servicio registrado». El rechazo explícito viene de #189 (HU-13) | T4: El Spa no puede publicar horarios en la agenda de la Barbería | `@T4 @HU-01 @defecto @bug-189` | Rechazo por ser de otro negocio y ningún horario nuevo | Falla: la publicación se acepta (201) | #189 |
| HU-01.2a | «When intento registrar el nuevo horario Then el sistema rechaza el registro And informa que existe un conflicto de horarios And no crea una nueva disponibilidad» | Se rechaza un horario que… (5 casos) | `@HU-01 @aceptacion` | Rechazo por conflicto y se conserva solo el original | Pasa | Relacionado #196 (texto de HU-13; HU-01 no fija texto) |
| HU-01.2b | «And el nuevo horario se cruza con el horario existente» (valor límite que no se cruza) | Se acepta un horario que empieza justo cuando termina el existente | `@HU-01 @aceptacion` | Guardado como disponible | Pasa | Ninguno |

## HU-03 Crear una reserva

| Id | Criterio textual de Azure | Escenario | Etiquetas | Resultado esperado | Resultado real | Bug |
|---|---|---|---|---|---|---|
| HU-03.1a | «When confirmo la reserva Then el sistema crea una reserva con estado "confirmada" And el horario deja de estar disponible» | T1: Ana reserva una cita y la Barbería la registra como atendida | `@T1 @HU-03 @HU-17 @aceptacion` | Reserva confirmada y horario fuera de la oferta | Pasa (horario preparado por JDBC) | Ninguno |
| HU-03.1b | El mismo, sobre un horario publicado por el proveedor | T5 (ver HU-01.1b) | `@T5 @HU-01 @HU-03 @defecto @bug-187` | Reserva confirmada y horario fuera de la oferta | Falla: la reserva responde 500 | #187 |
| HU-03.1c | «And estoy autenticado como cliente» | T3: Luis no puede dejar una cita a nombre de Ana | `@T3 @HU-03 @defecto @bug-291` | Rechazo, o cita a nombre de Luis; Ana sin cita. Un 500 no cumple | Falla: la cita queda a nombre de Ana (201) | #291 |
| HU-03.1d | «Given que existe un servicio con un horario disponible» (uno que ya comenzó no lo es, según #183) | No se puede reservar un horario que ya comenzó | `@HU-03 @defecto @bug-183` | Rechazo y ninguna reserva | Falla: no rechaza la reserva | #183 |
| HU-03.2 | «When el cliente "Luis" intenta confirmar una reserva Then el sistema rechaza la solicitud And informa que el horario ya no está disponible And no crea una segunda reserva» | T2: Luis no puede tomar el horario de Ana y Ana no asiste a su cita | `@T2 @HU-03 @HU-17 @aceptacion` | Rechazo informado y una sola reserva, la de Ana | Pasa | Relacionado #184. #186 es la reserva por recurso (HU-13), otro flujo |

## HU-17 Registrar asistencia o inasistencia

| Id | Criterio textual de Azure | Escenario | Etiquetas | Resultado esperado | Resultado real | Bug |
|---|---|---|---|---|---|---|
| HU-17.1 | «When marco la cita como "atendida" Then el sistema actualiza su estado a "atendida" And guarda el registro de la operación» | T1 (ver HU-03.1a) | `@T1 @HU-03 @HU-17 @aceptacion` | Estado «atendida» y registro de la operación | Pasa | Ninguno |
| HU-17.2 | «When marco la cita como "no asistió" Then el sistema actualiza su estado a "no asistió" And guarda el registro de la operación» | T2 (ver HU-03.2) | `@T2 @HU-03 @HU-17 @aceptacion` | Estado «no asistió» y registro de la operación | Pasa | Ninguno |
| HU-17.3 | «Given que tengo una reserva que todavía no ha finalizado When intento marcarla como "atendida" o "no asistió" Then el sistema rechaza la operación And conserva el estado original» | No se registra el resultado en una cita que no ha comenzado o está en curso (4 casos) | `@HU-17 @aceptacion` | Rechazo; sigue confirmada y sin registro de asistencia | Pasa | Ninguno |
| HU-17.4 | «Given que tengo una reserva cancelada When intento marcarla como "atendida" o "no asistió" Then el sistema rechaza la operación» | No se registra el resultado en una cita cancelada (2 casos) | `@HU-17 @aceptacion` | Rechazo; sigue cancelada (cita sembrada por JDBC: cancelar por la API responde 500) | Pasa | Ninguno |
| HU-17.5 | «When intento modificar la asistencia de una reserva no autorizada Then el sistema rechaza la operación And no modifica el estado de la reserva» | Un negocio no puede registrar la asistencia de una cita de otro negocio | `@HU-17 @aceptacion` | Rechazo; sigue confirmada | Pasa | Ninguno |

## Cobertura y resultados
9 de 9 criterios de Azure (HU-01: 2, HU-03: 2, HU-17: 5). 13 escenarios, 22 casos: 18 `@aceptacion`, todos pasan, y 4 `@defecto`, todos fallan en la afirmación de negocio (#183, #187, #189, #291; T3, T4 y T5 son tres de ellos). Seis corridas completas y T1 a T5 por separado dieron el mismo resultado. Escenario más lento 2.86 s; suma de la suite 12.0 a 13.0 s. Los 9 criterios están automatizados, pero HU-01.1 y HU-03.1 se cumplen solo en parte.

**HU-17.4:** la cita cancelada se siembra por JDBC porque cancelar por la API responde 500 (ver adicionales).

## Ambigüedades por confirmar con el equipo
1. **HU-03.2:** Azure pide informar que «el horario ya no está disponible»; el producto dice «La disponibilidad ya fue reservada.». Se acepta por sentido.
2. **HU-03.1c:** el diseño pide rechazar; #291 pide que la reserva quede a nombre del usuario autenticado. Se acepta cualquiera de las dos salidas y se exige que Ana quede sin cita.
3. **HU-01:** no define si se puede **publicar** un horario pasado (el código lo permite). No se automatiza; #183 cubre mostrarlo y reservarlo.
4. **HU-01.1d:** el texto de HU-01 no pide el rechazo entre negocios; lo pide HU-13 (#189) para un recurso ajeno; T4 usa una agenda ajena, por analogía.
5. **HU-17.1 y HU-17.2:** «guarda el registro de la operación» no dice qué guardar. Se comprueban estado anterior, estado nuevo, usuario y fecha.
6. **HU-03 sin servicio:** reservar indicando solo el horario responde 500 (sonda) y es la reproducción original de #187. T5 sí indica el servicio, para aislar la falta de recurso: al corregir #187 hay que verificar ambas variantes.

## Hallazgos adicionales reproducidos en local y reportados
Reportados en Azure el 23/11/2026 como #298 (cancelar), #299 (historial) y #300 (servicio ajeno). No probados en Render y sin cobertura en la suite. Evidencia en `evidencias/local/adicionales/`.

| Observación | Causa observada | Relación |
|---|---|---|
| Cancelar una reserva responde 500 | `AvailabilityEntityMapper.toEntity` no copia `recursoId`; al confirmar el horario pierde su recurso y el trigger rechaza la cancelación | HU-04, misma familia que #187 |
| El historial del cliente responde 500 | Columna inexistente `d.fecha_reserva` en `BookingHistoryJpaRepository` | HU-05; distinto de #75 y #290 |
| Reservar con un servicio de otro negocio responde 500 | Trigger `fn_validar_reserva_servicio_recurso` sin manejador | HU-03, familia de #187 |

Causa de #187 en local: `POST /disponibilidades` no guarda `id_recurso` y el trigger lo exige. En Render se reprodujo #187 (500, 23/11/2026, horario del 28/09/2027; causa muy probable, no confirmada) y se observó el comportamiento de #189: un proveedor nuevo publicó en la agenda de prueba del Sprint 1, sin confirmar el dueño. Quedaron dos cuentas de prueba y el horario.
