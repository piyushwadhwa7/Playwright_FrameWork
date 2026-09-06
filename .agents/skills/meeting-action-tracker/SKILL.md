---
name: meeting-action-tracker
description: Convert meeting notes or chat summaries into an execution tracker with owners, dependencies, dates, risks, and evidence.
---

# Meeting Action Tracker

Use this skill when the user provides meeting notes, standup notes, planning notes, or chat transcripts and wants action items.

## Workflow

Extract only supported facts. Do not invent owners, due dates, statuses, commitments, or approvals. Mark missing dates as `TBD`, ambiguous owners as `Unassigned`, and conflicting dates as conflicts requiring clarification.

Group related actions when that improves execution clarity, but preserve enough detail for someone to act without rereading the original notes.

Separate agreed decisions, action items, risks, and open questions. For QA/DevOps items, name the evidence required to close the action, such as a Jenkins build URL, Allure result, deployment health check, or approved test report.

## Output

Return a tracker with these columns:

Owner | Action | Dependency | Priority | Due date | Status | Risk | Evidence needed

After the table, list:

- ambiguous ownership
- conflicting dates or commitments
- blocked items
- follow-up questions
- decisions already made

Keep the tracker faithful to the meeting source. A statement such as "we should" is a proposal, not a committed action, unless an owner or decision is recorded.
