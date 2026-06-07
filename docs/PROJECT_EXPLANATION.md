# Project Explanation

## Why This Project Exists

This mini project demonstrates how to build a student enrollment system using a microservices architecture with Spring Boot. It is an academic project, so the goal is not only to make the application work, but also to show clear software architecture, separation of responsibilities, API communication, database design, and business rules.

## Problem Being Solved

The system solves a simple university-style enrollment problem:

- Students must be registered.
- Courses must be available.
- A student can enroll in a course using their CNIE and the course id.
- A course can have a maximum of three students.
- A student can cancel an enrollment only during the first 24 hours.
- A dashboard must show the student's enrolled courses and whether each enrollment can still be cancelled.

## Why Microservices Were Chosen

Microservices were chosen to separate the system into small independent parts. Each service has one clear responsibility and owns its own data. This makes the architecture easier to explain, easier to scale, and closer to real-world distributed applications.

A monolithic application would also work for this project, but microservices are useful here because they show service communication, API Gateway routing, and data ownership.

## Why There Are 3 Main Services

There are three main business services because the project has three main domains:

- `student-service`: manages student data and CNIE lookup.
- `course-service`: manages course data.
- `enrollment-service`: manages enrollments and applies enrollment business rules.

This separation keeps each service focused. The Enrollment Service does not manage student names or course titles directly; it asks the correct service when it needs that information.

## Why An API Gateway Is Used

The API Gateway is used as a single entry point for the application. Instead of clients calling many backend services directly, they call one gateway:

- `/students/**` goes to Student Service.
- `/courses/**` goes to Course Service.
- `/enrollments/**` goes to Enrollment Service.

This makes the client side simpler and centralizes routing.

## Why WebClient Is Used

The Enrollment Service needs to verify students and courses before creating an enrollment. It uses WebClient to call:

- Student Service, to find a student by CNIE.
- Course Service, to verify a course exists and retrieve its title for the dashboard.

WebClient is the modern Spring HTTP client and is preferred over the older RestTemplate.

## Why DTOs Are Used

DTOs are used to control what data enters and leaves each service. They help:

- Avoid exposing JPA entities directly.
- Validate incoming requests.
- Return only the fields needed by the client.
- Protect the internal database model from external API changes.

For example, the Enrollment API returns enrollment information, but it does not store or own student names or course titles.

## Why Swagger Is Used

Swagger/OpenAPI is used to document and test the REST APIs in the browser. It helps students, professors, and developers quickly understand available endpoints, request bodies, and responses.

Swagger URLs:

- Student Service: `http://localhost:8081/swagger-ui/index.html`
- Course Service: `http://localhost:8082/swagger-ui/index.html`
- Enrollment Service: `http://localhost:8083/swagger-ui/index.html`

## Why Each Database Is Separated

Each service has its own MySQL database:

- Student Service owns `student_db`.
- Course Service owns `course_db`.
- Enrollment Service owns `enrollment_db`.

This follows the microservices principle that each service owns its own data. The Enrollment database stores only:

- `id`
- `studentId`
- `courseId`
- `enrolledAt`

It does not store student CNIE, student names, or course titles. This avoids duplicated data and keeps the source of truth clear.

## Advantages

- Clear separation of responsibilities.
- Each service can evolve independently.
- The API Gateway gives one entry point.
- Each database has a clear owner.
- WebClient demonstrates real service-to-service communication.
- Swagger makes the APIs easy to test and present.
- The architecture is close to real-world backend systems.

## Disadvantages

- More complex than a monolithic application.
- More services must be started and monitored.
- Service communication can fail if one service is unavailable.
- Data consistency is harder because data is split across databases.
- More configuration is required for ports, URLs, and databases.

## Simple Summary

This project is a microservices-based student enrollment system. Student data, course data, and enrollment data are separated into different services and databases. The API Gateway routes requests, and the Enrollment Service uses WebClient to verify students and courses. The project demonstrates backend architecture, business rules, DTOs, validation, API documentation, and service communication in a simple academic context.
