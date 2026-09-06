---
name: playwright-test-generator
description: Convert manual scenarios, bugs, or requirements into production-grade Playwright Java tests for this Maven/TestNG framework.
---

# Playwright Test Generator

Use this skill when the user wants a manual scenario, bug, or requirement converted into an automated Playwright test.

## Project Fit

This repository uses Java 21, Maven, TestNG, Playwright Java, page objects under `src/main/java/com/qa/opencart/pages`, base test setup under `src/test/java/com/qa/opencart/base`, TestNG suite XML files under `src/test/resources/TestRunners`, and Allure annotations/listeners.

Prefer Playwright Java and TestNG, not TypeScript, unless the user explicitly asks otherwise. Follow the existing page-object style and assertion style. Put reusable UI behavior in page classes or utilities instead of duplicating locator logic across tests.

## Workflow

Before writing code, inspect nearby tests, page objects, constants, test data, and config. Convert the scenario into a test design with clear preconditions, actions, assertions, and cleanup. Prefer resilient locators and behavior-level assertions.

Avoid arbitrary sleeps. Use Playwright auto-waiting, locator assertions, wait-for-response/request checks, or state-based waits. Use API setup only when it reduces UI brittleness and fits the framework.

Make generated tests independent under the TestNG suite's parallel execution. Avoid shared accounts, static mutable state, fixed filenames, and assumptions about priority/order. Use unique data and targeted cleanup for any state the test creates. Add the test to an existing suite only when the user requests that scope; otherwise provide the required suite change separately.

For CI suitability, identify the required browser, headless behavior, credentials/config keys, and evidence to retain on failure. Do not add arbitrary retries that hide product defects or weak synchronization.

## Output

Return:

- Test design
- Locator strategy
- Reusable page/component methods
- Fixtures or test data
- API setup, if useful
- Assertions that validate behavior, not implementation
- Wait/retry strategy without arbitrary sleeps
- Screenshots, trace, logging, or Allure reporting strategy
- Negative and boundary tests
- CI considerations
- Strict automation-review weaknesses

When implementation is requested, include only assertions that prove the user-visible requirement. Record any requirement that cannot be safely automated because the environment, test data, or observability is missing.

When asked to implement, edit the smallest set of files and run the narrowest relevant Maven/TestNG command.
