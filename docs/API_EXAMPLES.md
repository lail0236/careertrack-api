# API Examples

These examples use `http://localhost:8080`. Run the application first. Never commit real tokens or credentials. Field names and constraints below are taken from the running OpenAPI document.

## 1. Check API health

```bash
curl http://localhost:8080/api/health
```

## 2. Register a user

Required: `name`, `email`, `password`.
- `name`: string, maximum 100 characters.
- `email`: valid email, maximum 255 characters.
- `password`: 8–72 characters.

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Demo User",
    "email": "demo@example.com",
    "password": "ReplaceWithALocalTestPassword!"
  }'
```

Use a unique email for each test registration.

## 3. Log in

Required: `email`, `password`.

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "demo@example.com",
    "password": "ReplaceWithALocalTestPassword!"
  }'
```

Copy `accessToken` from the response. Do not commit or publish a real token.

### PowerShell token variable

```powershell
$baseUrl = "http://localhost:8080"
$loginBody = @{
    email = "demo@example.com"
    password = "ReplaceWithALocalTestPassword!"
} | ConvertTo-Json

$token = (Invoke-RestMethod "$baseUrl/api/auth/login" `
    -Method Post -ContentType "application/json" -Body $loginBody).accessToken

$headers = @{ Authorization = "Bearer $token" }
```

## 4. Create an application

Required: `company`, `position` (each maximum 150 characters).
Optional: `location` (100), `jobUrl` (2048), `dateApplied` (date, `YYYY-MM-DD`), and `notes` (2000).

```bash
curl -X POST http://localhost:8080/api/applications \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "company": "Example Company",
    "position": "Junior Software Engineer",
    "location": "Dallas, TX",
    "jobUrl": "https://example.com/jobs/123",
    "dateApplied": "2026-11-01",
    "notes": "Applied through the company careers page."
  }'
```

## 5. List applications

```bash
curl http://localhost:8080/api/applications \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## 6. Get one application

```bash
curl http://localhost:8080/api/applications/APPLICATION_ID \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## 7. Update an application

`PUT /api/applications/{id}` uses the same `CreateJobApplicationRequest` schema as creation. Include `company` and `position`.

```bash
curl -X PUT http://localhost:8080/api/applications/APPLICATION_ID \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "company": "Example Company",
    "position": "Backend Engineer",
    "location": "Austin, TX",
    "jobUrl": "https://example.com/jobs/456",
    "dateApplied": "2026-11-01",
    "notes": "Updated application details."
  }'
```

## 8. Delete an application

```bash
curl -X DELETE http://localhost:8080/api/applications/APPLICATION_ID \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## 9. Change application status

Required: `status` (string, maximum 30 characters). Supported statuses: `SAVED`, `APPLIED`, `ASSESSMENT`, `INTERVIEW`, `OFFER`, `REJECTED`.

```bash
curl -X PATCH http://localhost:8080/api/applications/APPLICATION_ID/status \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "APPLIED"
  }'
```

## 10. Read application history

```bash
curl http://localhost:8080/api/applications/APPLICATION_ID/history \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## 11. Read analytics

```bash
curl http://localhost:8080/api/analytics/status-counts -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
curl http://localhost:8080/api/analytics/historical-stage-counts -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
curl http://localhost:8080/api/analytics/funnel -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
curl http://localhost:8080/api/analytics/activity -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## 12. Verify access isolation

Use two separate registered test users:
- User A creates an application and can retrieve it.
- User B attempts to retrieve User A's application ID and should receive HTTP `404`.
- An unauthenticated request to a protected endpoint should receive HTTP `401`.
- Each user's analytics should reflect only that user's data.

These are security expectations, not a substitute for running tests against your current build.
