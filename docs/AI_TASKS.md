# AI Tasks - Microservices-Based Student Enrollment System

This file is the single source of truth for project progress. Work must resume from the first unchecked task in this document.

## Agent Workflow Rules

- Before doing any work, read `docs/AI_TASKS.md`.
- Find the first unchecked task.
- Execute only that task and any directly related subtasks.
- After finishing:
  - mark the task as completed
  - verify the task result
  - commit changes
  - push to GitHub
- If interrupted, resume from the first unchecked task.
- Do not skip unchecked tasks.
- Do not start a later phase until all previous phase tasks are completed.
- Keep implementation changes small and traceable to one checklist item.

## Commit Strategy

- Use small commits.
- Make one logical change per commit.
- Commit after each completed task.
- Push after each commit.
- Use imperative commit messages that include the task number.

Example:

```bash
git add .
git commit -m "complete task 1.3 configure student database"
git push
```

## Phase 0 - Repository Preparation

- [x] 0.1 Verify repository root.
- [x] 0.2 Create `docs/` directory.
- [x] 0.3 Create `docs/AI_TASKS.md`.
- [x] 0.4 Create README template.
- [x] 0.5 Create `.gitignore`.
- [x] 0.6 Create placeholder `docker-compose.yml`.
- [x] 0.7 Define agent workflow rules.
- [x] 0.8 Define commit strategy.
- [x] 0.9 Confirm no Spring Boot service code has been generated.

## Phase 1 - Project Skeletons

- [x] 1.1 Generate `student-service` Spring Boot project skeleton.
- [x] 1.2 Verify `student-service` starts with the default application class.
- [x] 1.3 Commit and push `student-service` skeleton.
- [x] 1.4 Generate `course-service` Spring Boot project skeleton.
- [x] 1.5 Verify `course-service` starts with the default application class.
- [x] 1.6 Commit and push `course-service` skeleton.
- [x] 1.7 Generate `enrollment-service` Spring Boot project skeleton.
- [x] 1.8 Verify `enrollment-service` starts with the default application class.
- [x] 1.9 Commit and push `enrollment-service` skeleton.
- [x] 1.10 Generate `api-gateway` Spring Boot project skeleton.
- [x] 1.11 Verify `api-gateway` starts with the default application class.
- [x] 1.12 Commit and push `api-gateway` skeleton.

## Phase 2 - Shared Project Configuration

- [x] 2.1 Choose Java version.
- [x] 2.2 Choose Spring Boot version.
- [x] 2.3 Confirm Maven build configuration for all services.
- [x] 2.4 Add validation dependency to `student-service`.
- [x] 2.5 Add validation dependency to `course-service`.
- [x] 2.6 Add validation dependency to `enrollment-service`.
- [x] 2.7 Add Spring Data JPA dependency to `student-service`.
- [x] 2.8 Add Spring Data JPA dependency to `course-service`.
- [x] 2.9 Add Spring Data JPA dependency to `enrollment-service`.
- [x] 2.10 Add database driver dependency to `student-service`.
- [x] 2.11 Add database driver dependency to `course-service`.
- [x] 2.12 Add database driver dependency to `enrollment-service`.
- [x] 2.13 Add WebFlux dependency to `enrollment-service` for WebClient.
- [x] 2.14 Add Spring Cloud Gateway dependency to `api-gateway`.
- [x] 2.15 Verify all Maven builds.
- [x] 2.16 Commit and push shared configuration.

## Phase 3 - Student Service

- [x] 3.1 Configure `student-service` port `8081`.
- [x] 3.2 Configure `student-service` database connection placeholders.
- [x] 3.3 Create Student entity.
- [x] 3.4 Add unique constraint for student CNIE.
- [x] 3.5 Create Student repository.
- [x] 3.6 Create student request DTO.
- [x] 3.7 Create student response DTO.
- [x] 3.8 Create student mapper.
- [x] 3.9 Create student service interface.
- [x] 3.10 Implement create student logic.
- [x] 3.11 Implement list students logic.
- [x] 3.12 Implement get student by id logic.
- [x] 3.13 Implement get student by CNIE logic.
- [x] 3.14 Implement update student logic.
- [x] 3.15 Implement delete student logic.
- [x] 3.16 Create student controller.
- [x] 3.17 Add student validation rules.
- [x] 3.18 Add student not found exception.
- [x] 3.19 Add duplicate CNIE exception.
- [x] 3.20 Add global exception handler.
- [x] 3.21 Add Swagger/OpenAPI configuration.
- [x] 3.22 Verify student API endpoints.
- [x] 3.23 Commit and push Student Service.

## Phase 4 - Course Service

- [x] 4.1 Configure `course-service` port `8082`.
- [x] 4.2 Configure `course-service` database connection placeholders.
- [x] 4.3 Create Course entity.
- [x] 4.4 Create Course repository.
- [x] 4.5 Create course request DTO.
- [x] 4.6 Create course response DTO.
- [x] 4.7 Create course mapper.
- [x] 4.8 Create course service interface.
- [x] 4.9 Implement create course logic.
- [x] 4.10 Implement list courses logic.
- [x] 4.11 Implement get course by id logic.
- [x] 4.12 Implement update course logic.
- [x] 4.13 Implement delete course logic.
- [x] 4.14 Create course controller.
- [x] 4.15 Add course validation rules.
- [x] 4.16 Add course not found exception.
- [x] 4.17 Add global exception handler.
- [x] 4.18 Add Swagger/OpenAPI configuration.
- [x] 4.19 Verify course API endpoints.
- [x] 4.20 Commit and push Course Service.

## Phase 5 - Enrollment Service Foundation

