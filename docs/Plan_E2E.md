# Plan E2E de Bookify, Sprint 3

## Objetivo
Automatizar los criterios de aceptación de HU-01, HU-03 y HU-17 y comprobar, de punta a punta y sobre la API real, si el producto cumple lo que pide Azure DevOps. El detalle por criterio está en [Matriz_trazabilidad.md](Matriz_trazabilidad.md).

## Alcance
- **Dentro:** 13 escenarios (22 casos al expandir los ejemplos): 5 transacciones (T1 a T5) y 8 escenarios cortos.
- **De apoyo, sin evaluarlas:** registro de clientes (HU-06), inicio de sesión (HU-07), oferta (HU-02), cancelación (HU-04) e historial del proveedor (HU-16).
- **Fuera:** front, rendimiento, concurrencia simultánea y cambios al producto. Render lo ejecuta solo el coordinador, por decisión del equipo, antes de la demo.

| Etiqueta | Significado | Casos |
|---|---|---|
| `@aceptacion` | Se prevé que el producto cumple | 18 (todos pasan) |
| `@defecto` + `@bug-NNN` | Afirma el comportamiento correcto; debe fallar mientras el bug siga abierto | 4 (todos fallan: #183, #187, #189, #291) |

Un `@defecto` en verde no se oculta: se verifica si el bug se corrigió y se propone su cierre.

## Ambiente
- Código del producto en `5b2e445`; API local en el puerto 8090, JDK 17, sin correo saliente.
- PostgreSQL 17 en el contenedor `bookify_qa`, con el esquema, las restricciones y los triggers reales, y la semilla de `e2e/ambiente/`.
- Herramientas: Serenity BDD, Cucumber y Screenplay con Gradle.
- Las credenciales se leen de la configuración local, no de los features.

## Criterios de entrada
1. La API local responde y la base está creada con los tres scripts de `e2e/ambiente/`.
2. Los proveedores Barbería y Spa inician sesión.
3. Los features y la matriz están revisados contra Azure.

## Criterios de salida
1. Tres corridas completas (dos con base limpia y una sin limpiar), más cada transacción individual y una corrida en orden aleatorio, con el mismo resultado por escenario.
2. Todo `@aceptacion` pasa o tiene un bug registrado y sin duplicar.
3. Todo `@defecto` falla **en la afirmación de negocio**, no en la preparación ni por un error de la prueba.
4. Reporte de Serenity y tiempos guardados como evidencia. Nunca se fuerza el verde.

## Principios FIRST
| Principio | Aplicación |
|---|---|
| Rápidas | Menos de 5 s por transacción y menos de 1 min la suite, sin contar el arranque. Sin esperas fijas. |
| Independientes | Clientes, recurso y franja propios por escenario. Ninguno usa lo que dejó otro. |
| Repetibles | Fechas calculadas en hora de Bogotá; el día se elige después del último horario de la agenda. Correos y apellidos únicos. |
| Autoverificables | Las `Questions` comprueban la respuesta y el estado posterior. Un error interno (500) nunca cuenta como rechazo. |
| Oportunas | Los features se escribieron antes que el código de automatización. |

## Reglas para automatizar
1. **Horario libre (T1 a T3, HU-03 y HU-17):** precondición en base de datos, con recurso, porque publicar por la API no guarda el recurso (#187). En T4, T5 y HU-01 los horarios se publican por la API.
2. **Recurso propio por escenario** (nombre único, del mismo negocio) para esos horarios. La base impide que un recurso tenga dos horarios cruzados, y las citas «en curso» siempre incluyen el momento actual.
3. **«Termina la hora de la cita»:** se reserva un horario futuro y luego se traslada al pasado en la base. No se reserva un horario pasado: eso depende del bug #183 y la prueba se rompería al corregirlo.
4. **Motivo del rechazo:** se valida el sentido del mensaje, no su texto literal, y nunca se acepta «El correo ya está registrado» (#184).
5. **Cita cancelada (HU-17.4):** se siembra por JDBC porque cancelar por la API responde 500.
6. **«Queda registrado»:** la API no lo consulta; se lee el historial de estados en la base (estado anterior, estado nuevo, usuario y fecha).
7. **Estado de la cita:** por el historial del proveedor (HU-16), buscando al cliente por su apellido único. No usar el historial del cliente (riesgo R2).
8. **Reservar** siempre indica el servicio y, salvo en T3, el cliente autenticado.
9. Los pasos «Cuando» guardan la respuesta sin fallar; la comprobación va en «Entonces».

## Resultados de la ejecución local (23/11/2026)
- 6 corridas completas (2 con base limpia, 1 sin limpiar, 3 en orden aleatorio con semillas 11, 222 y 3333): 22 casos, 18 pasan, 4 fallan, siempre los mismos. T1 y T2 pasan; T3, T4 y T5 fallan por separado.
- Criterios de salida 1 a 4 cumplidos en local. Los 4 defectos fallan en la afirmación de negocio (`Expected: ...`), no en la preparación; no se evalúan las afirmaciones posteriores a la primera falla.
- FIRST medido: mayor escenario 2.86 s, T1 a T5 solas entre 2.81 y 3.50 s, suma de la suite 12.0 a 13.0 s, Gradle completo 15 a 21 s.
- Render: sonda manual del coordinador; se reprodujo #187 (500) y se observó el comportamiento de #189 (dueño de la agenda sin confirmar). No se corrió la suite allá.
- Adicionales reportados, sin cobertura: cancelar reserva (#298, HU-04), historial del cliente (#299, HU-05) y reservar con servicio de otro negocio (#300, HU-03), los tres con 500. Ver la matriz.
- Las HU ya estaban cerradas (no es TDD), no hay ejecución en paralelo. La revisión independiente está en `Revision_independiente.md`; los hallazgos obligatorios (H1 a H5) se atendieron.

## Riesgos y limitaciones
| # | Riesgo o limitación | Tratamiento |
|---|---|---|
| R1 | La preparación en base de datos no recorre publicar y reservar | T5 cubre ese tramo; se declara en el informe |
| R2 | El historial del cliente (HU-05) consulta `d.fecha_reserva`, columna inexistente; responde 500 (reproducido en local, reportado como #299) | Fuera de alcance; se usa HU-16 y lo confirma el coordinador |
| R3 | Confirmar una reserva borra el recurso del horario (`AvailabilityEntityMapper` omite `recursoId`), por lo que cancelar responde 500 (reproducido en local, reportado como #298, HU-04) | No lo exige ningún criterio del alcance; el coordinador lo revisa al comentar #187 |
| R4 | El ambiente local puede diferir de Render | Mismo esquema y triggers; Render se valida aparte |
| R5 | En la corrida sin limpiar se acumulan horarios, y los defectos dejan datos (T3 y T4) | Datos y días únicos por escenario |
| R6 | La doble reserva se prueba en secuencia, no con solicitudes simultáneas | Se declara como limitación |
