# API Examples

These examples use `http://localhost:8080`. Run the application first. Do not commit real tokens or credentials.

## 1. Check API health

```bash
curl http://localhost:8080/api/health
```

## 2. Register a user

The registration request uses `name`, `email`, and `password`.

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Demo User",
    "email": "demo@example.com",
    "password": "ReplaceWithALocalTestPassword!"
  }'
```

Use a unique email for each test registration. Do not reuse production credentials.

## 3. Log in

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "demo@example.com",
    "password": "ReplaceWithALocalTestPassword!"
  }'
```

Copy the `accessToken` from the response into a local shell variable. Do not paste a real token into public documentation or commit it.

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

The verified minimum request fields are `company` and `position`.

```bash
curl -X POST http://localhost:8080/api/applications \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "company": "Example Company",
    "position": "Junior Software Engineer"
  }'
```

## 5. List your applications

```bash
curl http://localhost:8080/api/applications \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## 6. Get one application

Replace `APPLICATION_ID` with an ID returned by the API.

```bash
curl http://localhost:8080/api/applications/APPLICATION_ID \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## 7. Change application status

Supported statuses are `SAVED`, `APPLIED`, `ASSESSMENT`, `INTERVIEW`, `OFFER`, and `REJECTED`. Check the request schema in Swagger UI for the exact JSON property expected by the current build.

Endpoint: `PATCH /api/applications/{id}/status`

## 8. Read application history

```bash
curl http://localhost:8080/api/applications/APPLICATION_ID/history \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## 9. Read analytics

```bash
curl http://localhost:8080/api/analytics/status-counts \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"

curl http://localhost:8080/api/analytics/funnel \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"

curl http://localhost:8080/api/analytics/activity \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"

curl http://localhost:8080/api/analytics/historical-stage-counts \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## 10. Verify access isolation

Use two separate registered test users:

- User A creates an application and can retrieve it.
- User B attempts to retrieve User A's application ID and should receive HTTP `404`.
- An unauthenticated request to a protected endpoint should receive HTTP `401`.
- Each user's analytics should reflect only that user's data.

These are security expectations, not a substitute for running the tests against your current build.
