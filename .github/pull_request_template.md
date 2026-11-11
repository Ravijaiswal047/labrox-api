## Description
<!-- Provide a clear summary of the change, motivation, and context. -->

## Type of Change
- [ ] `feat`: New feature / endpoint
- [ ] `fix`: Bug fix
- [ ] `perf`: Performance enhancement / query optimization
- [ ] `refactor`: Code refactoring without behavior change
- [ ] `test`: Unit / Integration test additions
- [ ] `docs`: Documentation updates
- [ ] `chore`: Build or dependency maintenance

## 21 CFR Part 11 & GxP Compliance Checklist
- [ ] Audit trail logging preserved for any state-changing actions
- [ ] Electronic signature requirements satisfied where applicable
- [ ] Zero database secrets or plain credentials added
- [ ] All commits cryptographically signed

## Verification & Testing
- [ ] `./mvnw clean verify` passed with 0 test failures
- [ ] CodeQL / SAST security scan clean
- [ ] Manual smoke tests verified against local gateway (port 8088)

## Parity & Clinical Integrity
- [ ] Request / response contracts maintain 100% parity
- [ ] Data validation handles edge cases and boundary conditions
