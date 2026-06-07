#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_DIR="$ROOT_DIR/logs"
PID_DIR="$ROOT_DIR/tmp/pids"
ENV_EXAMPLE="$ROOT_DIR/.env.example"
ENV_LOCAL="$ROOT_DIR/.env"

mkdir -p "$LOG_DIR" "$PID_DIR"

if [[ -f "$ENV_EXAMPLE" ]]; then
  set -a
  # shellcheck disable=SC1090
  source "$ENV_EXAMPLE"
  set +a
fi

if [[ -f "$ENV_LOCAL" ]]; then
  set -a
  # shellcheck disable=SC1090
  source "$ENV_LOCAL"
  set +a
fi

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Missing required command: $1" >&2
    exit 1
  fi
}

wait_for_url() {
  local name="$1"
  local url="$2"
  local attempts="${3:-60}"

  echo "Waiting for $name: $url"
  for _ in $(seq 1 "$attempts"); do
    if curl -fsS "$url" >/dev/null 2>&1; then
      echo "$name is ready"
      return 0
    fi
    sleep 2
  done

  echo "$name did not become ready. Check logs in $LOG_DIR." >&2
  return 1
}

wait_for_mysql() {
  local container="$1"
  local root_password="$2"

  echo "Waiting for MySQL container: $container"
  for _ in $(seq 1 60); do
    if docker exec "$container" mysqladmin ping -uroot -p"$root_password" --silent >/dev/null 2>&1; then
      echo "$container is ready"
      return 0
    fi
    sleep 2
  done

  echo "$container did not become ready." >&2
  return 1
}

start_process() {
  local name="$1"
  local command="$2"
  local pid_file="$PID_DIR/$name.pid"
  local log_file="$LOG_DIR/$name.log"

  if [[ -f "$pid_file" ]] && kill -0 "$(cat "$pid_file")" >/dev/null 2>&1; then
    echo "$name is already running with PID $(cat "$pid_file")"
    return 0
  fi

  echo "Starting $name. Logs: $log_file"
  (
    cd "$ROOT_DIR"
    nohup bash -lc "$command" >"$log_file" 2>&1 &
    echo $! >"$pid_file"
  )
}

require_command docker
require_command mvn
require_command curl

echo "Starting database containers"
docker compose --env-file "$ENV_EXAMPLE" up -d student-db course-db enrollment-db

wait_for_mysql student-db "${STUDENT_DB_ROOT_PASSWORD:-root_password}"
wait_for_mysql course-db "${COURSE_DB_ROOT_PASSWORD:-root_password}"
wait_for_mysql enrollment-db "${ENROLLMENT_DB_ROOT_PASSWORD:-root_password}"

start_process "student-service" \
  "STUDENT_DB_URL=jdbc:mysql://localhost:${STUDENT_DB_PORT:-3307}/${STUDENT_DB_NAME:-student_db} STUDENT_DB_USERNAME=${STUDENT_DB_USERNAME:-student_user} STUDENT_DB_PASSWORD=${STUDENT_DB_PASSWORD:-student_password} STUDENT_JPA_DDL_AUTO=${STUDENT_JPA_DDL_AUTO:-update} mvn -f student-service/pom.xml spring-boot:run"
wait_for_url "Student Service" "http://localhost:${STUDENT_SERVICE_PORT:-8081}/v3/api-docs"

start_process "course-service" \
  "COURSE_DB_URL=jdbc:mysql://localhost:${COURSE_DB_PORT:-3308}/${COURSE_DB_NAME:-course_db} COURSE_DB_USERNAME=${COURSE_DB_USERNAME:-course_user} COURSE_DB_PASSWORD=${COURSE_DB_PASSWORD:-course_password} COURSE_JPA_DDL_AUTO=${COURSE_JPA_DDL_AUTO:-update} mvn -f course-service/pom.xml spring-boot:run"
wait_for_url "Course Service" "http://localhost:${COURSE_SERVICE_PORT:-8082}/v3/api-docs"

start_process "enrollment-service" \
  "ENROLLMENT_DB_URL=jdbc:mysql://localhost:${ENROLLMENT_DB_PORT:-3309}/${ENROLLMENT_DB_NAME:-enrollment_db} ENROLLMENT_DB_USERNAME=${ENROLLMENT_DB_USERNAME:-enrollment_user} ENROLLMENT_DB_PASSWORD=${ENROLLMENT_DB_PASSWORD:-enrollment_password} ENROLLMENT_JPA_DDL_AUTO=${ENROLLMENT_JPA_DDL_AUTO:-update} STUDENT_SERVICE_BASE_URL=http://localhost:${STUDENT_SERVICE_PORT:-8081} COURSE_SERVICE_BASE_URL=http://localhost:${COURSE_SERVICE_PORT:-8082} mvn -f enrollment-service/pom.xml spring-boot:run"
wait_for_url "Enrollment Service" "http://localhost:${ENROLLMENT_SERVICE_PORT:-8083}/v3/api-docs"

start_process "api-gateway" \
  "STUDENT_SERVICE_URL=http://localhost:${STUDENT_SERVICE_PORT:-8081} COURSE_SERVICE_URL=http://localhost:${COURSE_SERVICE_PORT:-8082} ENROLLMENT_SERVICE_URL=http://localhost:${ENROLLMENT_SERVICE_PORT:-8083} mvn -f api-gateway/pom.xml spring-boot:run"
wait_for_url "API Gateway" "http://localhost:${API_GATEWAY_PORT:-8080}/students"

if [[ -f "$ROOT_DIR/frontend/package.json" ]]; then
  require_command npm
  if [[ ! -d "$ROOT_DIR/frontend/node_modules" ]]; then
    echo "Installing frontend dependencies"
    npm --prefix "$ROOT_DIR/frontend" install
  fi
  start_process "frontend" \
    "VITE_API_BASE_URL=http://localhost:${API_GATEWAY_PORT:-8080} npm --prefix frontend run dev -- --host 0.0.0.0"
  wait_for_url "Frontend" "http://localhost:${FRONTEND_PORT:-5173}" 30
else
  echo "No frontend/package.json found. Skipping frontend startup."
fi

cat <<EOF

Project is running.

URLs:
- Frontend:          http://localhost:${FRONTEND_PORT:-5173} (if frontend exists)
- API Gateway:      http://localhost:${API_GATEWAY_PORT:-8080}
- Student Swagger:  http://localhost:${STUDENT_SERVICE_PORT:-8081}/swagger-ui/index.html
- Course Swagger:   http://localhost:${COURSE_SERVICE_PORT:-8082}/swagger-ui/index.html
- Enrollment Swagger: http://localhost:${ENROLLMENT_SERVICE_PORT:-8083}/swagger-ui/index.html

Logs:
- $LOG_DIR

Stop everything:
- scripts/stop-all.sh
EOF
