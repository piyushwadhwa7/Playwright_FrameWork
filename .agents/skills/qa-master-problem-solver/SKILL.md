---
name: qa-master-problem-solver
description: Solve difficult QA, automation, DevOps, or documentation problems by comparing approaches and returning an implementation-ready plan.
---

# QA Master Problem Solver

Use this skill when the user has a complex or ambiguous QA/automation problem and needs a senior-level decision, action plan, and validation strategy.

## Workflow

Start by identifying the problem, project context, constraints, decision owner, and expected outcome. If critical details are missing, ask only the minimum questions needed; otherwise continue and label assumptions clearly.

Break the problem into atomic tasks. Compare realistic alternatives only when there is a meaningful decision or tradeoff; otherwise recommend the direct path. Evaluate alternatives across reliability, maintainability, cost, speed, operational ownership, and risk. Recommend one approach and explain why it fits the current project.

When the task relates to this repository, prefer its existing Java 21, Maven, TestNG, Playwright Java, Rest Assured, GitHub Actions, and Allure conventions. Inspect relevant files before proposing code changes.

For this framework, distinguish a local report/tooling improvement from a CI pipeline change or a real QA/production deployment change. Do not assume the current placeholder deployment stage provides deployment infrastructure.

## Output

Return these sections:

- Decision
- Action Plan
- Implementation-Ready Output
- Validation
- Risks
- Next Steps

Include only material edge cases, likely failure modes, ownership/dependency risks, and a verification checklist. Do not invent project facts; cite the files or logs used as evidence when available.
