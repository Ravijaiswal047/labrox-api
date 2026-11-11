# LabRox API Git Workflow & Release Governance

## 1. Branch Strategy (Git Flow-Lite)

- **`main`**: Production-ready code only. Direct push is blocked. Protected branch requiring ≥ 2 reviews, signed commits, and all CI checks green.
- **`develop`**: Active integration branch. Protected branch requiring ≥ 1 review and green CI checks.
- **`feature/<ticket-description>`**: Cut from `develop`, merged back to `develop` via PR.
- **`fix/<ticket-description>`**: Cut from `develop`, merged back to `develop` via PR.
- **`hotfix/<ticket-description>`**: Emergency fixes cut from `main`, merged into both `main` and `develop`.

## 2. Commit Signing Policy (21 CFR Part 11)

To ensure non-repudiation and traceability required under 21 CFR Part 11:
- Every commit must be signed with an approved SSH or GPG key.
- Unsigned commits are rejected by CI and branch protection rules.

```bash
git config commit.gpgsign true
git config gpg.format ssh
git config user.signingkey ~/.ssh/id_ed25519.pub
```

## 3. Conventional Commits Standard

Every commit must follow Conventional Commits formatting:
`<type>(<scope>): <subject>`

Allowed types:
- `feat`: Microservice feature / endpoint addition
- `fix`: Bugfix or error handling correction
- `perf`: Database query index, caching, or latency improvement
- `test`: Unit, integration, or contract tests
- `docs`: Architecture, ADRs, or API specification
- `ci`: GitHub Actions workflow changes
- `chore`: Tooling, dependency, or configuration updates

## 4. Release Lifecycle & Promotion

1. When features on `develop` pass all verification checks, a release PR is opened from `develop` -> `main`.
2. Upon approval and merge, a signed semantic version tag is created:
   ```bash
   git tag -s -a v0.1.0 -m "Release v0.1.0: First production-ready LIMS baseline"
   git push origin v0.1.0
   ```
3. GitHub Actions triggers `release.yml`, builds distribution JARs, calculates SHA-256 checksums, and publishes the official release notes.
