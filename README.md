# Salary Management System

Employee salary management for an HR Manager overseeing a 10,000-employee,
multi-country organization. Built for the Incubyte Software Craftsperson
(Java/Angular) take-home assessment.

See [`docs/requirements.md`](docs/requirements.md) for scope/goals,
[`docs/architecture.md`](docs/architecture.md) for the design, and
[`docs/ai-usage.md`](docs/ai-usage.md) for how AI tools were used.

## Stack

- **Backend**: Java 21, Spring Boot 3, Spring Data JPA, Flyway, PostgreSQL
- **Frontend**: Angular (standalone components), Angular Material
- **Database**: PostgreSQL 17

## Prerequisites

- JDK 17+ (no global Maven needed — the project uses the Maven wrapper `mvnw`)
- Node.js 18+ (no global Angular CLI needed — use `npx ng`)
- PostgreSQL running locally

## Database setup

```bash
psql -U postgres -c "CREATE DATABASE salary_management;"
```

The backend connects as `postgres` (see `backend/src/main/resources/application.yml`
for connection settings) and runs Flyway migrations automatically on startup.

## Running the backend

```bash
cd backend
./mvnw spring-boot:run
```

API available at `http://localhost:8080/api/v1`, Swagger UI at
`http://localhost:8080/swagger-ui.html`.

### Seeding 10,000 employees

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=seed
```

Running with the `seed` profile populates the database with 10,000 synthetic
employees and salary history on startup (safe to run once; it checks for
existing data first).

## Running the frontend

```bash
cd frontend
npm install
npx ng serve
```

App available at `http://localhost:4200`.

## Running tests

```bash
# Backend
cd backend && ./mvnw test

# Frontend
cd frontend && npm test
```
