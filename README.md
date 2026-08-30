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

## Deploying (free tier: Render + Vercel)

### 1. Backend + database on Render

1. Sign in at [render.com](https://render.com) and click **New > PostgreSQL**.
   Pick the free plan, any name/region. Once it's up, open it and copy the
   **Hostname**, **Port**, **Database**, **Username**, and **Password** from
   the "Connections" section.
2. Click **New > Web Service**, connect this GitHub repo, and set:
   - **Root Directory**: `backend`
   - **Runtime**: Docker (it will pick up `backend/Dockerfile` automatically)
   - **Instance Type**: Free
3. Add these environment variables on the web service:
   | Key | Value |
   |-----|-------|
   | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<Hostname>:<Port>/<Database>` |
   | `SPRING_DATASOURCE_USERNAME` | `<Username>` |
   | `SPRING_DATASOURCE_PASSWORD` | `<Password>` |
   | `SPRING_PROFILES_ACTIVE` | `seed` (first deploy only — seeds 10k employees; the seeder skips itself if data already exists, so it's safe to leave on, but you can remove it after the first successful deploy) |
4. Deploy. Once live, note the public URL Render gives you (e.g.
   `https://salary-management-api.onrender.com`) — you'll need it in step 2.
   Free-tier services spin down after inactivity and take ~30–60s to wake up
   on the next request; that's expected, not a bug.

### 2. Frontend on Vercel

1. Edit `frontend/src/environments/environment.prod.ts` and replace
   `PLACEHOLDER_BACKEND_URL` with the Render URL from step 1 (no trailing
   slash), then commit and push.
2. Sign in at [vercel.com](https://vercel.com), **Add New > Project**, import
   this repo, and set **Root Directory** to `frontend`. Vercel picks up
   `frontend/vercel.json` automatically for the build command, output
   directory, and SPA routing fallback.
3. Deploy. Vercel gives you a public URL for the app.

### 3. Lock down CORS (optional but recommended)

By default the backend accepts requests from any origin
(`APP_CORS_ALLOWED_ORIGINS=*`) so step 1 and step 2 don't have to happen in a
specific order. Once you have the Vercel URL, go back to the Render web
service's environment variables and set `APP_CORS_ALLOWED_ORIGINS` to that
exact URL, then redeploy — this is what a real deployment would do instead of
leaving it open to any origin.
