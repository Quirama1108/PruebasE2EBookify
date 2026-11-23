"""Convierte el CSV de extraer_esquema.sql en SQL ejecutable (01_esquema.sql y 02_catalogos.sql)."""
import csv, json, re, sys
import xml.etree.ElementTree as ET
from pathlib import Path

csv.field_size_limit(10**8)
fuente, destino = sys.argv[1], Path(sys.argv[2])
with open(fuente, encoding="utf-8-sig", newline="") as fh:
    d = json.loads(list(csv.reader(fh))[1][0])

out = ["SET check_function_bodies = off;", "CREATE EXTENSION IF NOT EXISTS pgcrypto;", 'CREATE EXTENSION IF NOT EXISTS "uuid-ossp";',
       "CREATE EXTENSION IF NOT EXISTS btree_gist;", "CREATE EXTENSION IF NOT EXISTS unaccent;"]
tablas = {}
for c in d["columnas"]:
    tablas.setdefault(c["t"], []).append(c)
    for s in re.findall(r"nextval\('([^']+)'", c["def"] or ""):
        out.append(f"CREATE SEQUENCE IF NOT EXISTS {s};")
for t, cols in tablas.items():
    lin = []
    for c in cols:
        s = f'"{c["c"]}" {c["tipo"]}'
        if c["ident"]:
            s += f' GENERATED {"ALWAYS" if c["ident"] == "a" else "BY DEFAULT"} AS IDENTITY'
        elif c["gen"]:
            s += f' GENERATED ALWAYS AS ({c["def"]}) STORED'
        elif c["def"] is not None:
            s += f' DEFAULT {c["def"]}'
        lin.append(s + (" NOT NULL" if c["nn"] else ""))
    out.append(f'CREATE TABLE public."{t}" (\n  ' + ",\n  ".join(lin) + "\n);")
orden = {"p": 0, "u": 1, "c": 2, "x": 3, "f": 4}
cons = [c for c in d["restricciones"] if c["tipo"] in orden]
funcs = [f.strip() + ";" for f in d["funciones"] or []]
out += funcs  # antes de restricciones e indices: pueden usarlas
for c in sorted(cons, key=lambda c: orden[c["tipo"]]):
    out.append(f'ALTER TABLE {c["t"]} ADD CONSTRAINT "{c["n"]}" {c["def"]};')
nombres = {c["n"] for c in cons}
for i in d["indices"] or []:
    n = re.search(r"INDEX (\S+) ON", i).group(1).strip('"')
    if n not in nombres:
        out.append(i + ";")
for v in d["vistas"] or []:
    out.append(f'CREATE VIEW public.{v["n"]} AS {v["def"]}')
out += [t + ";" for t in d["triggers"] or []]
(destino / "01_esquema.sql").write_text("\n".join(out) + "\n", encoding="utf-8", newline="\n")

XSI = "{http://www.w3.org/2001/XMLSchema-instance}nil"
cat = ["SET session_replication_role = replica;"]
for t, x in d["catalogos"].items():
    for fila in ET.fromstring(x):
        vals = [(e.tag, None if e.get(XSI) else (e.text or "")) for e in fila]
        cs = ", ".join(f'"{k}"' for k, _ in vals)
        vs = ", ".join("NULL" if v is None else "'" + v.replace("'", "''") + "'" for _, v in vals)
        cat.append(f"INSERT INTO public.{t} ({cs}) OVERRIDING SYSTEM VALUE VALUES ({vs});")
    ident = [c for c in tablas[t] if c["ident"] or "nextval" in (c["def"] or "")]
    for c in ident:
        cat.append(f"SELECT setval(pg_get_serial_sequence('public.{t}','{c['c']}'), (SELECT max({c['c']}) FROM public.{t})) WHERE pg_get_serial_sequence('public.{t}','{c['c']}') IS NOT NULL;")
cat.append("SET session_replication_role = origin;")
(destino / "02_catalogos.sql").write_text("\n".join(cat) + "\n", encoding="utf-8", newline="\n")
print(len(tablas), "tablas;", len(cons), "restricciones;", len(d["restricciones"]) - len(cons), "omitidas;", len(d["triggers"]), "triggers")
