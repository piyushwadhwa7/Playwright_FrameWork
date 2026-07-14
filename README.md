# Playwright_FrameWork

A **Java + Playwright** test automation framework built on the **Page Object Model (POM)** design pattern, targeting the [OpenCart demo application](https://naveenautomationlabs.com/opencart/). It is config-driven, runs through TestNG XML suites, and produces **Allure** reports with cross-run trend graphs.

## Tech Stack

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 21 | Language |
| [Playwright for Java](https://playwright.dev/java/) | 1.60.0 | Browser automation |
| [TestNG](https://testng.org/) | 7.10.2 | Test runner & assertions |
| [Allure TestNG](https://allurereport.org/) | 2.29.1 | Test result generation |
| Allure CLI | 3.x | Report generation & trends |
| [ExtentReports](https://www.extentreports.com/) | 5.1.2 | HTML reporting |
| [Apache POI](https://poi.apache.org/) | 5.3.0 | Excel-driven test data |
| org.json | 20240303 | JSON handling (history parsing) |
| Maven + Surefire | 3.5.4 | Build & suite execution |

## Project Structure

```
Playwright_FrameWork/
├── pom.xml                              # Deps + surefire wired to the TestNG suite
└── src
    ├── main/java/com/qa/opencart
    │   ├── ai
    │   │   ├── AllureHistoryReader.java  # Reads allure-history JSONL run history
    │   │   ├── FailureClassifier.java    # Extracts failure details from ITestResult
    │   │   └── FlakyDetector.java         # Flags tests that both pass & fail in history
    │   ├── constants
    │   │   └── AppConstants.java          # Shared expected values (titles, etc.)
    │   ├── factory
    │   │   └── PlaywrightFactory.java     # Browser/context/page init + ThreadLocal<Page>
    │   ├── listners
    │   │   ├── Retry.java                 # IRetryAnalyzer (retries failed tests up to 3x)
    │   │   └── TestAllureListners.java    # ITestListener: screenshots, Allure attachments,
    │   │                                  #   history IDs, run summary & flaky reports
    │   └── pages
    │       ├── HomePage.java              # Page object: home page locators + actions
    │       └── LoginPage.java             # Page object: login page locators + actions
    └── test
        ├── java/com/qa/opencart
        │   ├── base
        │   │   └── BaseTest.java          # Setup/teardown; loads config, inits browser
        │   └── tests
        │       ├── HomePageTest.java      # Home page tests (data-driven search)
        │       └── LoginPageTest.java     # Login page tests
        └── resources
            ├── TestRunners
            │   └── testng_regression.xml  # Suite: parallel tests + listener registration
            └── config
                └── config.properties      # browser, url, credentials, env, etc.
```

### Architecture

- **`PlaywrightFactory`** — creates the `Playwright`, `Browser`, `BrowserContext`, and `Page`. Reads the browser name and target URL from `config.properties`, supports `chromium`, `firefox`, `safari` (WebKit) and branded `chrome`, and throws a clear error for invalid names. Exposes the current thread's page via a static `ThreadLocal<Page>` (`PlaywrightFactory.getPage()`) so listeners can capture screenshots.
- **`BaseTest`** — all test classes extend it. `@BeforeTest` loads `config.properties` and launches the browser; `@AfterTest` closes the context.
- **Page Objects** (`pages/`) — each page encapsulates its locators and actions (e.g. `getHomePageTitle()`, `doSearch(...)`, `navigateToLoginPage()`), supporting page-chaining.
- **`TestAllureListners`** — a TestNG `ITestListener` registered in the suite XML. Attaches Playwright screenshots on failure/skip, sets Allure `historyId`/`testCaseId` (required for v3 trends), and attaches a run summary plus a flaky-test report on finish.
- **Config-driven** — browser, URL, credentials, and environment come from `config.properties`; headless mode comes from the `-Dheadless` system property.

## Prerequisites

- JDK 21+
- Maven 3.8+
- [Allure CLI](https://allurereport.org/docs/install/) 3.x (for reports): `brew install allure`
- Playwright browser binaries (installed automatically on first run, or explicitly):

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"
```

## Configuration

Edit `src/test/resources/config/config.properties`:

```properties
browser  = chromium        # chromium | firefox | safari | chrome
url      = https://naveenautomationlabs.com/opencart/
username =                 # supply your own test credentials
password =
env      = staging
```

> **Do not commit real credentials.** Keep secrets out of version control.

## Running the Tests

Tests run through the TestNG suite (`testng_regression.xml`), which Surefire is configured to use:

```bash
mvn clean test                  # headed (default), uses config browser
mvn clean test -Dheadless=true  # headless — required on CI (no display)
```

- **Headless:** defaults to `false` so local runs are headed. CI passes `-Dheadless=true`.
- **Browser/URL:** controlled by `config.properties`.

## Allure Reporting

Test results are written to `allure-results/` on every run. Trend graphs (History, Duration, Retries, Categories) require **history accumulated across multiple runs**, stored in `allure-history/allure-history.jsonl`.

### Quick view (single run, no trends)

```bash
mvn clean test -Dheadless=true
allure serve allure-results
```

### Generate a persistent report WITH cross-run trends

The key command reads prior history for the trends **and** appends the current run to it:

```bash
allure awesome ./allure-results \
  --history-path allure-history/allure-history.jsonl \
  -o allure-report
```

Run it after each `mvn test` and the trend graphs gain one data point per run.

> **Use `awesome`, not `classic`.** In Allure 3.x the `classic` (Allure 2 compat)
> report renders a blank page. In the opened `awesome` report, the charts —
> **Current status**, **Status dynamics** (cross-run trend), **Test results by
> severities** — are under the **"Report ▾ → Graphs"** menu at the top-left.

### Populate all graphs from multiple runs (one-shot)

Use the helper script, which runs the suite N times and accumulates history so every trend graph is populated:

```bash
./scripts/allure-report.sh 4      # runs the suite 4x, builds allure-report/
allure open allure-report          # view the generated report
```

### Useful Allure commands

| Command | Purpose |
|---------|---------|
| `allure serve allure-results` | Generate a temporary report from the latest run and open it |
| `allure awesome ./allure-results --history-path allure-history/allure-history.jsonl -o allure-report` | Generate a persistent report with cross-run trend charts |
| `allure awesome ./allure-results -h allure-history/allure-history.jsonl -o allure-report --open` | Same, then open it in the browser |
| `allure open allure-report` | Serve an already-generated report directory |
| `allure history ./allure-results --history-path allure-history/allure-history.jsonl` | Append the current run to history without generating a report |

> **Do not use `allure classic`** with Allure 3.x — it renders a blank page. Use `allure awesome` (shown above); `allure generate` also defaults to the awesome report.
>
> On CI, persist `allure-history/allure-history.jsonl` between builds (cache/artifact) so trends survive across runs.

## Test Coverage

**HomePageTest**
- `homePageTitleTest` — home page title is `Your Store`
- `homePageUrlTest` — home page URL matches `config.properties`
- `homePageSearchTest` — data-driven search (`iMac`, `iphone`, `samsung`) verifies the results header

**LoginPageTest**
- `navigateToLoginPageTest` — navigates via page-chaining and verifies the title
- `forgotPasswordLinkExistTest` — the "Forgotten Password" link is present
- `appLoginTest` — logs in with credentials from `config.properties`

## Continuous Integration

GitHub Actions (`.github/workflows/main.yml`) runs the suite on every push/PR to `main`/`master`, installs Playwright browsers, and executes `mvn test -Dheadless=true`.

## Roadmap

- [x] Externalize configuration into `config.properties`
- [x] Add Login page object & tests
- [x] Data-driven tests (TestNG `@DataProvider`)
- [x] Parallel execution via TestNG XML suite
- [x] CI integration (GitHub Actions)
- [x] Allure reporting with cross-run trend graphs
- [ ] Excel-driven data via Apache POI
- [ ] Wire AspectJ weaver so `@Attachment` screenshots render on failure
- [ ] Persist Allure history on CI for long-term trends
