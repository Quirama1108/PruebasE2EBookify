# Verificación de los ajustes por la retroalimentación (24/11/2026)

Ambiente local, JDK 17, base `bookify_qa` recreada antes de la corrida completa.

| Comprobación | Resultado |
|---|---|
| Completa antes de los ajustes | 22 casos: 18 pasan, 4 fallan |
| Completa después (`completa.log`) | Igual, escenario por escenario |
| Aleatorio, semillas 11 y 77 | 22 casos: 18 pasan, 4 fallan |
| Motivo de las 4 fallas | Los de #183, #187, #189 y #291, en la afirmación de negocio |
| Registro de clientes rechazado a propósito | 14 de 22 fallan: los que usan clientes y el de #189 |
| Clave de negocio incorrecta a propósito | T1 y T4 fallan |
