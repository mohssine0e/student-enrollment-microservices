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

