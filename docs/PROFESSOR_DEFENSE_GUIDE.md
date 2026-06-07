# Professor Defense Guide

This guide is written in the first person so I can use it directly when presenting or defending the project.

## Project Presentation

Hello professor. My project is a microservices-based student enrollment system built with Spring Boot. The goal is to manage students, courses, and enrollments while applying real business rules.

I divided the backend into three main services: Student Service, Course Service, and Enrollment Service. I also added an API Gateway so the client has one entry point. Each service has its own MySQL database, which follows the microservices principle of data ownership.

A student enrolls using their CNIE and a course id. Before creating the enrollment, the Enrollment Service verifies that the student exists by calling Student Service and verifies that the course exists by calling Course Service. I used WebClient for these service-to-service calls.

The Enrollment Service stores only the enrollment id, student id, course id, and enrollment date. It does not store student names, CNIE values, or course titles because those belong to the Student and Course services. For the dashboard, the Enrollment Service fetches course information from Course Service and calculates whether each enrollment can still be cancelled.

The project also includes Swagger documentation, DTOs, validation, exception handling, Docker Compose for MySQL databases, and startup scripts for local execution.

## Architecture Explanation

I chose this structure:

- The client calls the API Gateway.
- The API Gateway routes requests to the correct backend service.
- Student Service manages student data.
- Course Service manages course data.
- Enrollment Service manages enrollment rules and communicates with Student and Course services using WebClient.

The communication is simple:

- `/students/**` routes to Student Service.
- `/courses/**` routes to Course Service.
- `/enrollments/**` routes to Enrollment Service.
- Enrollment Service calls Student Service to verify CNIE.
- Enrollment Service calls Course Service to verify course ids and retrieve course titles.

## Database Design

I used one database per business service:

- `student_db` for student data.
- `course_db` for course data.
- `enrollment_db` for enrollment data.

The Enrollment database stores only:

- `id`
- `studentId`
- `courseId`
- `enrolledAt`

I did not store student CNIE, student name, or course title in the Enrollment database because that would duplicate data owned by other services. If a course title changes, the dashboard will show the updated title from Course Service.

## Business Rules

### Maximum 3 Students Per Course

I count enrollments by course id before creating a new enrollment. If the count is already three, the Enrollment Service rejects the request with a conflict response.

### Student Verification

When a user enrolls using CNIE, the Enrollment Service calls Student Service. If the student does not exist, the enrollment is rejected.

### Course Verification

Before enrollment, the Enrollment Service calls Course Service by course id. If the course does not exist, the enrollment is rejected.

### 24-Hour Cancellation Rule

Each enrollment has an `enrolledAt` timestamp. Cancellation is allowed only if the current time is within 24 hours of that timestamp. The dashboard also returns `canCancel` to show this status to the client.

## Technical Choices

### Spring Boot

I used Spring Boot because it is widely used for Java backend applications and makes it easy to build REST APIs quickly.

### JPA

I used Spring Data JPA to simplify database access and avoid writing repetitive SQL for basic CRUD operations.

### MySQL

I used MySQL because it is a common relational database and fits the structured data in this project.

### WebClient

I used WebClient for service-to-service HTTP communication. It is the modern Spring client and is preferred over the older RestTemplate.

### DTOs

I used DTOs to separate API data from internal JPA entities. DTOs also make validation and response control cleaner.

### Swagger

I used Swagger/OpenAPI to document and test the REST endpoints from the browser.

### API Gateway

I used the API Gateway to provide one entry point and hide the internal service ports from the client.

## Questions A Professor Might Ask

### 1. Why did you choose microservices instead of a monolith?

Concise answer: I chose microservices to separate responsibilities and show service communication.

Detailed answer: A monolith would be simpler, but this academic project is meant to demonstrate distributed backend architecture. By separating Student, Course, and Enrollment services, each service owns one domain and one database. This makes the architecture closer to real-world systems.

### 2. What is the main disadvantage of microservices here?

Concise answer: The system is more complex to run and coordinate.

Detailed answer: Instead of starting one application, I must start multiple services and databases. Communication can fail if one service is down, and data consistency is more difficult because data is distributed.

### 3. Why are there three business services?

Concise answer: Because the project has three domains: students, courses, and enrollments.

Detailed answer: Student data, course data, and enrollment rules are different responsibilities. Separating them keeps the code clearer and prevents one service from owning too much logic.

### 4. Why is the API Gateway necessary?

Concise answer: It gives the client one entry point.

Detailed answer: Without the gateway, the frontend would need to know every backend service URL. With the gateway, the client calls one base URL and the gateway routes requests internally.

### 5. Why did you use WebClient?

Concise answer: Enrollment Service needs to call Student and Course services.

Detailed answer: WebClient is the modern Spring HTTP client. I used it so Enrollment Service can verify the student by CNIE and verify the course by id before creating an enrollment.

### 6. Why not use RestTemplate?

Concise answer: RestTemplate is older and no longer the recommended choice.

Detailed answer: Spring recommends WebClient for modern HTTP communication. WebClient also supports reactive and non-blocking use cases, even though this project uses it in a simple way.

### 7. Why are DTOs important?

Concise answer: DTOs protect the internal data model.

Detailed answer: If I expose entities directly, API clients become dependent on database structure. DTOs let me validate input, control output fields, and change entities without breaking the API.

