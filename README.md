# CareerTrack API

A REST API for tracking job applications, recording status history, and analyzing a user's job-search pipeline. Built with Java 21, Spring Boot, Spring Security, PostgreSQL, Flyway, and Docker Compose.

**Repository:** https://github.com/lail0236/careertrack-api

**Live API:** https://careertrack-api-0s5d.onrender.com

**Health check:** https://careertrack-api-0s5d.onrender.com/api/health

## Features

- User registration and login with JWT access tokens.
- Password hashing with BCrypt.
- Authenticated, user-scoped application management.
- Create, retrieve, update, and delete job applications.
- Status tracking: `SAVED`, `APPLIED`, `ASSESSMENT`, `INTERVIEW`, `OFFER`, and `REJECTED`.
- Application status history.
- Per-user status, funnel, activity, and historical analytics.
- Request validation and centralized error handling.
- PostgreSQL migrations managed by Flyway.
- OpenAPI 3.1 documentation and Swagger UI for local development.
- Docker Compose development environment.
- Automated tests and CI workflow.
- Deployed Spring Boot API with a managed PostgreSQL database.

## Technology stack

| Area | Technology |
|---|---|
| Language | Java 21 |
| Backend | Spring Boot |
| Security | Spring Security, JWT, BCrypt |
| Database | PostgreSQL |
| Migrations | Flyway |
| API documentation | OpenAPI 3.1 / Swagger UI |
| Containers | Docker, Docker Compose |
| Build and tests | Maven Wrapper |
| Deployment | Render Web Service, Neon PostgreSQL |
| CI | GitHub Actions |

## Architecture

### Production deployment

```text
Client
  | HTTPS + JSON; JWT bearer token for protected routes
  v
Render Free Web Service
  |-- Spring Boot REST API (Docker)
  |-- Authentication and authorization
  |-- Application management
  |-- Status history
  |-- Analytics
  |
  | PostgreSQL connection over SSL
  v
Neon Managed PostgreSQL
  |-- Persistent application and user data
  |-- Flyway-managed schema migrations
```

