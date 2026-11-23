#!/usr/bin/env bash
# Recrea la base de QA desde los SQL y deja la API local en 8090. Se puede repetir: la base vuelve a la semilla.
set -euo pipefail
cd "$(dirname "$0")"
JAVA="${JAVA_QA:-C:/Program Files/Java/jdk-17}/bin/java"
JAR=${BOOKIFY_JAR:-../../build/libs/bookify-0.0.1-SNAPSHOT.jar}
API=http://localhost:8090/api/v1
LOG="${TMPDIR:-/tmp}/bookify_qa_api.log"
CLAVE='Calidad-QA-Local-2026!'

entrar() {
  curl -fs -o /dev/null "$API/auth/login" -H 'Content-Type: application/json' \
    -d "{\"correo\":\"barberia@bookify-qa.test\",\"password\":\"$CLAVE\"}"
}

docker rm -f -v bookify_qa >/dev/null 2>&1 || true
docker run -d --name bookify_qa -e POSTGRES_PASSWORD=bookify_qa -e POSTGRES_DB=bookify_qa \
  -p 127.0.0.1:5544:5432 postgres:17-alpine >/dev/null
# Por TCP: el servidor temporal de la inicialización solo escucha en el socket.
for _ in $(seq 60); do
  docker exec bookify_qa pg_isready -q -h 127.0.0.1 -U postgres -d bookify_qa && break
  sleep 1
done
for sql in 01_esquema.sql 02_catalogos.sql 03_semilla.sql; do
  docker exec -i -e PGCLIENTENCODING=UTF8 bookify_qa \
    psql -q -v ON_ERROR_STOP=1 -U postgres -d bookify_qa -o /dev/null < "$sql"
done
echo "Base bookify_qa recreada en 127.0.0.1:5544"

if curl -s -o /dev/null "$API/catalog/services"; then
  echo "Ya hay un servicio en 8090; se reutiliza"
else
  [ -f "$JAR" ] || { echo "Falta $JAR: ejecute ./gradlew bootJar en la raíz del producto" >&2; exit 1; }
  SPRING_DATASOURCE_URL=jdbc:postgresql://127.0.0.1:5544/bookify_qa SPRING_DATASOURCE_USERNAME=postgres \
  SPRING_DATASOURCE_PASSWORD=bookify_qa PORT=8090 nohup "$JAVA" -jar "$JAR" >"$LOG" 2>&1 </dev/null &
fi

for _ in $(seq 90); do
  if entrar; then echo "API lista en $API"; exit 0; fi
  sleep 1
done
echo "La API no respondió bien en 90 s; revise $LOG" >&2
exit 1
