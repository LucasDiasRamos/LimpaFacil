#!/usr/bin/env sh
set -eu

if [ -n "${LIMPAFACIL_DB_URL:-}" ]; then
  echo "Aguardando PostgreSQL em db:5432..."
  until nc -z db 5432; do
    sleep 1
  done
fi

exec "$@"
