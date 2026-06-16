# Project Technology Stack Explanation

This document outlines the architecture, design patterns, and technologies used to build the Student Enrollment System. The project follows a modern **microservices architecture** on the backend and a **Single Page Application (SPA)** architecture on the frontend.

---

## 1. Backend Architecture (Spring Boot Microservices)

The backend is composed of several specialized, loosely coupled services built with **Java** and **Spring Boot 3**. This separation of concerns allows for independent scaling, deployment, and database management.

### Services
- **Student Service (Port 8081)**: Manages student identity and profiles. Uses its own dedicated MySQL database.
- **Course Service (Port 8082)**: Manages the course catalog and metadata. Uses its own dedicated MySQL database.
- **Enrollment Service (Port 8083)**: The core business logic service that links students to courses. It enforces rules like capacity limits (max 3 students per course) and cancellation windows (24 hours). This service communicates synchronously with the Student and Course services to validate data.
- **API Gateway (Port 8080)**: Built with **Spring Cloud Gateway**. It acts as the single entry point for all frontend requests, routing traffic dynamically based on paths (e.g., `/students/**` to Student Service, `/courses/**` to Course Service). It also handles global CORS configuration.

### Data Layer
- **MySQL**: Each microservice has its own isolated MySQL database running in Docker containers (`student-db`, `course-db`, `enrollment-db`). This adheres strictly to the "Database per Service" pattern.
- **Spring Data JPA & Hibernate**: Used for Object-Relational Mapping (ORM) to interact with the databases efficiently.

### API Documentation
- **Springdoc OpenAPI (Swagger)**: Automatically generates interactive REST API documentation for each microservice, accessible via their respective `/swagger-ui/index.html` endpoints.

---

## 2. Frontend Architecture (React + Vite)

The frontend is a lightweight, blazing-fast React application built for a premium user experience.

### Core Technologies
- **React (v18)**: Component-based library for building interactive user interfaces.
- **Vite**: A next-generation frontend tooling that provides incredibly fast Hot Module Replacement (HMR) and optimized production builds.

### Networking & API Communication
- **Vite Proxy**: Instead of relying on the browser to handle cross-origin requests (which often leads to strict CORS errors), we use Vite's built-in development proxy. All requests to `/api` are seamlessly routed to the Spring API Gateway (`http://localhost:8080`). This avoids CORS issues entirely and provides a much cleaner networking layer.
- **Native Fetch API**: Used for making asynchronous HTTP requests to our microservices.

### Styling & UI/UX
- **Tailwind CSS**: A utility-first CSS framework used to rapidly build custom, responsive designs without leaving the HTML.
- **Premium Glassmorphism**: The UI features modern design trends including "glass" effects (using backdrop-blur), subtle mesh gradients, and micro-animations to create a premium, engaging experience.
- **Google Fonts**: Uses 'Inter' and 'Outfit' to provide highly legible, aesthetically pleasing typography.

---

## 3. DevOps & Deployment

- **Docker & Docker Compose**: Used to containerize the database tier, ensuring a consistent development environment across different machines.
- **Bash Scripts**: Custom scripts (`start-all.sh`, `stop-all.sh`) are used to orchestrate the startup of the databases, backend services, and frontend in the correct dependency order.
