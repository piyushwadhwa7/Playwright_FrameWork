---
name: qa-bug-investigator
description: Investigate defects from bug reports, logs, screenshots, steps, or test evidence and produce developer-ready QA analysis.
---

# QA Bug Investigator

Use this skill when the user provides a bug, failing behavior, screenshot, log, reproduction steps, or vague defect report and wants root-cause analysis or a developer-ready bug comment.

## Workflow

Inspect the provided evidence first. For this repository, also check relevant TestNG tests, page objects, API tests, config, Allure results, and recent output artifacts when they are related to the defect.

Rank probable root causes by likelihood. For each hypothesis, explain the evidence that supports it, the evidence still needed, and the fastest way to confirm or reject it. Do not jump to a single root cause before comparing alternatives.

Cover UI, API, database/state, integration, environment, configuration, and test-data possibilities where relevant. Distinguish product defects from automation defects.

Record the affected environment, build/version, account or test-data state, browser/device when relevant, timestamp/time zone, and correlation/request ID when available. Do not report secrets or personally identifying data. If a failure occurs only in CI, compare the exact CI command and runtime prerequisites before assigning blame to the product.

## Output

Return:

- Summary of observed defect
- Ranked root-cause hypotheses
- Evidence needed for each hypothesis
- Minimal reproduction
- Boundary and negative cases
- API, DB/state, UI, and integration checks
- Exact retest checklist
- Regression impact
- Developer-ready bug comment

The developer-ready comment must include observed result, expected result, reproducible steps, scope/impact, evidence links or artifact paths, and a clear statement of what remains unverified. Do not claim root cause as fact unless evidence proves it.

If asked to fix the issue, propose the smallest safe code change first and verify with the narrowest relevant test command.
