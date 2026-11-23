# Resumen de ejecución E2E (local)

Base y API locales. Corridas tras corregir los hallazgos del revisor. El código 1 se debe a los 4 defectos (#183, #187, #189, #291).
c1 y c2 con base limpia; c3 y siguientes sin limpiar.

## Corridas

| Corrida | Comando | Hora UTC | Salida | Total | Pasan | Fallan | Gradle (s) | Suma escenarios (s) | Escenario más lento (s) |
|---|---|---|---|---|---|---|---|---|---|
| c1 | `./gradlew test --rerun-tasks` | 2026-11-23T21:18:01Z | 1 | 22 | 18 | 4 | 21.0 | 13.04 | 2.86 |
| c2 | `./gradlew test --rerun-tasks` | 2026-11-23T21:18:42Z | 1 | 22 | 18 | 4 | 19.9 | 13.02 | 2.81 |
| c3 | `./gradlew test --rerun-tasks` | 2026-11-23T21:19:02Z | 1 | 22 | 18 | 4 | 20.3 | 12.99 | 2.85 |
| aleatorio_11 | `./gradlew aleatorio -Dsemilla=11 --rerun-tasks` | 2026-11-23T21:19:23Z | 1 | 22 | 18 | 4 | 15.5 | 11.95 | 2.55 |
| aleatorio_222 | `./gradlew aleatorio -Dsemilla=222 --rerun-tasks` | 2026-11-23T21:19:39Z | 1 | 22 | 18 | 4 | 17.1 | 12.89 | 2.77 |
| aleatorio_3333 | `./gradlew aleatorio -Dsemilla=3333 --rerun-tasks` | 2026-11-23T21:19:56Z | 1 | 22 | 18 | 4 | 16.0 | 12.38 | 2.80 |
| T1 | `./gradlew test -Dcucumber.filter.tags=@T1 --rerun-tasks` | 2026-11-23T21:20:12Z | 0 | 1 | 1 | 0 | 11.5 | 3.29 | 3.29 |
| T2 | `./gradlew test -Dcucumber.filter.tags=@T2 --rerun-tasks` | 2026-11-23T21:20:24Z | 0 | 1 | 1 | 0 | 11.4 | 3.5 | 3.50 |
| T3 | `./gradlew test -Dcucumber.filter.tags=@T3 --rerun-tasks` | 2026-11-23T21:20:36Z | 1 | 1 | 0 | 1 | 11.3 | 3.38 | 3.38 |
| T4 | `./gradlew test -Dcucumber.filter.tags=@T4 --rerun-tasks` | 2026-11-23T21:20:48Z | 1 | 1 | 0 | 1 | 10.5 | 2.81 | 2.81 |
| T5 | `./gradlew test -Dcucumber.filter.tags=@T5 --rerun-tasks` | 2026-11-23T21:20:58Z | 1 | 1 | 0 | 1 | 10.9 | 2.98 | 2.98 |
| aceptacion | `./gradlew aceptacion --rerun-tasks` | 2026-11-23T21:21:10Z | 0 | 18 | 18 | 0 | 18.4 | 10.83 | 2.98 |
| defectos | `./gradlew defectos --rerun-tasks` | 2026-11-23T21:21:28Z | 1 | 4 | 0 | 4 | 11.6 | 4.55 | 2.79 |

## Fallas de c1 (obtenido frente a esperado)

- **Crear una reserva :: No se puede reservar un horario que ya comenzó**: Expected: "RECHAZO: HORARIO_YA_COMENZO" but: was "ACEPTADA"
- **Transacciones de punta a punta de una reserva :: Ana reserva un horario que la Barbería acaba de publicar**: Expected: "ACEPTADA" but: was "HTTP 500 {\"timestamp\":\"2026-11-23T21:18:19.970Z\",\"status\":500,\"error\":\"Internal Server Error\",\"path\":\"/api/v1/bookings\"}"
- **Transacciones de punta a punta de una reserva :: El Spa no puede publicar horarios en la agenda de la Barbería**: Expected: "RECHAZO: DE_OTRO_NEGOCIO" but: was "ACEPTADA"
- **Transacciones de punta a punta de una reserva :: Luis no puede dejar una cita a nombre de Ana**: Expected: "RECHAZADA O A NOMBRE PROPIO" but: was "CITA REGISTRADA A NOMBRE DE OTRO CLIENTE"

## Transacciones solas

| Transacción | Resultado | Escenario (s) |
|---|---|---|
| T1 | pasa | 3.29 |
| T2 | pasa | 3.5 |
| T3 | falla | 3.38 |
| T4 | falla | 2.81 |
| T5 | falla | 2.98 |

## Resultado por escenario (c1)

| Escenario | Estado | s |
|---|---|---|
| Crear una reserva :: No se puede reservar un horario que ya comenzó | falla | 0.62 |
| Registrar asistencia o inasistencia :: No se registra "atendida" en una cita cancelada | pasa | 0.68 |
| Registrar asistencia o inasistencia :: No se registra "atendida" en una cita que está en curso | pasa | 0.68 |
| Registrar asistencia o inasistencia :: No se registra "atendida" en una cita que no ha comenzado | pasa | 0.74 |
| Registrar asistencia o inasistencia :: No se registra "no asistió" en una cita cancelada | pasa | 0.65 |
| Registrar asistencia o inasistencia :: No se registra "no asistió" en una cita que está en curso | pasa | 0.67 |
| Registrar asistencia o inasistencia :: No se registra "no asistió" en una cita que no ha comenzado | pasa | 0.69 |
| Registrar asistencia o inasistencia :: Un negocio no puede registrar la asistencia de una cita de otro negocio | pasa | 0.68 |
| Registrar disponibilidad del proveedor :: Se acepta un horario que empieza justo cuando termina el existente | pasa | 0.16 |
| Registrar disponibilidad del proveedor :: Se rechaza un horario cuya hora final es anterior a la inicial | pasa | 0.14 |
| Registrar disponibilidad del proveedor :: Se rechaza un horario cuya hora final es igual a la inicial | pasa | 0.14 |
| Registrar disponibilidad del proveedor :: Se rechaza un horario que contiene por completo al existente | pasa | 0.20 |
| Registrar disponibilidad del proveedor :: Se rechaza un horario que empieza dentro del existente | pasa | 0.20 |
| Registrar disponibilidad del proveedor :: Se rechaza un horario que es igual al existente | pasa | 0.18 |
| Registrar disponibilidad del proveedor :: Se rechaza un horario que está contenido en el existente | pasa | 0.19 |
| Registrar disponibilidad del proveedor :: Se rechaza un horario que termina dentro del existente | pasa | 0.22 |
| Registrar disponibilidad del proveedor :: Un horario válido queda disponible y los clientes lo ven | pasa | 2.86 |
| Transacciones de punta a punta de una reserva :: Ana reserva un horario que la Barbería acaba de publicar | falla | 0.54 |
| Transacciones de punta a punta de una reserva :: Ana reserva una cita y la Barbería la registra como atendida | pasa | 0.66 |
| Transacciones de punta a punta de una reserva :: El Spa no puede publicar horarios en la agenda de la Barbería | falla | 0.18 |
| Transacciones de punta a punta de una reserva :: Luis no puede dejar una cita a nombre de Ana | falla | 0.93 |
| Transacciones de punta a punta de una reserva :: Luis no puede tomar el horario de Ana y Ana no asiste a su cita | pasa | 1.06 |
