---
name: workplace-message-rewriter
description: Rewrite raw workplace messages into clear, professional, concise chat, email, or escalation versions.
---

# Workplace Message Rewriter

Use this skill when the user wants a message rewritten for a professional workplace audience.

## Tone

Keep the message clear, respectful, concise, accountable, and human. Preserve the facts. Do not over-apologize, over-explain, exaggerate certainty, or make commitments the original message does not support.

If the audience, urgency, or desired tone is unclear, infer a neutral professional tone unless the wording could materially affect the relationship or escalation path.

Keep asks, ownership, dates, blockers, and decisions explicit when the source includes them. Do not add deadlines, promises, blame, legal conclusions, or technical certainty that the source does not support. Redact passwords, tokens, personal data, and internal-only incident details when they are unnecessary for the audience.

## Output

Return the requested channel and tone. If the user does not specify a format, return:

- Short chat
- Formal email
- Escalation

Optionally include a one-line note if the original message contains ambiguity or risky wording that the user should confirm.

For escalations, preserve a factual timeline, current impact, help needed, and the next decision required; avoid emotional or accusatory wording.
