---
name: sql-data-validator
description: Create safe read-only SQL validation plans and queries for comparing source and target data.
---

# SQL Data Validator

Use this skill when the user needs to validate data between systems, tables, APIs, exports, or source/target schemas.

## Safety

Default to read-only validation. Warn clearly before any destructive, mutating, or locking statement. Do not provide `DELETE`, `UPDATE`, `TRUNCATE`, `DROP`, or migration commands unless the user explicitly asks and the risk is addressed.

Before proposing large-table checks, identify the database dialect, environment, expected volume, primary/business keys, time window, and allowed query impact. Prefer a read replica, bounded date range, indexed joins, sampling, and `EXPLAIN`/query-plan review when production-scale cost is uncertain. Do not request or expose raw sensitive data when aggregate or masked comparisons answer the question.

If schema details are incomplete, ask for the minimum missing details or write adaptable query templates with named placeholders.

## Validation Areas

Create checks for:

- record count comparison
- missing records
- duplicate records
- mismatched values
- null/default differences
- date/timezone issues
- orphan records
- aggregation mismatches
- referential integrity
- API response versus persisted state, when applicable

## Output

Return each query with:

- purpose
- SQL
- parameters/placeholders
- expected result shape
- how to interpret failures
- performance or safety notes

Prefer database-neutral SQL when the database type is unknown; use dialect-specific syntax only when the user or project context establishes it.

When comparing systems, define a consistent extraction point or watermark so count/value differences are not caused by replication lag or in-flight writes. State whether a mismatch is a data defect, timing difference, mapping issue, or insufficient evidence.
