# Microservices-Based Student Enrollment System

## Project Overview

This repository contains a Spring Boot microservices system for managing students, courses, and course enrollments. It implements independent Student, Course, and Enrollment services behind an API Gateway, with each data-owning service using its own MySQL database.

The system is built around four services:

- `student-service`
- `course-service`
- `enrollment-service`
- `api-gateway`

Students are identified by CNIE for enrollment. The Enrollment Service validates students and courses through WebClient calls, enforces a maximum of three students per course, supports cancellation only within 24 hours, and exposes a dashboard response with course titles and `canCancel` values.

## Architecture Diagram

```text
Client
  |
  v
API Gateway (:8080)
  |
  +--> Student Service (:8081) ----> MySQL student_db (:3307 host)
  |
  +--> Course Service (:8082) -----> MySQL course_db (:3308 host)
  |
  +--> Enrollment Service (:8083) -> MySQL enrollment_db (:3309 host)
             |
             +--> Student Service via WebClient
             |
             +--> Course Service via WebClient
```

The Enrollment database stores only enrollment identity and references: `id`, `studentId`, `courseId`, and `enrolledAt`. Student names, CNIE values, and course titles are retrieved from their owning services when needed.

## Services

| Service | Responsibility | Port |
| --- | --- | --- |
| `student-service` | Manages student records, validates unique CNIE values, and exposes lookup by id or CNIE. | `8081` |
| `course-service` | Manages the course catalog and exposes CRUD endpoints for course data. | `8082` |
| `enrollment-service` | Creates enrollments by CNIE and course id, enforces capacity and cancellation rules, and builds dashboard responses by calling other services with WebClient. | `8083` |
| `api-gateway` | Provides the single entry point and routes `/students/**`, `/courses/**`, and `/enrollments/**` to their services. | `8080` |

## Databases

Each data-owning service uses its own MySQL database.

| Service | Database | Host Port | Notes |
| --- | --- | --- | --- |
| `student-service` | `student_db` | `3307` | Owns student profile and CNIE data. |
| `course-service` | `course_db` | `3308` | Owns course titles and descriptions. |
| `enrollment-service` | `enrollment_db` | `3309` | Stores only `id`, `studentId`, `courseId`, and `enrolledAt`. |

Local credentials are placeholders in `.env.example`; production credentials should not be committed.

## Ports

| Component | Port | Status |
| --- | --- | --- |
| API Gateway | `8080` | HTTP entry point |
| Student Service | `8081` | Spring Boot service |
| Course Service | `8082` | Spring Boot service |
| Enrollment Service | `8083` | Spring Boot service |
| Student Database | `3307` host -> `3306` container | MySQL |
| Course Database | `3308` host -> `3306` container | MySQL |
| Enrollment Database | `3309` host -> `3306` container | MySQL |

## Technologies

| Technology | Version / Use |
| --- | --- |
| Java | `21` project target |
| Spring Boot | `3.3.6` |
| Spring Cloud | `2023.0.4` for API Gateway |
| Maven | `3.9.16` locally verified |
| MySQL | `8.4` Docker image |
| Springdoc OpenAPI | `2.6.0` |
| Docker Compose | `5.1.4` locally verified |
| Spring modules | Web, Data JPA, Validation, WebFlux WebClient, Cloud Gateway |

## Running Instructions

On Arch Linux, the easiest workflow is to use the project scripts:

```bash
scripts/start-all.sh
```

This starts the MySQL containers, Student Service, Course Service, Enrollment Service, API Gateway, and the frontend when `frontend/package.json` exists. Logs are written to `logs/`, and process IDs are stored in `tmp/pids/`.

Stop the full project with:

```bash
scripts/stop-all.sh
```

Start the local MySQL databases:

```bash
docker compose --env-file .env.example up -d student-db course-db enrollment-db
```

Run the services in separate terminals:

```bash
STUDENT_DB_URL=jdbc:mysql://localhost:3307/student_db \
STUDENT_DB_USERNAME=student_user \
STUDENT_DB_PASSWORD=student_password \
STUDENT_JPA_DDL_AUTO=update \
mvn -f student-service/pom.xml spring-boot:run
```

```bash
COURSE_DB_URL=jdbc:mysql://localhost:3308/course_db \
COURSE_DB_USERNAME=course_user \
COURSE_DB_PASSWORD=course_password \
COURSE_JPA_DDL_AUTO=update \
mvn -f course-service/pom.xml spring-boot:run
```

```bash
ENROLLMENT_DB_URL=jdbc:mysql://localhost:3309/enrollment_db \
ENROLLMENT_DB_USERNAME=enrollment_user \
ENROLLMENT_DB_PASSWORD=enrollment_password \
ENROLLMENT_JPA_DDL_AUTO=update \
STUDENT_SERVICE_BASE_URL=http://localhost:8081 \
COURSE_SERVICE_BASE_URL=http://localhost:8082 \
mvn -f enrollment-service/pom.xml spring-boot:run
```

```bash
STUDENT_SERVICE_URL=http://localhost:8081 \
COURSE_SERVICE_URL=http://localhost:8082 \
ENROLLMENT_SERVICE_URL=http://localhost:8083 \
mvn -f api-gateway/pom.xml spring-boot:run
```

The Docker Compose file also contains build-context placeholders for service containers. Dockerfiles are not included in this project, so the verified workflow runs databases through Docker and services through Maven.

## API Documentation

Swagger/OpenAPI is enabled for the three backend services.

- Student Service: `http://localhost:8081/swagger-ui/index.html`
- Course Service: `http://localhost:8082/swagger-ui/index.html`
- Enrollment Service: `http://localhost:8083/swagger-ui/index.html`

OpenAPI JSON:

- Student Service: `http://localhost:8081/v3/api-docs`
- Course Service: `http://localhost:8082/v3/api-docs`
- Enrollment Service: `http://localhost:8083/v3/api-docs`

## Development Workflow

Development progress is tracked in [`docs/AI_TASKS.md`](docs/AI_TASKS.md).

Before starting work:

1. Read `docs/AI_TASKS.md`.
2. Find the first unchecked task.
3. Complete only that task and directly related subtasks.
4. Verify the result.
5. Mark the task complete.
6. Commit and push the change.

If work is interrupted, resume from the first unchecked task.

## Known Limitations

- Service containers are defined as Docker Compose build-context placeholders, but Dockerfiles are not included.
- There is no authentication or authorization layer.
- Services use fixed URLs from environment variables; there is no service discovery.
- Database schema creation is handled with Hibernate `ddl-auto=update` for local verification, not a migration tool.
- Cross-service calls are synchronous WebClient requests without circuit breakers or retries.
- End-to-end verification is manual and documented through `docs/AI_TASKS.md`, not automated as a CI pipeline.

## Future Improvements

Potential improvements after the core project is complete:

- Add Dockerfiles for each Spring Boot service and run the full stack from Docker Compose.
- Service discovery
- Centralized configuration
- Resilience patterns with circuit breakers and retries
- Authentication and authorization
- Database migrations with Flyway or Liquibase
- Centralized logging
- Distributed tracing
- Automated integration tests
- CI/CD pipeline
- Frontend dashboard for students and administrators