- [x] 5.1 Configure `enrollment-service` port `8083`.
- [x] 5.2 Configure `enrollment-service` database connection placeholders.
- [x] 5.3 Create Enrollment entity.
- [x] 5.4 Ensure Enrollment stores only enrollment id, student id, course id, and enrolled date/time.
- [x] 5.5 Create Enrollment repository.
- [x] 5.6 Create `EnrollmentRequestDTO`.
- [x] 5.7 Create `EnrollmentResponseDTO`.
- [x] 5.8 Create `StudentDTO`.
- [x] 5.9 Create `CourseDTO`.
- [x] 5.10 Create `StudentDashboardDTO`.
- [x] 5.11 Create `DashboardCourseDTO`.
- [x] 5.12 Create enrollment mapper.
- [x] 5.13 Commit and push Enrollment Service foundation.

## Phase 6 - Enrollment Service Integration

- [x] 6.1 Configure WebClient bean.
- [x] 6.2 Configure Student Service base URL placeholder.
- [x] 6.3 Configure Course Service base URL placeholder.
- [x] 6.4 Implement WebClient call to find student by CNIE.
- [x] 6.5 Implement WebClient call to find course by id.
- [x] 6.6 Handle Student Service unavailable response.
- [x] 6.7 Handle Course Service unavailable response.
- [x] 6.8 Verify WebClient integration with mocked or running services.
- [x] 6.9 Commit and push Enrollment Service integration.

## Phase 7 - Enrollment Business Rules

- [x] 7.1 Implement course enrollment count query.
- [x] 7.2 Enforce maximum 3 students per course.
- [x] 7.3 Add course full exception.
- [x] 7.4 Implement enrollment creation by CNIE.
- [x] 7.5 Prevent enrollment when student does not exist.
- [x] 7.6 Prevent enrollment when course does not exist.
- [x] 7.7 Implement enrollment deletion lookup.
- [x] 7.8 Enforce 24-hour cancellation rule.
- [x] 7.9 Add cancellation period expired exception.
- [x] 7.10 Add enrollment not found exception.
- [x] 7.11 Add global exception handler.
- [x] 7.12 Verify enrollment business rules.
- [x] 7.13 Commit and push Enrollment business rules.

## Phase 8 - Dashboard

- [x] 8.1 Implement repository query for enrollments by student id.
- [x] 8.2 Implement dashboard lookup by CNIE.
- [x] 8.3 Fetch dashboard course details using WebClient.
- [x] 8.4 Compute `canCancel` from enrolled date/time.
- [x] 8.5 Create dashboard response mapping.
- [x] 8.6 Create dashboard endpoint.
- [x] 8.7 Verify dashboard response content.
- [x] 8.8 Commit and push Dashboard feature.

## Phase 9 - API Gateway

- [x] 9.1 Configure gateway port `8080`.
- [x] 9.2 Configure route `/students/**` to `student-service`.
- [x] 9.3 Configure route `/courses/**` to `course-service`.
- [x] 9.4 Configure route `/enrollments/**` to `enrollment-service`.
- [x] 9.5 Verify student route through gateway.
- [x] 9.6 Verify course route through gateway.
- [x] 9.7 Verify enrollment route through gateway.
- [x] 9.8 Commit and push API Gateway.

## Phase 10 - Swagger And API Documentation

- [x] 10.1 Add Swagger/OpenAPI dependency to `student-service`.
- [x] 10.2 Add Swagger/OpenAPI dependency to `course-service`.
- [x] 10.3 Add Swagger/OpenAPI dependency to `enrollment-service`.
- [x] 10.4 Document Student Service endpoints.
- [x] 10.5 Document Course Service endpoints.
- [x] 10.6 Document Enrollment Service endpoints.
- [x] 10.7 Verify Swagger UI for Student Service.
- [x] 10.8 Verify Swagger UI for Course Service.
- [x] 10.9 Verify Swagger UI for Enrollment Service.
- [x] 10.10 Commit and push Swagger documentation.

## Phase 11 - Docker Setup

Local database engine: MySQL 8.4 LTS.

- [x] 11.1 Choose database engine for local development.
- [x] 11.2 Define Student Service database container.
- [x] 11.3 Define Course Service database container.
- [x] 11.4 Define Enrollment Service database container.
- [x] 11.5 Define Docker network.
- [x] 11.6 Define named volumes.
- [x] 11.7 Add environment variable placeholders.
- [x] 11.8 Add service container placeholders.
- [ ] 11.9 Verify `docker compose config`.
- [ ] 11.10 Commit and push Docker setup.

## Phase 12 - End-To-End Verification

- [ ] 12.1 Start databases.
- [ ] 12.2 Start Student Service.
- [ ] 12.3 Start Course Service.
- [ ] 12.4 Start Enrollment Service.
- [ ] 12.5 Start API Gateway.
- [ ] 12.6 Create test student.
- [ ] 12.7 Create test course.
- [ ] 12.8 Enroll student by CNIE.
- [ ] 12.9 Verify maximum 3 students per course.
- [ ] 12.10 Verify dashboard by CNIE.
- [ ] 12.11 Verify 24-hour cancellation rule.
- [ ] 12.12 Verify gateway routes.
- [ ] 12.13 Commit and push verification updates.

## Phase 13 - README Completion

- [ ] 13.1 Update project overview with final implementation details.
- [ ] 13.2 Add final architecture diagram.
- [ ] 13.3 Add final service descriptions.
- [ ] 13.4 Add final database names.
- [ ] 13.5 Add final port table.
- [ ] 13.6 Add final technology versions.
- [ ] 13.7 Add final running instructions.
- [ ] 13.8 Add final API documentation links.
- [ ] 13.9 Add known limitations.
- [ ] 13.10 Add future improvements.
- [ ] 13.11 Commit and push README completion.
