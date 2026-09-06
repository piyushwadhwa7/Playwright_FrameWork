---
name: ci-failure-diagnoser
description: Diagnose Maven, TestNG, Playwright, Allure, GitHub Actions, and environment failures in CI/CD pipelines.
---

# CI Failure Diagnoser

Use this skill when the user provides a Jenkins or GitHub Actions failure, pipeline log, Maven/TestNG output, Playwright browser failure, Allure issue, Qodana failure, or CI/CD troubleshooting request.

Do not use this skill for a general pipeline explanation when no failure needs diagnosing.

## Goal

Identify the earliest failing command or stage, prove the direct cause with the smallest safe check, and distinguish it from failures that happen afterward. Give a minimal repair and a prevention that remains useful as the project grows.

Never ask for or print passwords, bearer tokens, SSH keys, cookies, or full environment dumps. Refer to secret names and credential IDs only.

## Repository Context

Inspect only the files that apply to the reported failure:

- `Jenkinsfile` for Jenkins stages, credentials, Qodana, and report publication.
- `.github/workflows/main.yml` for GitHub Actions setup.
- `pom.xml` for Java target, Surefire, and dependency versions.
- `src/test/resources/TestRunners/testng_regression.xml` and `testng_api.xml` for suite scope.
- `src/test/resources/config/config.properties.template` for expected configuration keys.
- `scripts/allure-report.sh` and `scripts/api-allure-report.sh` only for local Allure-report script failures.
- Relevant test classes, `target/surefire-reports`, `allure-results`, screenshots, traces, and Qodana output when available.

Current project facts to use when diagnosing:

- Maven compiles for Java 21. A JDK below 21 is incompatible. GitHub Actions currently uses Temurin 25; Jenkins selects Maven but does not explicitly select a JDK, so inspect its Java version separately.
- The Jenkins regression command uses `testng_regression.xml`, which runs both Playwright UI tests and Users API tests. The API-only suite is `testng_api.xml`.
- Jenkins passes `opencart-login` as `OC_USERNAME` and `OC_PASSWORD`, and `gorest-token` as `GOREST_TOKEN`. Tests receive the token through `-Dgorest.bearer.token` and read `gorest_bearer_token` configuration.
- Jenkins runs browser tests with `-Dheadless=true`, but unlike GitHub Actions it does not run Playwright browser installation in the pipeline. Missing browser binaries or Linux libraries are therefore a Jenkins-specific hypothesis.
- Jenkins wraps regression tests in `catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE')`. A failed regression stage can leave the overall build green; do not call the build healthy until the stage result and Surefire results have been checked.
- The Jenkins `Deploy to QA` stage is currently an `echo` placeholder, not a real deployment. Do not diagnose deployment infrastructure from that stage.
- Jenkins publishes raw `allure-results` through the Allure Jenkins plugin. The local `scripts/*allure-report.sh` scripts are not invoked by Jenkins and additionally require the Allure CLI and Node.js.
- Qodana runs in a temporary Docker container on the Jenkins agent. Docker daemon access, image-pull access, workspace mounts, disk space, and `qodana-results` write permissions are relevant only for the Qodana stage.
- GoRest is an external API. Authentication errors, rate limits, response changes, and test-data collisions are separate from framework failures.

## Workflow

### 1. Classify the Execution Environment

Identify whether the failure is local, Jenkins, GitHub Actions, or a remote execution environment. Record the failed stage/job, operating system, Java version, Maven version, browser, suite file, and whether UI, API, or Qodana was running.

Do not assume a local pass proves CI will pass. Compare the CI command and prerequisites with the local command.

### 2. Find the First Real Failure

Read the log from the beginning of the failing stage. Locate the first non-zero command, exception, test failure, or missing prerequisite. Treat stack traces, failed report generation, and later cancelled stages as downstream symptoms unless they are earlier than the original failure.

For Jenkins regression failures, check both:

- the `Regression Automation Test` stage result, because `catchError` can leave the overall build green;
- `target/surefire-reports` and `allure-results` to identify the failed TestNG method.

