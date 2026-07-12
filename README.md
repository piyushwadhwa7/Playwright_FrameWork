# Playwright_FrameWork

A **Java + Playwright** test automation framework built on the **Page Object Model (POM)** design pattern, targeting the [OpenCart demo application](https://naveenautomationlabs.com/opencart/).

## Tech Stack

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 21 | Language |
| [Playwright for Java](https://playwright.dev/java/) | 1.60.0 | Browser automation |
| [TestNG](https://testng.org/) | 7.10.2 | Test runner & assertions |
| [Allure TestNG](https://allurereport.org/) | 2.29.1 | Test reporting |
| [ExtentReports](https://www.extentreports.com/) | 5.1.2 | HTML reporting |
| [Apache POI](https://poi.apache.org/) | 5.3.0 | Excel-driven test data |
| org.json | 20240303 | JSON handling |
| Maven | — | Build & dependency management |

## Project Structure

```
Playwright_FrameWork/
├── pom.xml
└── src
    ├── main/java/com/qa/opencart
    │   ├── factory
    │   │   └── PlaywrightFactory.java   # Browser/context/page initialization
    │   └── pages
    │       └── HomePage.java            # Page object: locators + page actions
    └── test/java/com/qa/opencart
        └── tests
            └── HomePageTest.java        # TestNG tests (title, URL, search)
```

### Design Pattern

- **Factory** (`PlaywrightFactory`) — creates the `Playwright`, `Browser`, `BrowserContext`, and `Page` instances. Supports `chromium`, `firefox`, `safari` (WebKit), and branded `chrome`; throws a clear error for invalid browser names.
- **Page Objects** (`pages/`) — each page class encapsulates its locators and exposes action methods (e.g. `getHomePageTitle()`, `doSearch(...)`), keeping tests free of locator details.
- **Tests** (`tests/`) — TestNG classes that consume page objects and assert behavior.

## Prerequisites

- JDK 21+
- Maven 3.8+
- Playwright browser binaries (downloaded automatically on first run, or install explicitly):

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

## Running the Tests

```bash
mvn clean test
```

To run a single test class:

```bash
mvn clean test -Dtest=HomePageTest
```

> The browser is currently selected in the test's `setup()` via `pf.initBrowser("chromium")`. Tests run headed (`setHeadless(false)`).

## Reports

Allure results are written to `allure-results/`. To view the report:

```bash
allure serve allure-results
```

## Current Test Coverage

- `homePageTitleTest` — verifies the home page title is `Your Store`
- `homePageUrlTest` — verifies the home page URL
- `homePageSearchTest` — searches for a product and verifies the results-page header

## Roadmap

- [ ] Externalize configuration (browser, URL, headless) into a `config.properties` file
- [ ] Add more page objects (Login, Registration, Product, Cart)
- [ ] Data-driven tests using Apache POI (Excel) / JSON
- [ ] Parallel execution via TestNG XML suites
- [ ] CI integration (GitHub Actions)
