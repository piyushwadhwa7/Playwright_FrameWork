---
name: playwright-failure-analyzer
description: Analyze failed Playwright Java/TestNG runs using logs, Allure results, screenshots, traces, test code, and framework context, then suggest or apply fixes.
---

# Playwright Failure Analyzer

Use this skill after a failed UI automation run or when the user asks what happened in Playwright tests.

## Evidence To Inspect

Look for the latest relevant evidence before diagnosing:

- Maven/TestNG console output or stack trace
- `allure-results` JSON and attachments
- `allure-report` or `allure-history` trend data when useful
- failed test classes under `src/test/java`
- page objects and utilities under `src/main/java`
- `src/test/resources/config` and TestNG suite XML files
- `src/test/java/com/qa/opencart/base` for setup, browser lifecycle, and configuration loading
- GitHub Actions workflow or CI logs if the failure happened in CI
- Playwright screenshots, videos, traces, or `playwright-mcp-output` artifacts when present

Do not assume the latest file is relevant without checking timestamps or matching test names.

In this repository, compare the exact failing CI command with the suite definition. `testng_regression.xml` can run UI and API tests, while `testng_api.xml` isolates the API suite. Jenkins uses headless mode and may not have Playwright browser binaries or Linux dependencies installed; GitHub Actions installs browsers explicitly. Treat those as environment evidence, not locator failures.

## Classification

Classify the likely cause as one or more of:

- locator
- synchronization
- test data
- environment/configuration
- application defect
- network/API
- browser/runtime
- flaky test
- test design issue

For each classification, cite concrete evidence from logs, code, or artifacts.

When the suite is parallel, check whether the test shares browser state, account data, files, static variables, or generated records with another test before labelling it flaky. Prefer a trace/screenshot/locator state over retries as evidence for a UI diagnosis.

## Output

Return:

- Failure summary
- Likely category and confidence
- Evidence
- Fastest diagnostic steps
- Corrected code or exact patch plan
- Why the original failed
- Whether the test should change or the product should be fixed
- Prevention notes
- Verification command

For CI failures, also state whether the failure is visible in the overall build result or masked by a stage-level error handler. Do not expose credentials in reproduced commands or attachments.

If the user asks for fixes, make the smallest safe project edit, preserve existing framework conventions, and run the narrowest relevant test where practical.
