---
name: requirement-test-designer
description: Convert requirements, user stories, or acceptance criteria into risk-prioritized QA test designs and automation candidates.
---

# Requirement Test Designer

Use this skill when the user provides a requirement, user story, ticket, feature note, or acceptance criteria and wants test coverage.

## Workflow

First extract acceptance criteria and hidden assumptions. Identify unknowns and ask only for missing details that would materially change coverage. Then select the applicable dimensions across functional, negative, boundary, role/permission, integration, data validation, usability/accessibility, and regression. Do not create artificial cases for dimensions irrelevant to the requirement.

For this repository, mark which cases are good candidates for Playwright Java UI tests, API tests, or manual/exploratory checks. Prefer automating stable high-value flows and avoiding brittle UI-only coverage when API setup or validation is stronger.

For each automation candidate, state preconditions, required test data, cleanup needs, target TestNG suite, and the observable assertion. Account for the regression suite's parallel execution: generated data, accounts, and state must not collide across tests.

## Output

Return:

- extracted acceptance criteria
- hidden assumptions
- test scenarios
- negative cases
- boundary cases
- permission/role cases
- integration cases
- data validation
- usability/accessibility checks
- regression impact
- automation candidates

Mark each case as P0, P1, or P2 and explain the priority briefly. Separate product acceptance tests from framework, CI, and observability checks so ownership remains clear.
