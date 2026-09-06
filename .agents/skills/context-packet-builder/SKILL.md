---
name: context-packet-builder
description: Build concise context packets for complex QA, automation, debugging, DevOps, or documentation tasks.
---

# Context Packet Builder

Use this skill when the task is complex and the user needs a clean context packet for Codex, another agent, a teammate, a ticket, or a debugging handoff.

## Workflow

Extract verified facts separately from assumptions and unknowns. Keep the packet concise enough to be pasted into another tool, but complete enough that the next person can act without asking for obvious missing details.

For repository tasks, include relevant file paths, commands, logs, test names, environment, commit/branch when known, and validation criteria. Quote only the smallest useful log excerpt and never include secrets, private credentials, cookies, or full environment dumps.

For CI or automation work, distinguish local, Jenkins, GitHub Actions, QA, and production evidence. State whether the supplied command actually ran, what it changed, and where its output can be found.

## Output

Use this structure:

ROLE: who should act
OBJECTIVE: one measurable goal
CONTEXT: system/project/background
INPUTS: logs/files/code/data
CONSTRAINTS: time/tools/security/compatibility
KNOWN FACTS: verified facts only
ASSUMPTIONS: decisions made because evidence is incomplete
UNKNOWN: what is uncertain
OUTPUT FORMAT: table/code/checklist/email/etc.
QUALITY BAR: what good means
VALIDATION: how the result will be checked

If the source material is messy, add a short "Needs clarification" section at the end. Do not turn unknown facts into questions when a safe, clearly labelled assumption lets the recipient proceed.
