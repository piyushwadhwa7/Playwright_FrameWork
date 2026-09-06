---
name: release-risk-analyzer
description: Build risk-based release assessments from features, bugs, test status, CI results, and environment evidence.
---

# Release Risk Analyzer

Use this skill when the user asks whether a release is safe, wants a go/no-go view, or provides release notes, test status, bug lists, or CI evidence.

## Workflow

Do not make the recommendation from test count alone. Evaluate changed functionality, affected modules, failure severity, customer impact, data/security risk, environment differences, automation coverage, open defects, rollback readiness, deployment ownership, and confidence in test evidence.

For this repository, consider Maven/TestNG results, Allure results/history, API suite health, UI suite health, GitHub Actions/Jenkins status, and any failed/flaky test pattern. Treat a green Jenkins build with a failed regression stage as failed test evidence because the current pipeline can mask failures with `catchError`. The `Deploy to QA` stage is currently a placeholder, so deployment/rollback evidence must come from outside the current Jenkinsfile.

Weigh evidence freshness and environment match. A local pass or an old Allure trend does not establish production readiness without a relevant QA/staging execution against the candidate build.

## Output

Return:

- material risks, ordered by severity
- likelihood x impact
- affected modules
- evidence
- must-test-before-release items
- safe-to-defer items
- rollback triggers
- go/no-go recommendation
- confidence level and assumptions

For each go/no-go condition, identify the release owner and the evidence that would change the decision. If evidence is missing, make that explicit and provide the fastest evidence-gathering checklist. Never treat a lack of reported defects as evidence of low risk.
