#!/usr/bin/env bash
# uso: run.sh nombre dirResultados limpiar(0/1) gradle-args...
export JAVA_HOME='C:\Program Files\Java\jdk-17'
cd "$(dirname "$0")/../.."
n=$1; dir=$2; limpiar=$3; shift 3
L=evidencias/local
[ "$limpiar" = 1 ] && bash ambiente/levantar.sh > $L/logs/$n.levantar.log 2>&1
rm -rf "$dir"; mkdir -p build/aleatorio; rm -f build/aleatorio/resultado.xml
ini=$(date -u +%FT%TZ); t0=$(date +%s.%N)
./gradlew "$@" --rerun-tasks > $L/logs/$n.log 2>&1; cod=$?
t1=$(date +%s.%N)
python $L/parse.py "$dir" $L/logs/$n.escenarios.json
python - "$n" "$cod" "$ini" "$(python -c "print(round($t1-$t0,1))")" "$*" <<'PY'
import sys,json
n,cod,ini,dur,cmd=sys.argv[1:6]
r=json.load(open(f'evidencias/local/logs/{n}.escenarios.json',encoding='utf-8'))
f=[x for x in r if x['estado']=='falla']
res=dict(corrida=n,comando='./gradlew '+cmd+' --rerun-tasks',hora_utc=ini,codigo_salida=int(cod),duracion_total_s=float(dur),total=len(r),pasan=sum(x['estado']=='pasa' for x in r),fallan=len(f),omitidos=sum(x['estado']=='omitido' for x in r),suma_escenarios_s=round(sum(x['duracion_s'] for x in r),2),fallas=[dict(nombre=x['escenario'],primera_linea=x['primera_linea']) for x in f])
json.dump(res,open(f'evidencias/local/logs/{n}.json','w',encoding='utf-8'),ensure_ascii=False,indent=1)
print(n,cod,res['total'],res['pasan'],res['fallan'],res['duracion_total_s'])
PY
