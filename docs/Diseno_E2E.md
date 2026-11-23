# Diseño y plan: criterios de aceptación y E2E de Bookify (Sprint 3)

**Porqué:** entregable de Calidad S3 (cierre 24/11/2026): "Automatización de criterios de aceptación y ejecución E2E sobre el objeto de prueba".
**Alcance:** HU-01, HU-03, HU-17. Login (HU-07), catálogo (HU-02) e historial (HU-16) solo como apoyo.
**Fuera:** front, cambios al código del producto, push. Render solo lo ejecuta el coordinador, tras la demo.

## Enfoques
1. **Elegido:** Screenplay + scaffold `io.github.jumosqu12.screenplayarchitecture` 2.0.1, `--type=REST` (Serenity + Cucumber). Lo pide la materia.
2. Karate: no sigue Screenplay. 3. Postman: no es lo pedido.

## Ambiente (ya verificado)
- Worktree `bookify-QA-e2e`, rama `qa/e2e-sprint3`, código `5b2e445`. Proyecto de pruebas en `e2e/`.
- BD: contenedor `bookify_qa`, postgres:17, `127.0.0.1:5544`, postgres/bookify_qa, base bookify_qa. `e2e/ambiente/01_esquema.sql`, `02_catalogos.sql`, `03_semilla.sql` (43 tablas, 167 restricciones, 29 triggers reales de Supabase).
- API: `http://localhost:8090`, JDK 17 (`JAVA_HOME=C:\Program Files\Java\jdk-17`), sin SMTP. Variables SPRING_DATASOURCE_URL/USERNAME/PASSWORD y PORT.
- Proveedores semilla: barberia@bookify-qa.test y spa@bookify-qa.test. El id `aaaaaaaa-0000-4000-8000-000000000001` (Barbería) / `bbbbbbbb-...` (Spa) es a la vez proveedor, servicio, agenda_servicio y recurso. Clave local en `03_semilla.sql`. Clientes: se registran con nombre Y apellido.

## Hallazgo que fija el diseño (sonda 23/11, datos de API real)
POST /disponibilidades no guarda `id_recurso`; el trigger `fn_validar_reserva_servicio_recurso` lo exige, y la reserva responde 500: causa raíz de #187. Reservar sin `servicioId` también responde 500.
Por eso el horario reservable de T1-T3 es **precondición por JDBC** (con recurso), declarado como tal en el informe. El tramo publicar→reservar se prueba en T5.

| # | Transacción | Etiqueta | Resultado de la sonda |
|---|---|---|---|
| T1 | Ana consulta la oferta, reserva, la Barbería registra asistencia; queda atendida en el historial | @aceptacion | 201, 200, historial ATENDIDA |
| T2 | Ana reserva, Luis rechazado en el mismo horario, Ana queda no asistió | @aceptacion | 201, 409, 200 |
| T3 | Luis reserva a nombre de Ana | @defecto @bug-291 | 201 (debe rechazar) |
| T4 | El Spa publica en la agenda de la Barbería | @defecto @bug-189 | 201 (debe rechazar) |
| T5 | La Barbería publica un horario por API y Ana lo reserva | @defecto @bug-187 | 500 (debe confirmar) |

Cortos: resto de criterios de Azure de HU-01 (#2), HU-03 (#5) y HU-17 (#201).

## Reglas
- Gherkin en español, lenguaje de negocio: sin URL, JSON, HTTP ni ids. Las pruebas de defecto afirman el comportamiento CORRECTO y deben fallar en la afirmación de negocio. Nunca forzar verde.
- FIRST: <5 s por transacción, suite <1 min sin arranque, sin sleep; datos y franja propios por escenario; fechas calculadas; 3 corridas (2 limpias, 1 sin limpiar), individuales y orden aleatorio; `Questions`; features antes del código.
- Las afirmaciones de rechazo exigen respuesta de negocio y comprueban el estado posterior; un 500 nunca cuenta como rechazo.

## Plan
1. Analista (Opus/alto): features, plan E2E, matriz. 2. Automatizador (Opus/medio): scaffold y código. 3. Ejecutor (Sonnet/bajo): corridas y evidencia. 4. Reportero (Sonnet/medio) y revisor (Opus/alto) en paralelo. 5. Coordinador: Azure (comentar #187, #189, #291; bugs nuevos sin duplicar) y Render.
