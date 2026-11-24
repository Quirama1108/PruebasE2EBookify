# Reporte de Quality Gates, Sprint 3

Fecha: 24/11/2026. Código: `main` de Bookify en `68147f7`. Análisis en local con SonarQube Community y el perfil «Sonar way comprehensive». Datos: [resultado.json](../evidencias/quality_gates/resultado.json).

## Resultado: se cumplen 4 de 5

| Gate | Meta | Resultado | Cumple |
|---|---|---|---|
| Cobertura de pruebas unitarias | ≥ 65 % | 76,5 % (líneas: 79,2 %) | Sí |
| Deuda técnica | ≤ 2 días | 239 min (4 h) | Sí |
| Complejidad ciclomática por clase | < 50 | 34 (`BookingService`) | Sí |
| Severidad de hallazgos | Minor | 1 Critical y 4 Major | **No** |
| Vulnerabilidades críticas | 0 | 0 (hay 1 de severidad Major) | Sí |

254 pruebas unitarias pasan y 1 está omitida. Hay 0 bugs, 67 code smells y 0 hallazgos Blocker. La complejidad se lee por archivo.

## Hallazgos por encima de Minor

| Severidad | Regla | Archivo | Línea | Esfuerzo |
|---|---|---|---|---|
| Critical | java:S1452 | `MerchantOccupancyMetricsController` | 21 | 20 min |
| Major (vulnerabilidad) | java:S2077 | `EmailBookingConfirmationAdapter` | 88 | 20 min |
| Major | java:S5778 | `ProviderBookingHistoryUseCaseImplTest` | 47 | 5 min |
| Major | java:S5778 | `ProviderBookingHistoryUseCaseImplTest` | 54 | 5 min |
| Major | java:S5778 | `ProviderBookingHistoryUseCaseImplTest` | 60 | 5 min |

En S2077 se concatenan nombres de tabla y columna. Las llamadas son internas y usan constantes; el código se pasa como parámetro. Por esa lectura no se considera explotable, pero el hallazgo cuenta para el gate. No se corrigieron en este sprint.

## Por qué en local y no en SonarCloud

El último análisis de SonarCloud es del 26/10/2026 (`d903328`), porque el fork que lo ejecuta no se ha actualizado desde entonces (Azure #295). Ese análisis no incluye el código del Sprint 3.

Para comprobar que las cifras son comparables se analizó en local ese mismo commit:

| Métrica en `d903328` | SonarCloud | Local |
|---|---|---|
| Cobertura | 95,2 % | 95,3 % |
| Deuda técnica | 180 min | 180 min |
| Code smells | 59 | 59 |
| Hallazgos Minor | 45 | 45 |
| Vulnerabilidades | 0 | 0 |

## Cambio frente al análisis anterior

La cobertura bajó de 95,2 % a 76,5 % y no se mantuvo la meta propia del 90 %. Entró código sin pruebas unitarias, sobre todo métricas y correo.

## Limitaciones

- No reemplaza el análisis de SonarCloud, que está pendiente.
- La comparación con SonarCloud se hizo con un solo commit.
- El perfil local tiene 608 reglas de Java activas y el de SonarCloud, 670.
