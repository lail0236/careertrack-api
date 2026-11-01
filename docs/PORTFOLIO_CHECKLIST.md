# Portfolio Readiness Checklist

## Documentation
- [ ] Confirm README endpoint list matches `/v3/api-docs`.
- [ ] Confirm examples match the live OpenAPI schemas.
- [ ] Follow setup instructions from a clean checkout.
- [ ] Confirm `.env.example` contains placeholders only.
- [ ] Confirm `.env` is ignored and not tracked.

## Swagger evidence
- [x] Capture `swagger-overview.png` showing API title and application endpoints.
- [x] Capture `swagger-auth-analytics.png` showing authentication, health, and analytics.
- [x] Capture `swagger-application-details.png` showing expanded application operations and schemas.
- [x] Remove tokens, passwords, email addresses, and personal data from screenshots.
- [x] Save images under `docs/screenshots/`.
- [x] Add screenshots to the README.

## Verification
- [ ] Run `.\mvnw.cmd -B test`.
- [ ] Run `git diff --check`.
- [ ] Review all staged documentation and screenshot files.
- [ ] Commit only after reviewing the diff.
- [ ] Push and verify GitHub Actions is green.

## Before public deployment
- [ ] Use a strong production-only JWT signing secret.
- [ ] Restrict database network access.
- [ ] Use HTTPS.
- [ ] Configure backups, logs, and monitoring.
- [ ] Review credential history; rotate any secret that was exposed.
- [ ] Choose a license explicitly if reuse is intended.
