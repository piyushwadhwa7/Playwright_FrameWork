---
name: api-test-architect
description: Design or improve API test coverage matrices and API automation for this Java Maven framework.
---

# API Test Architect

Use this skill when the user provides an endpoint, API spec, payload, bug, or API testing goal and wants coverage design or automated API tests.

## Project Fit

This repository includes Java 21, Maven, TestNG, Playwright Java API request support, Rest Assured, Jackson, Hamcrest, and an API suite at `src/test/resources/TestRunners/testng_api.xml`. Prefer the existing API test style unless there is a clear reason to introduce a different pattern.

Use `BaseApiTest` and config-driven secrets/properties where appropriate. Do not hardcode tokens, credentials, or sensitive values. Prefer system properties or config placeholders for local and CI runs.

## Coverage Matrix

Select the relevant cases for each endpoint; do not force every category when the API contract makes it inapplicable. Cover:

- happy path
- required fields
- optional fields
- data types
- boundaries
- authentication
- authorization
- idempotency
- concurrency
- duplicate requests
- malformed payloads
- rate limits
- pagination, filtering, and sorting
- error contracts
- security-relevant validation

For each case, provide input, expected status, expected response, DB/state verification, and priority.

## Framework and Scale Guidance

Use `testng_api.xml` to verify API work independently from Playwright UI failures. Use the regression suite only when the requested coverage genuinely spans UI and API behavior.

For state-changing tests, create unique data, retain the created identifier only for the current test flow, and clean up in an `always`/finally-equivalent path when the API allows it. Do not make tests depend on a record created by a previous CI build or on test execution order unless the suite explicitly models one CRUD workflow.

Assert the contract at the right level: status code, required response headers where contractual, response schema/type, field values, and observable state. Do not assert volatile infrastructure headers, response times, or undocumented fields as strict pass criteria unless the product has an agreed requirement for them.

For external APIs such as GoRest, separate framework failures from authentication, rate-limit, provider availability, and shared-data failures. Capture status code, request ID, and rate-limit information without exposing the bearer token.

## Output

Return a coverage matrix first, then implementation recommendations. Mark each scenario as automated API, automated UI/API, manual, or blocked by missing observability. When implementing, keep API tests isolated from UI tests and verify with the API TestNG suite when possible.
