# Contributing to LabRox API

Thank you for contributing to LabRox Enterprise LIMS. As a regulated clinical diagnostics software platform, strict engineering standards are required for all contributions.

## Branching Strategy (Git Flow-Lite)
- `main`: Production-ready, locked, tagged releases only.
- `develop`: Integration branch for tested features and bugfixes.
- `feature/<name>`: Feature branch cut from `develop`.
- `fix/<name>`: Bugfix branch cut from `develop` or hotfix from `main`.

Direct pushes to `main` and `develop` are prohibited. All changes must arrive via Pull Request.

## Commit Message Conventions
We enforce [Conventional Commits](https://www.conventionalcommits.org/):
`type(scope): concise description in imperative mood`

Examples:
- `feat(sample): add barcode duplicate accessioning check`
- `fix(order): resolve patient UHID race condition in registration`
- `perf(diagnostic): optimize delta check search query indexes`
- `docs(api): update OpenAPI specifications for approval service`

## Signed Commits
All commits must be cryptographically signed using SSH or GPG keys:
```bash
git config commit.gpgsign true
git config user.signingkey ~/.ssh/id_ed25519.pub
```

## Pull Request Checklist
Before opening a PR into `develop`:
1. [ ] `./mvnw clean test` passes on all modules.
2. [ ] Zero secrets or credentials in commit history (`gitleaks` clean).
3. [ ] All commits are signed.
4. [ ] 21 CFR Part 11 audit trails are preserved for any state-changing endpoints.
5. [ ] Relevant tests and documentation are added or updated.
