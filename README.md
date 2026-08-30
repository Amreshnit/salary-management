# Salary Management System

Employee salary management for an HR Manager overseeing a 10,000-employee,
multi-country organization. Built for the Incubyte Software Craftsperson
(Java/Angular) take-home assessment.

See [`docs/requirements.md`](docs/requirements.md) for scope/goals,
[`docs/architecture.md`](docs/architecture.md) for the design, and
[`docs/ai-usage.md`](docs/ai-usage.md) for how AI tools were used.

## Live demo

- **App**: https://salary-management-one.vercel.app
- **API**: https://salary-management-api-wqir.onrender.com/api/v1

Both are on free hosting tiers. The backend spins down after inactivity and
can take 30-60 seconds to respond on the first request after a while —
that's expected, not a bug.

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

**Alternative: pure-SQL seeding.** If you'd rather generate and insert the
10,000 rows directly via `psql` without booting the backend, run
`backend/scripts/seed-employees.sql` against an already-migrated database
(start the backend once with the default profile first so Flyway creates the
tables, then stop it):

```bash
psql -U postgres -d salary_management -f backend/scripts/seed-employees.sql
```

It generates realistic, varied data with plain SQL (`random()`, array
lookups, a PL/pgSQL loop) — no Java/Faker involved — and is idempotent the
same way: it checks `employee` for existing rows first and skips itself if
any are found, so running it twice never duplicates data.

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

## Deploying to AWS free tier (alternative)

This runs the whole stack on AWS's free tier instead: RDS for the database,
a single EC2 instance for both the backend and the frontend (one instance
keeps you inside the free tier's 750 hours/month rather than splitting it
across two). AWS account setup and the exact current free-tier terms are
something only you can do/confirm on your account — this is the sequence to
follow once you're signed in.

### 1. RDS (PostgreSQL, free tier)

1. AWS Console → **RDS** → **Create database** → **Standard create** →
   Engine **PostgreSQL** → template **Free tier**.
2. Instance identifier `salary-management-db`, master username `postgres`,
   set a master password (write it down).
3. Instance class stays on the free-tier-eligible default (`db.t3.micro` or
   `db.t4g.micro`, 20 GB storage).
4. Under **Connectivity**, set **Public access** to **No** (the app will
   reach it from inside the same VPC via the EC2 instance, not from the
   internet) — note the **VPC** it's created in, you'll launch EC2 into the
   same one.
5. Create it, wait for it to become "Available", then open it and copy the
   **Endpoint** and **Port** (default 5432).

### 2. EC2 (backend + frontend, free tier)

1. **EC2** → **Launch instance** → Amazon Linux 2023, instance type
   `t2.micro` or `t3.micro` (free-tier eligible), same VPC as the RDS
   instance. Create/reuse a key pair (needed for SSH).
2. **Security group** for this instance: allow inbound **22** (SSH, from
   your IP), **80** (HTTP, from anywhere) — nothing else needs to be public,
   since nginx will reverse-proxy `/api` to the backend running on
   `localhost:8080` internally.
3. Go back to the **RDS security group** and add an inbound rule: PostgreSQL
   (5432) from the **EC2 instance's security group** (not from `0.0.0.0/0`) —
   this is what lets the app reach the database without exposing it publicly.
4. SSH into the instance and install Docker and git:
   ```bash
   sudo yum install -y docker git
   sudo systemctl enable --now docker
   sudo usermod -aG docker ec2-user   # log out/in once for this to take effect
   ```
5. Clone the repo and build/run the backend container, pointing it at RDS:
   ```bash
   git clone https://github.com/Amreshnit/salary-management.git
   cd salary-management/backend
   docker build -t salary-backend .
   docker run -d --name salary-backend --restart unless-stopped \
     -p 127.0.0.1:8080:8080 \
     -e SPRING_DATASOURCE_URL="jdbc:postgresql://<RDS Endpoint>:5432/postgres" \
     -e SPRING_DATASOURCE_USERNAME=postgres \
     -e SPRING_DATASOURCE_PASSWORD="<the password you set>" \
     -e SPRING_PROFILES_ACTIVE=seed \
     salary-backend
   ```
   (`-p 127.0.0.1:8080:8080` keeps the backend reachable only from nginx on
   the same machine, not directly from the internet.)
6. Build the frontend (pointed at this same instance, since nginx will serve
   both from one origin — so the API base URL can stay empty/relative,
   exactly like local dev) and install nginx to serve it:
   ```bash
   sudo yum install -y nginx nodejs
   cd ~/salary-management/frontend
   npm install && npx ng build --configuration production
   sudo cp -r dist/salary-management-ui/browser/* /usr/share/nginx/html/
   ```
7. Configure nginx to serve the Angular build and reverse-proxy `/api` to the
   backend container (`/etc/nginx/conf.d/salary-management.conf`):
   ```nginx
   server {
       listen 80;
       root /usr/share/nginx/html;
       index index.html;

       location /api/ {
           proxy_pass http://127.0.0.1:8080;
       }

       location / {
           try_files $uri $uri/ /index.html;
       }
   }
   ```
   ```bash
   sudo systemctl enable --now nginx
   sudo systemctl restart nginx
   ```
8. Visit the EC2 instance's public IP/DNS in a browser — the app should load
   and the employee list should show the seeded data.

Since nginx serves the frontend and proxies `/api` on the same origin, there's
no CORS configuration needed here (unlike the Render/Vercel path, where the
frontend and backend are on different domains).
