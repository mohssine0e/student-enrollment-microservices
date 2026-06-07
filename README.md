# Microservices-Based Student Enrollment System

## Project Overview

This repository contains a Spring Boot microservices system for managing students, courses, and course enrollments. It implements independent Student, Course, and Enrollment services behind an API Gateway, with each data-owning service using its own MySQL database.

The system is built around four services:

- `student-service`
- `course-service`
- `enrollment-service`
- `api-gateway`

Students are identified by CNIE for enrollment. The Enrollment Service validates students and courses through WebClient calls, enforces a maximum of three students per course, supports cancellation only within 24 hours, and exposes a dashboard response with course titles and `canCancel` values.
