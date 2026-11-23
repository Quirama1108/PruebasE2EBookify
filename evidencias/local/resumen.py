"""Arma resumen.json y Resumen_ejecucion.md a partir de logs/*.json."""
import json
from pathlib import Path
L = Path(__file__).parent
orden = ["c1", "c2", "c3", "aleatorio_11", "aleatorio_222", "aleatorio_3333", "T1", "T2", "T3", "T4", "T5", "aceptacion", "defectos"]
R = {n: json.loads((L / f"logs/{n}.json").read_text(encoding="utf-8")) for n in orden}
E = {n: json.loads((L / f"logs/{n}.escenarios.json").read_text(encoding="utf-8")) for n in orden}
m = ["# Resumen de ejecución E2E (local)", "", "Base y API locales. Corridas tras corregir los hallazgos del revisor. El código 1 se debe a los 4 defectos (#183, #187, #189, #291).",
     "c1 y c2 con base limpia; c3 y siguientes sin limpiar.", "", "## Corridas", "",
     "| Corrida | Comando | Hora UTC | Salida | Total | Pasan | Fallan | Gradle (s) | Suma escenarios (s) | Escenario más lento (s) |", "|---|---|---|---|---|---|---|---|---|---|"]
for n in orden:
    r = R[n]; r["mas_lento_s"] = max(x["duracion_s"] for x in E[n])
    m.append(f"| {n} | `{r['comando']}` | {r['hora_utc']} | {r['codigo_salida']} | {r['total']} | {r['pasan']} | {r['fallan']} | {r['duracion_total_s']} | {r['suma_escenarios_s']} | {r['mas_lento_s']:.2f} |")
m += ["", "## Fallas de c1 (obtenido frente a esperado)", ""] + [f"- **{x['nombre']}**: {x['primera_linea']}" for x in R["c1"]["fallas"]]
m += ["", "## Transacciones solas", "", "| Transacción | Resultado | Escenario (s) |", "|---|---|---|"] + [f"| {n} | {'pasa' if R[n]['fallan'] == 0 else 'falla'} | {R[n]['suma_escenarios_s']} |" for n in ("T1", "T2", "T3", "T4", "T5")]
m += ["", "## Resultado por escenario (c1)", "", "| Escenario | Estado | s |", "|---|---|---|"] + [f"| {x['escenario']} | {x['estado']} | {x['duracion_s']:.2f} |" for x in E["c1"]]
(L / "Resumen_ejecucion.md").write_text("\n".join(m) + "\n", encoding="utf-8", newline="\n")
(L / "resumen.json").write_text(json.dumps(list(R.values()), ensure_ascii=False, indent=1), encoding="utf-8")
