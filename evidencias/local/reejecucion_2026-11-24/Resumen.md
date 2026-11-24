# Reejecución del 24/11/2026 sobre main `8884acd`

Ambiente: local, base `bookify_qa` recreada antes de la primera corrida, API recompilada, JDK 17. Las corridas 2 y 3 van sobre los datos de la anterior.

| Comprobación | Resultado |
|---|---|
| Suite E2E (3 corridas) | 22 casos: 18 pasan, 4 fallan; las mismas fallas en las tres (#183, #187, #189, #291) |
| Pruebas unitarias | 255: 254 pasan, 1 omitida |
| #298, #299 y #300 | Siguen respondiendo 500 |
| HU-23 (código HU-26): modificar o eliminar un horario ajeno o inexistente | 404 «Horario no encontrado» |
| HU-23: modificar o eliminar con reserva confirmada | 409 y el horario no cambia |
| HU-23: rango inválido; id o fecha con formato inválido | 400 |
| HU-23: modificar y eliminar un horario propio | 200 y 204 |
| `/actuator/health` y `/actuator/prometheus` (local y Render) | 200 sin token |
| `/actuator/info` (local y Render) y `/actuator/env` (local) | 401 |
| `observability/prometheus.yml` con `promtool check config` | Falla: `https://` dentro de `targets` |
| `docker compose` de `observability/` | Prometheus no arranca: el montaje apunta a `observability/observability/prometheus.yml` |
| Prometheus con el `targets` corregido | Lee Render: `health: up` |
| `management.prometheus.metrics.export.push.*` | No existe en Spring Boot 4.1.1 |

HU-23 y la observabilidad se comprobaron con una sonda HTTP; la suite E2E no las cubre. Observación menor: un id inválido responde con un mensaje que habla del formato de las fechas.

**Nota posterior:** las fallas de `prometheus.yml` y del montaje (#301) se corrigieron en `main` con el PR #14 (`68147f7`), que solo cambia esos dos archivos de `observability/`. El defecto de `management.prometheus.metrics.export.push.*` (#302) sigue abierto.
