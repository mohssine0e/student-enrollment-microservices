# Technical Decisions

This file records shared project configuration choices as tasks are completed.

## Java Version

- Java 21 is the project baseline.
- Service Maven builds set `<java.version>21</java.version>`.
- The local machine currently runs a newer JDK, but Maven compiles the services for Java 21.

## Spring Boot Version

- Spring Boot 3.3.6 is the project baseline for all services.
- Each service currently uses `spring-boot-starter-parent` version `3.3.6`.
