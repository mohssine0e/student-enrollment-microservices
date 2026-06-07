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
