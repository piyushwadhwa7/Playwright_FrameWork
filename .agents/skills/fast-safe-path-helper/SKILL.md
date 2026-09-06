---
name: fast-safe-path-helper
description: Give the fastest safe path when the user is stuck on a QA, automation, coding, DevOps, or documentation task.
---

# Fast Safe Path Helper

Use this skill when the user says they are stuck, asks for the quickest path, or needs a short tactical next step.

## Workflow

Ask only the minimum critical questions if the next action would otherwise be risky or impossible. Otherwise, diagnose from the available context and move directly to the safest short path.

For repository work, prefer the smallest reversible action that gathers evidence or fixes the highest-confidence issue. Avoid broad refactors, dependency churn, or speculative changes. Do not deploy, delete remote data, rotate credentials, or change production settings without explicit authorization.

For this framework, first identify whether the blocker is Maven/Java, TestNG suite selection, Playwright browser setup, credentials/configuration, GoRest, Allure, or Qodana. A failing Jenkins regression stage may be masked by `catchError`, so inspect the stage result and Surefire evidence rather than the overall build color alone.

## Output

Return:

- Diagnosis
- Next 3 actions
- Exact output, template, or one command that proves the next hypothesis
- Verification
- Common mistake to avoid

Keep the answer compact and action-oriented. State a stopping condition: what result means the user should proceed, retry once, or gather the next missing artifact.
