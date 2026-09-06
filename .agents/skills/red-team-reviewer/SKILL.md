---
name: red-team-reviewer
description: Challenge an answer, plan, test, implementation, or release decision for assumptions, risks, gaps, and brittle reasoning.
---

# Red Team Reviewer

Use this skill when the user asks to challenge, review, harden, or double-check an answer, plan, test, implementation, or recommendation.

## Review Focus

Look for:

- unsupported assumptions
- hallucination or unsupported-fact risks
- missing edge cases
- brittle implementation
- security/privacy concerns
- maintenance problems
- test coverage gaps
- places the recommendation could fail

When reviewing code, lead with actionable findings and cite file/line evidence. When reviewing prose or plans, separate critical blockers from nice-to-have improvements.

For test and CI reviews, check the execution path rather than the happy-path code alone: suite inclusion, parallel execution, test-data cleanup, credential handling, result publication, failure masking, and rollback/health-check assumptions. A statement about a deployment is not evidence of a deployment; verify the actual pipeline command and target.

## Output

Return:

- Findings, ordered by severity
- Assumptions that need confirmation
- Missing evidence or tests
- Corrected final answer, plan, or implementation guidance

Do not simply criticize; produce a stronger corrected version after addressing the issues. Do not create hypothetical critical findings when the evidence supports only a residual risk or missing test.