For GitHub Actions, distinguish setup failure, browser-install failure, test failure, and artifact/reporting failure.

### 3. Match the Failure to Its Stage

Use this triage order:

| Stage or signal | Check first | Common direct causes |
| --- | --- | --- |
| Checkout | SCM error before Maven starts | repository access, branch/ref, webhook, agent network |
| Maven build | compiler/dependency error | JDK below 21, wrong Maven, dependency repository/network, source error |
| TestNG suite loading | Surefire/TestNG error before tests | wrong suite path, malformed XML, missing class/listener |
| Playwright UI test | browser launch/page error | browser binaries missing, Linux libraries missing, headless-only behavior, credentials, environment URL |
| API test | HTTP response or token error | missing/invalid GoRest token, rate limit, external API change, shared test data |
| Allure | test results exist but report fails | missing/empty `allure-results`, listener issue, Jenkins Allure plugin/configuration |
| Local Allure script | script prerequisite error | missing Allure CLI, Node.js, malformed history/report output |
| Qodana | Qodana stage/container error | Docker unavailable, permission denied, image pull, mount/output permission, disk space |

### 4. Verify the Smallest Relevant Hypothesis

Use commands only when they prove one specific possibility. Prefer read-only commands first and describe the evidence expected from each command.

Examples:

```bash
java -version
mvn -version
```

These verify the JDK and Maven actually used by the failing agent. Use them for compiler, Java compatibility, or toolchain-drift hypotheses.

```bash
find target/surefire-reports -maxdepth 1 -type f -print
```

This confirms whether Surefire produced test evidence and helps separate a framework startup failure from an individual test failure.

```bash
find allure-results -maxdepth 1 -type f -print
```

This checks whether Allure has input files before investigating report publication.

```bash
docker version
```

Use this only for Qodana/Docker-stage failures. It verifies that the Jenkins agent can reach a Docker daemon.

```bash
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/TestRunners/testng_api.xml \
  -Dgorest.bearer.token='REDACTED'
```

Use an actual secret only through the CI credential mechanism, never by placing it in chat or source control. This isolates API tests from Playwright UI failures.

### 5. Assess Reproducibility

State one of these outcomes:

- **Reproducible:** the same command and environment produce the same failure.
- **CI-only:** a missing CI prerequisite or configuration differs from local.
- **Intermittent:** timing, rate limit, external dependency, shared data, or agent capacity is involved.
- **Insufficient evidence:** no first failure or required artifact/log is available.

For intermittent API failures, capture status code, request ID if available, rate-limit headers, and timestamp. For UI failures, capture the test name, browser, screenshot/trace location, and page URL without exposing credentials.

### 6. Recommend the Fix at the Right Layer

Give both:

- **Minimal fix:** the smallest change that resolves the observed failure.
- **Permanent prevention:** a pipeline, test, environment, or reporting improvement that prevents recurrence.

Examples of permanent prevention:

- Pin and print the CI JDK and Maven versions.
- Add Playwright browser installation to Jenkins agents or use a prebuilt agent image.
- Separate fast API checks from slower UI regression suites when feedback time becomes a problem.
- Publish Surefire XML, Allure results, screenshots, traces, and Qodana results in `post { always { ... } }` blocks.
- Preserve Allure history in persistent Jenkins storage when trend reporting is required.
- Use unique API test data and clean it up after tests.
- Add a real health-check step only after a real QA deployment exists.

## Output Format

Return findings in this order:

1. **First failing step:** exact stage, command/test, and a short log reference.
2. **Direct cause:** the evidence-supported cause.
3. **Downstream symptoms:** errors caused by the direct failure, including masked Jenkins outcomes where relevant.
4. **Reproducibility:** one of the four classifications above and why.
5. **Minimal fix:** precise code, pipeline, or environment change.
6. **Permanent prevention:** practical improvement for future CI runs.
7. **Targeted checks:** only the commands/artifacts needed to verify the diagnosis.

If the evidence is incomplete, say exactly what log segment, report, screenshot, trace, or configuration value is needed. Do not guess and do not propose broad refactors before the first failure is known.