### 8. Why does Enrollment store only ids?

Concise answer: Because Student and Course services own the detailed data.

Detailed answer: Enrollment Service only needs references. Student names and CNIE belong to Student Service, and course titles belong to Course Service. Storing them in Enrollment would duplicate data and create consistency problems.

### 9. What happens if a course title changes?

Concise answer: The dashboard shows the updated title.

Detailed answer: Since Enrollment does not store course titles, it fetches course details from Course Service when building the dashboard. This keeps the title consistent with the source of truth.

### 10. What happens if Student Service is unavailable?

Concise answer: Enrollment creation cannot verify the student and should fail.

Detailed answer: Enrollment Service depends on Student Service for CNIE verification. If Student Service is unavailable, Enrollment Service returns an error instead of creating invalid data.

### 11. What happens if Course Service is unavailable?

Concise answer: Enrollment creation and dashboard course title retrieval are affected.

Detailed answer: Enrollment Service cannot verify course existence or fetch course titles without Course Service. The project handles unavailable service responses with controlled exceptions.

### 12. Why did you separate databases?

Concise answer: Each microservice should own its own data.

Detailed answer: Shared databases couple services together. Separate databases make ownership clear and prevent one service from directly modifying another service's data.

### 13. Why did you choose MySQL?

Concise answer: It is a common relational database and fits structured data.

Detailed answer: Students, courses, and enrollments are relational concepts. MySQL is reliable, familiar, and easy to run locally with Docker.

### 14. Why use JPA?

Concise answer: JPA simplifies database access.

Detailed answer: Spring Data JPA provides repositories, entity mapping, and CRUD operations. This reduces boilerplate SQL and keeps the service layer focused on business logic.

### 15. What is the role of the repository layer?

Concise answer: It handles database access.

Detailed answer: Repositories isolate persistence logic. The service layer calls repositories instead of writing database access code directly.

### 16. What is the role of the service layer?

Concise answer: It contains business logic.

Detailed answer: Controllers receive HTTP requests, but services decide what should happen. For example, Enrollment Service enforces capacity and cancellation rules.

### 17. What is the role of the controller layer?

Concise answer: It exposes REST endpoints.

Detailed answer: Controllers map HTTP requests to service methods, validate request bodies, and return HTTP responses.

### 18. Why use validation annotations?

Concise answer: To reject invalid requests early.

Detailed answer: Validation ensures required fields like CNIE and course id are present and valid before the service layer processes them.

### 19. Why use exception handling?

Concise answer: To return clear API errors.

Detailed answer: Global exception handlers convert business exceptions into consistent HTTP responses, such as 404 for not found or 409 for conflicts.

### 20. How is the maximum of three students enforced?

Concise answer: Enrollment Service counts enrollments by course id before saving.

Detailed answer: If a course already has three enrollments, the service throws a course-full exception and returns a conflict response.

### 21. How is the 24-hour cancellation rule enforced?

Concise answer: The service compares current time with `enrolledAt`.

Detailed answer: If more than 24 hours have passed since enrollment, cancellation is rejected. The dashboard uses the same logic to return `canCancel`.

### 22. Why does the student enroll using CNIE?

Concise answer: CNIE is a real-world student identifier.

Detailed answer: The API accepts CNIE because users may know the student's official identifier better than the internal database id. Student Service translates CNIE into the student id.

### 23. Why does Enrollment use studentId after lookup?

Concise answer: Internal references should use stable ids.

Detailed answer: CNIE is owned by Student Service and may be considered business data. Enrollment stores the student id as a reference to the student record.

### 24. What is Swagger used for?

Concise answer: It documents and tests APIs.

Detailed answer: Swagger shows endpoints, request bodies, and responses in the browser. This is useful for demos and for understanding the backend quickly.

### 25. How does the dashboard work?

Concise answer: It finds a student by CNIE, loads enrollments, then fetches course details.

Detailed answer: Enrollment Service first calls Student Service to get the student. Then it queries its database for enrollments by student id. For each enrollment it calls Course Service to get the course title and calculates `canCancel`.

### 26. How would you scale the system?

Concise answer: I would scale services independently.

Detailed answer: If enrollment traffic is high, I can run more Enrollment Service instances without scaling Student or Course Service. In production I would add service discovery, load balancing, and monitoring.

### 27. How would you improve reliability?

Concise answer: I would add retries, circuit breakers, and monitoring.

Detailed answer: Because services depend on HTTP calls, failures are possible. Resilience4j could add circuit breakers, retries, and fallbacks for temporary failures.

### 28. Why not use a shared database?

Concise answer: A shared database would break service independence.

Detailed answer: If all services use the same database, they become tightly coupled. One schema change could affect multiple services. Separate databases keep boundaries clear.

### 29. What would you add for production?

Concise answer: Authentication, migrations, Dockerfiles, CI/CD, and monitoring.

Detailed answer: The academic version focuses on architecture. A production version should add security, Flyway or Liquibase migrations, container images, automated tests, observability, and deployment automation.

### 30. What did you learn from this project?

Concise answer: I learned how backend services communicate and keep data ownership clear.

Detailed answer: This project helped me understand microservices, API Gateway routing, WebClient communication, DTOs, validation, database separation, business rule enforcement, and API documentation.
