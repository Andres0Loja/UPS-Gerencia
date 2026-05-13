#!/usr/bin/env sh
set -eu

curl -sS -X POST "http://localhost:8080/api/academic-requests" \
  -H "Content-Type: application/json" \
  -d '{
    "professorId": 1,
    "subjectId": 1,
    "studentIds": [1, 2],
    "topic": "galaxias y exploracion espacial"
  }'

printf "\n\nHistorial:\n"
curl -sS "http://localhost:8080/api/solicitudes"
