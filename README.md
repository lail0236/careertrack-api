# CareerTrack API

A REST API for tracking job applications, recording status history, and analyzing a user's job-search pipeline. Built with Java 21, Spring Boot, Spring Security, PostgreSQL, Flyway, and Docker Compose.

**Repository:** https://github.com/lail0236/careertrack-api

## Features

- User registration and login with JWT access tokens.
- Password hashing with BCrypt.
- Authenticated, user-scoped application management.
- Status tracking: `SAVED`, `APPLIED`, `ASSESSMENT`, `INTERVIEW`, `OFFER`, and `REJECTED`.
- Application status history.
- Per-user analytics for status counts, application funnel, activity, and historical stage counts.
- Request validation and centralized error handling.
- PostgreSQL schema migrations managed by Flyway.
- OpenAPI documentation and Swagger UI.
- Docker Compose development environment.
- Automated tests and CI workflow.

## Technology stack

| Area | Technology |
|---|---|
| Language | Java 21 |
| Backend | Spring Boot |
| Security | Spring Security, JWT, BCrypt |
| Database | PostgreSQL |
| Database migrations | Flyway |
| API documentation | OpenAPI / Swagger UI |
| Containers | Docker, Docker Compose |
| Build and tests | Maven Wrapper |

## Architecture overview

```text
Client
  |
  | HTTP + JSON; JWT bearer token for protected routes
  v
Spring Boot REST API
  |-- Authentication and authorization
  |-- Application management
  |-- Status history
  |-- Analytics
  |
  v
PostgreSQL
  ^
  | Flyway-managed schema migrations
```

## Prerequisites

- Git
- Docker Desktop with Docker Compose
- Java 21 is useful for local development; the container workflow builds and runs the API in Docker.

## Run locally with Docker Compose

1. Clone the repository and enter its directory:

   ```powershell
   git clone https://github.com/lail0236/careertrack-api.git
   cd careertrack-api
   ```

2. Create a local environment file from the example:

   ```powershell
   Copy-Item .env.example .env
   ```

3. Open `.env` and replace every placeholder with local development values. Use a strong database password and a JWT secret of at least 32 bytes. Do not commit `.env` or paste its secrets into issue reports.

4. Build and start the services:

   ```powershell
   docker compose up --build -d
   ```

5. Check service status and API health:

   ```powershell
   docker compose ps
   Invoke-RestMethod http://localhost:8080/api/health
   ```

6. Open Swagger UI:

   http://localhost:8080/swagger-ui/index.html

   OpenAPI JSON is available at:

   http://localhost:8080/v3/api-docs

### Stop the services

```powershell
docker compose down
```

This preserves the named PostgreSQL data volume. To stop the services and **delete local database data**, use `docker compose down -v` only when you intentionally want a clean database.

## Configuration

The Compose setup reads local secrets from `.env`. Start with `.env.example` and check the actual Compose configuration if variable names change. Typical configuration values include the PostgreSQL database name and username, PostgreSQL password, and JWT signing secret.

Never use example credentials or development secrets in a public deployment. Keep `.env` out of version control.

## API overview

The local API base URL is `http://localhost:8080`.

| Method | Endpoint | Purpose | Authentication |
|---|---|---|---|
| `GET` | `/api/health` | Health check | Public |
| `POST` | `/api/auth/register` | Register a user | Public |
| `POST` | `/api/auth/login` | Log in and receive a JWT | Public |
| `GET` | `/api/applications` | List the current user's applications | Required |
| `POST` | `/api/applications` | Create an application | Required |
| `GET` | `/api/applications/{id}` | Get one owned application | Required |
| `PATCH` | `/api/applications/{id}/status` | Change an application's status | Required |
| `GET` | `/api/applications/{id}/history` | Read history for an owned application | Required |
| `GET` | `/api/analytics/status-counts` | Counts grouped by current status | Required |
| `GET` | `/api/analytics/funnel` | Application funnel metrics | Required |
| `GET` | `/api/analytics/activity` | Application activity | Required |
| `GET` | `/api/analytics/historical-stage-counts` | Historical stage counts | Required |

For request examples, see [`docs/API_EXAMPLES.md`](docs/API_EXAMPLES.md). Swagger UI exposes the schemas available in the running application.

## Authentication flow

1. Register through `POST /api/auth/register`.
2. Log in through `POST /api/auth/login`.
3. Copy the returned `accessToken`.
4. Send it on protected requests using `Authorization: Bearer YOUR_ACCESS_TOKEN`.

Access tokens are short-lived. Log in again when a token expires.

## Testing

Run the automated test suite from the repository root:

```powershell
.\mvnw.cmd -B test
```

Build the application:

```powershell
.\mvnw.cmd -B package
```

For a Docker-based smoke test:

```powershell
docker compose up --build -d
Invoke-RestMethod http://localhost:8080/api/health
```

The project has also been manually checked for authentication and cross-user isolation: unauthenticated protected requests return HTTP 401, and attempts to retrieve another user's application or history return HTTP 404. Analytics were checked with two test users to confirm that each user's results exclude the other user's data.

## Security notes

- Passwords are stored as BCrypt hashes, not plaintext.
- Protected endpoints require JWT authentication.
- Application queries and analytics are scoped to the authenticated user.
- Keep `.env` and real credentials out of Git.
- Rotate any credential that was ever exposed in a public commit or log.
- Before public deployment, configure production-grade secrets, HTTPS, database access restrictions, backups, and appropriate logging.

## Screenshots for the portfolio

Suggested screenshots to add under `docs/screenshots/`:

1. `swagger-overview.png` — Swagger UI endpoint overview.
2. `auth-register-login.png` — authentication endpoints and schemas.
3. `application-endpoints.png` — application CRUD, status, and history endpoints.
4. `analytics-endpoints.png` — analytics endpoint group.
5. `ci-passing.png` — successful GitHub Actions run.

Capture screenshots from your own running application or GitHub repository. Do not include tokens, passwords, personal data, or `.env` contents.

## Project status

The core API, JWT authentication, per-user data isolation, analytics, database migrations, Docker workflow, and automated tests have been implemented. This repository is being prepared as a software engineering portfolio project.

## License

No license is specified here. If you want others to reuse the code, choose and add a license explicitly.