The production API and database use free-tier services. Free-tier services have usage limits and availability trade-offs; see [Free-tier notes](#free-tier-notes).

### Local architecture

```text
Client
  | HTTP + JSON; JWT bearer token for protected routes
  v
Spring Boot REST API
  |-- Authentication and authorization
  |-- Application management
  |-- Status history
  |-- Analytics
  v
PostgreSQL
  ^ Flyway-managed schema migrations
```

Protected application, history, and analytics operations are scoped to the authenticated user's identity.

## Live deployment

- **API base URL:** https://careertrack-api-0s5d.onrender.com
- **Health endpoint:** https://careertrack-api-0s5d.onrender.com/api/health
- **Hosting:** Render Free Web Service (Docker)
- **Database:** Neon Free PostgreSQL
- **Production profile:** Spring `prod`
- **API documentation:** Swagger UI and OpenAPI endpoints are disabled in the production profile. They remain available in local development.

The live health endpoint returns the application name and an `UP` status when the service is running.

### Production verification

The deployed service was manually tested against the hosted database. Verification included:

- User registration and login.
- JWT bearer-token authentication and the configured 900-second token lifetime.
- Creating and retrieving a job application.
- Updating an application's status and retrieving its status history.
- Confirming that one user cannot retrieve another user's application or history.
- Confirming that application lists and analytics are scoped to the authenticated user.
- Checking status counts, funnel metrics, activity, and historical stage counts.

These checks were performed against the deployed API and Neon database.

### Free-tier notes

This deployment intentionally uses free-tier services and does not require a paid hosting plan.

- Render Free Web Services may spin down after a period without inbound traffic. The first request after inactivity can take longer while the service starts.
- Neon Free has compute, storage, and transfer limits. Monitor usage in the Neon dashboard.
- Free-tier limits and terms can change; check the providers' current documentation.
- This configuration is suitable for a portfolio demonstration, but it does not provide the uptime guarantees of a paid production service.

## Prerequisites

- Git
- Docker Desktop with Docker Compose
- Java 21 for local development (the container workflow builds and runs the API in Docker)

## Run locally with Docker Compose

1. Clone the repository:

   ```powershell
   git clone https://github.com/lail0236/careertrack-api.git
   cd careertrack-api
   ```

2. Create the local environment file:

   ```powershell
   Copy-Item .env.example .env
   ```

3. Replace placeholders in `.env` with local values. Use a strong database password and a JWT secret of at least 32 bytes. Never commit `.env`.

4. Build and start:

   ```powershell
   docker compose up --build -d
   ```

5. Check services and API health:

   ```powershell
   docker compose ps
   Invoke-RestMethod http://localhost:8080/api/health
   ```

6. Open Swagger UI: http://localhost:8080/swagger-ui/index.html

   OpenAPI JSON: http://localhost:8080/v3/api-docs

### Stop services

```powershell
docker compose down
```

This preserves the named PostgreSQL volume. `docker compose down -v` deletes local database data; use it only when you intentionally want a clean database.

## Configuration

The Compose setup reads local secrets from `.env`. Start with `.env.example` and check the actual Compose configuration if variable names change. Never use example credentials or development secrets in a public deployment. Keep `.env` out of version control.

The production profile (`application-prod.properties`) expects database and JWT settings to be supplied through environment variables. Configure these in the hosting provider's secret/environment-variable settings; do not commit production credentials.

## API overview

The running OpenAPI document exposes **14 operations across four controller groups**. Local base URL: `http://localhost:8080`.

| Method | Endpoint | Purpose | Authentication |
|---|---|---|---|
| `GET` | `/api/health` | Health check | Public |
| `POST` | `/api/auth/register` | Register a user | Public |
| `POST` | `/api/auth/login` | Log in and receive a JWT | Public |
| `GET` | `/api/applications` | List the current user's applications | Required |
| `POST` | `/api/applications` | Create an application | Required |
| `GET` | `/api/applications/{id}` | Get one application | Required |
| `PUT` | `/api/applications/{id}` | Update an application | Required |
| `DELETE` | `/api/applications/{id}` | Delete an application | Required |
| `PATCH` | `/api/applications/{id}/status` | Change application status | Required |
| `GET` | `/api/applications/{id}/history` | Read status history | Required |
| `GET` | `/api/analytics/status-counts` | Current status counts | Required |
| `GET` | `/api/analytics/historical-stage-counts` | Historical stage counts | Required |
| `GET` | `/api/analytics/funnel` | Application funnel metrics | Required |
| `GET` | `/api/analytics/activity` | Application activity | Required |

For exact request examples and field constraints, see [`docs/API_EXAMPLES.md`](docs/API_EXAMPLES.md). Swagger UI exposes the live schemas in local development.

## Authentication flow

1. Register through `POST /api/auth/register`.
2. Log in through `POST /api/auth/login`.
3. Copy the returned `accessToken`.
4. Send it on protected requests using `Authorization: Bearer YOUR_ACCESS_TOKEN`.

Access tokens are short-lived. Log in again when a token expires.

## Testing

```powershell
.\mvnw.cmd -B test
.\mvnw.cmd -B package
```

Docker smoke test:

```powershell
docker compose up --build -d
Invoke-RestMethod http://localhost:8080/api/health
```

Manual checks have verified that unauthenticated protected requests return HTTP 401, attempts to retrieve another user's application or history return HTTP 404, and analytics exclude another user's data.

Production deployment checks are summarized in [Production verification](#production-verification).

## Security notes

- Passwords are stored as BCrypt hashes, not plaintext.
- Protected endpoints require JWT authentication.
- Application queries and analytics are scoped to the authenticated user.
- Keep `.env` and real credentials out of Git.
- Rotate any credential that was ever exposed in a public commit, screenshot, or log.
- Production secrets belong in the hosting provider's environment-variable settings, not in source control.
- The production profile disables Swagger UI and OpenAPI endpoints.

## Swagger UI screenshots

### API overview

![Swagger API overview](docs/screenshots/swagger-overview.png)

### Authentication and analytics

![Swagger authentication and analytics](docs/screenshots/swagger-auth-analytics.png)

### Application endpoints and schemas

![Swagger application details](docs/screenshots/swagger-application-details.png)

Do not include tokens, passwords, personal data, or `.env` contents in screenshots.

## Project status

The core API, JWT authentication, per-user data isolation, analytics, database migrations, Docker workflow, automated tests, and API documentation have been implemented. The API is deployed and manually verified using Render Free Web Service and Neon Free PostgreSQL. The deployment is intended as a free portfolio demonstration and is subject to free-tier limits.

## License

No license is specified. Add one explicitly if you want others to reuse the code.