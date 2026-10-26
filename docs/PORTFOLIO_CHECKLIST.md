# Portfolio Readiness Checklist

## Documentation
- [ ] Review `README.md` against actual configuration and endpoint schemas.
- [ ] Follow setup instructions from a clean checkout.
- [ ] Confirm `.env.example` contains placeholders only.
- [ ] Confirm `.env` is ignored and not tracked.
- [ ] Check that the README contains no unverified deployment claims.

## API evidence
- [ ] Open Swagger UI locally.
- [ ] Capture the endpoint overview.
- [ ] Capture authentication, applications, and analytics endpoint groups.
- [ ] Remove tokens, passwords, email addresses, and personal data from screenshots.
- [ ] Save images under `docs/screenshots/`.

## Verification
- [ ] Run `.\mvnw.cmd -B test`.
- [ ] Run `git status`.
- [ ] Inspect changes with `git diff -- README.md docs/API_EXAMPLES.md docs/PORTFOLIO_CHECKLIST.md`.
- [ ] Commit only after reviewing the diff.
- [ ] Push and verify GitHub Actions is green.

## Before public deployment
- [ ] Use a strong production-only JWT signing secret.
- [ ] Restrict database network access.
- [ ] Use HTTPS.
- [ ] Configure backups, logs, and monitoring.
- [ ] Review credential history; rotate any secret that was exposed.
- [ ] Choose a license explicitly if reuse is intended.
