#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PID_DIR="$ROOT_DIR/tmp/pids"
ENV_EXAMPLE="$ROOT_DIR/.env.example"

stop_process() {
  local name="$1"
  local pid_file="$PID_DIR/$name.pid"

  if [[ ! -f "$pid_file" ]]; then
    echo "$name is not tracked as running"
    return 0
  fi

  local pid
  pid="$(cat "$pid_file")"
  if kill -0 "$pid" >/dev/null 2>&1; then
    echo "Stopping $name process group with leader PID $pid"
    kill -- "-$pid" >/dev/null 2>&1 || kill "$pid" >/dev/null 2>&1 || true
    for _ in $(seq 1 20); do
      if ! kill -0 "$pid" >/dev/null 2>&1; then
        break
      fi
      sleep 1
    done
    if kill -0 "$pid" >/dev/null 2>&1; then
      echo "Force stopping $name"
      kill -9 -- "-$pid" >/dev/null 2>&1 || kill -9 "$pid" >/dev/null 2>&1 || true
    fi
  else
    echo "$name process is not running"
  fi

  rm -f "$pid_file"
}

stop_process frontend
stop_process api-gateway
stop_process enrollment-service
stop_process course-service
stop_process student-service

if command -v docker >/dev/null 2>&1; then
  echo "Stopping database containers"
  docker compose --env-file "$ENV_EXAMPLE" stop student-db course-db enrollment-db
else
  echo "Docker is not available; database containers were not stopped"
fi

echo "Project stopped."
