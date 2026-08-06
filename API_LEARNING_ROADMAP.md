# API Automation Learning Roadmap

**Mapped module-by-module to the syllabus you shared.**
Built for: `Playwright_FrameWork` (Java 21, Maven, TestNG, Allure, Jenkins)

---

## 0. First — where this syllabus comes from

The content you pasted is, word for word, the syllabus of:

**[Manual & Automation Testing of WebServices/API](https://naveenautomationlabs.com/manual-automation-testing-of-webservices-api/)** — Naveen Khunteta (Naveen AutomationLabs)

- ₹11,000 INR / $155 USD (listed down from ₹16,500)
- Recorded videos, lifetime access, includes GIT course + SDET notes
- **No refund policy** — worth knowing before you pay
- Also sold via [academy.naveenautomationlabs.com](https://academy.naveenautomationlabs.com/courses/api-course-61c9730b0cf20c7ec979c67a)

**Is it worth it?** The syllabus is solid and genuinely comprehensive — it's the most complete Java API syllabus publicly listed. Naveen is a real practitioner with 318K+ subscribers and a lot of free content. But roughly **70% of this syllabus is available free**, and three sections are outdated (see below). Read the module map first, then decide.

---

## 1. What's outdated or missing in this syllabus

Flagging this because studying dead material wastes weeks.

### Outdated

| Syllabus item | Status | What to do instead |
|---|---|---|
| **9.9 Twitter APIs** | **Dead.** On 6 Feb 2026 X replaced tiered pricing with pay-per-use. No free tier, no free read allowance. | Use **GitHub API** (free, excellent OAuth/pagination practice) |
| **9.5 IMDB APIs** | The free IMDb-API service is defunct | **[OMDb API](https://www.omdbapi.com/)** — free tier, 1,000 req/day |
| **6.1 JDK 11 HttpClient** | Not wrong, just dated framing | Same API, but you're on **Java 21** — use it as `java.net.http.HttpClient` |
| **9.1 GORest** | gorest.co.in was down for a period and has been rebuilt — **v2 now expects a bearer token**, v1 deprecated (sunset 2 Jun 2027) | Your `GETApiCall.java` currently hits it unauthenticated. See §4 |

### Missing (and employers ask for these)

- **Contract testing (Pact)** — the fastest-rising API requirement in 2026 job posts
- **JSON Schema validation** — asserting response *shape*, not just values
- **GraphQL** (partially covered by GoRest) and **gRPC**
- **Testcontainers** — increasingly replaces WireMock for integration-level mocking
- **AI/MCP-assisted API test generation** — now appearing in SDET listings

---

## 2. Module-by-module resource map

### Module 1 — API & WebServices fundamentals
*API, WebService, backend architecture, REST vs SOAP, CRUD, HTTP verbs*

| Resource | Cost | Why |
|---|---|---|
| **[Postman Learning Center — What is an API](https://learning.postman.com/docs/getting-started/introduction/)** | Free | Official, current, well-written |
| **[MDN — HTTP request methods](https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods)** | Free | The authoritative reference on GET/POST/PUT/PATCH/DELETE/HEAD/OPTIONS |
| **[Naveen AutomationLabs — API playlists](https://www.youtube.com/c/NaveenAutomationLabs/playlists)** | Free | Same instructor, same explanations, zero cost |

> **Time: 1 day.** Don't over-invest here. REST vs SOAP matters for interviews; you will almost never touch SOAP in a modern job.

---

### Module 2 + 3 — Postman & Newman
*Params, headers, payloads, env vars, Chai assertions, collection runner, Newman, HTML reports, mock servers, monitors, docs, workspaces, Docker, Jenkins*

| Resource | Cost | Why |
|---|---|---|
| **[Postman Academy — API Fundamentals Student Expert](https://academy.postman.com/path/postman-api-fundamentals-student-expert)** | **Free + certification** | Official, hands-on, and you get a **Credly badge for your CV** (badges moved from Badgr to Credly on 21 Jul 2026) |
| **[Postman Academy](https://academy.postman.com/)** | Free | Full catalogue: automation, mock servers, monitors, workspaces |
| **[Postman Learning Center docs](https://learning.postman.com/)** | Free | Reference for every single sub-topic in modules 2 and 3 |
| **[Newman docs](https://learning.postman.com/docs/collections/using-newman-cli/command-line-integration-with-newman/)** | Free | CLI options, HTML reporters, CI integration |

**This covers modules 2 and 3 completely — all 41 sub-topics — for free, from the vendor.** Postman's own material is better and more current than any third-party course, because the product changes every few months.

For **3.25 (Docker)** and **3.26 (Jenkins)**: you already run Jenkins. Add a stage that runs `newman run collection.json -r htmlextra`. That's a 30-minute task, not a course.

> **Time: 1 week.** Get the certification — it's free and it's a real line on a CV.

---

### Module 4 — APIs at the network layer (DevTools)

| Resource | Cost | Why |
|---|---|---|
| **[Chrome DevTools — Network panel](https://developer.chrome.com/docs/devtools/network)** | Free | Official docs |
| **Your own Playwright framework** | Free | You already have `page.onRequest()` / `page.onResponse()` available — instrument `PlaywrightFactory` to log API calls during UI tests |

> **Time: half a day.** Best learned by doing, not watching.

---

### Module 5 — HTTP status codes (1xx–5xx)

| Resource | Cost | Why |
|---|---|---|
| **[MDN — HTTP response status codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)** | Free | The reference everyone actually uses |
| **[GoRest.in](https://gorest.in/)** | Free | Exposes **13 real status codes** on demand — 400/401/403/404/405/415/422/429/500 etc. Practice asserting each one |
| **[gorest.co.in](https://gorest.co.in/)** | Free | `?force_status=500` forces any error code; `?delay=1500` simulates latency up to 5s |

> **Time: 2 hours.** Then write one TestNG test per status code. That single exercise is worth more than the video.

---

### Module 6 — JDK HttpClient
*JDK 11 HttpClient, CRUD, payloads, query & path params*

| Resource | Cost | Why |
|---|---|---|
| **[Baeldung — Exploring the New HTTP Client in Java](https://www.baeldung.com/java-9-http-client)** | Free | The canonical Java tutorial site; precise and current |
| **[Baeldung — Posting with Java HttpClient](https://www.baeldung.com/java-httpclient-post)** | Free | Sync, async, concurrent POSTs, auth, JSON bodies, file upload |
| **[OpenJDK — Introduction to the Java HTTP Client](https://openjdk.org/groups/net/httpclient/intro.html)** | Free | From the people who wrote it. Now covers HTTP/3 too |
| **[ZetCode — Java HttpClient](https://zetcode.com/java/httpclient/)** | Free | Compact worked examples |

> **Time: 2 days.** Honestly low priority — nobody builds production API frameworks on raw `HttpClient`. It's interview material ("how would you do this without a library?") and it teaches you what REST Assured is hiding from you. Worth exactly that much, no more.

---

### Module 7 — REST Assured (the core module)

This is 60% of the syllabus and 90% of the job value. Spend your money here if you spend it anywhere.

#### 7a. Core REST Assured (BDD + non-BDD, auth, POJO, JsonPath, Hamcrest, serialization)

| Resource | Cost | Why |
|---|---|---|
| **[Rest API Testing from Scratch — REST Assured Java](https://www.udemy.com/course/rest-api-automation-testing-rest-assured/)** — Rahul Shetty | ~₹500–3,000 on sale | **4.6★, 47,705 ratings.** Updated Jan 2026 with GraphQL. Covers modules 1–7 almost exactly. Frequently on sale — never pay full price |
| **[Automating your API tests with REST Assured](https://testautomationu.applitools.com/automating-your-api-tests-with-rest-assured/)** — Bas Dijkstra, TAU | **Free** | 6 chapters, Java + JUnit. Shorter and more rigorous than the Udemy option. Ch.3 parameterization, Ch.4 code reuse (RequestSpecification), Ch.5 XML responses, **Ch.6 (de-)serialization of Java objects** — the single best free explanation of syllabus item 7.1.10 |
| **[REST Assured official Usage Guide](https://github.com/rest-assured/rest-assured/wiki/Usage)** | Free | Covers GIVEN/WHEN/THEN, LOG/ALL, RequestSpecification, ResponseSpecBuilder, JsonPath, XmlPath, EXTRACT — every item in 7.1 |
| **[Hamcrest tutorial](https://hamcrest.org/JavaHamcrest/tutorial)** | Free | Item 7.1.11 |

**Note on auth (item 7.6):** Basic, Preemptive, Digest, Bearer, OAuth1/OAuth2, API key/secret. The REST Assured wiki covers all of these in one page. Practice OAuth2 against the **[GitHub API](https://docs.github.com/en/rest)** — free, real, and a better talking point than a tutorial app.

#### 7b. End-to-end framework (Maven, TestNG, DataProviders, POI, Allure, Git, Jenkins, Docker)

**You already know this entire section.** Look at your own repo:

- ✅ Maven project setup, folder structure → your `pom.xml` + `com.qa.opencart` package layout
- ✅ TestNG + testng.xml runners → `src/test/resources/TestRunners`
- ✅ Apache POI for Excel data-driven → already in your `pom.xml`
- ✅ Constants files → your `AppConstants.java`
- ✅ Allure + Extent reporting → already wired, with `TestAllureListners` and `allure-results/`
- ✅ Git, Jenkins CI/CD → your `Jenkinsfile` and `.github/`
- ⬜ Docker image + Docker Hub → the one genuine gap

**So skip 7.2 entirely except Docker.** Paying ₹11,000 to be taught the framework skills you've already implemented is the main reason to hesitate on this course.

For Docker: **[Docker's official Java guide](https://docs.docker.com/language/java/)** + write a `Dockerfile` that runs `mvn test`. One evening.

> **Time: 3 weeks for 7a. Skip most of 7b.**

---

### Module 8 — WireMock

| Resource | Cost | Why |
|---|---|---|
| **[WireMock — Java usage](https://wiremock.org/docs/java-usage/)** | Free | Official. Covers items 8.1–8.3 directly |
| **[WireMock — Stubbing](https://wiremock.org/docs/stubbing/)** | Free | Official stubbing reference |
| **[Baeldung — Introduction to WireMock](https://www.baeldung.com/introduction-to-wiremock)** | Free | Best worked walkthrough |

> **Time: 3 days.** Then integrate into your framework (item 8.4) — add a `WireMockServer` to a TestNG `@BeforeSuite` and stub one dependency.

---

### Module 9 — Real-time API practice targets

| Syllabus item | Use this | Notes |
|---|---|---|
| GoRest with auth | **[gorest.co.in](https://gorest.co.in/)** | Sign in with GitHub/Google → generate bearer token. v2 direct responses, v1 envelope (deprecated, sunset 2 Jun 2027). Rate limit configurable 1–300/min |
| GoRest (no signup) | **[gorest.in](https://gorest.in/)** | Built by Naveen as a drop-in replacement. **GETs need no token.** 13 status codes, JSON + XML, rate limiting, downloadable Postman collection |
| ReqRes | **[reqres.in](https://reqres.in/)** | Classic CRUD + delayed responses for latency testing |
| Booker APIs | **[restful-booker](https://restful-booker.herokuapp.com/)** | Deliberately contains bugs — great for practising bug reporting |
| Weather / Forecast | **[OpenWeatherMap](https://openweathermap.org/api)** | Free tier, API-key auth — good for item 7.6 |
| ~~IMDB~~ | **[OMDb API](https://www.omdbapi.com/)** | 1,000 req/day free |
| OAuth APIs | **[GitHub API](https://docs.github.com/en/rest)** | Best free OAuth2 + pagination + rate-limit-header practice |
| ~~Twitter~~ | **Skip** | No free tier since 6 Feb 2026 |
| Swagger | **[Swagger Petstore](https://petstore.swagger.io/)** | Item 9.10 — also use it to practise generating collections from an OpenAPI spec (item 3.7) |

---

## 3. What to add that the syllabus doesn't cover

| Topic | Resource | Cost |
|---|---|---|
| **Contract testing — concepts** | **[Contract Tests with Pact — TAU](https://testautomationu.applitools.com/pact-contract-tests/)** (8 chapters) | Free |
| **Contract testing — Java** | **[Baeldung — Consumer Driven Contracts with Pact](https://www.baeldung.com/pact-junit-consumer-driven-contracts)** + **[pact-jvm JUnit5 docs](https://docs.pact.io/implementation_guides/jvm/consumer/junit5)** | Free |
| **JSON Schema validation** | `io.rest-assured:json-schema-validator` + [REST Assured wiki](https://github.com/rest-assured/rest-assured/wiki/Usage#json-schema-validation) | Free |
| **Testcontainers** | [testcontainers.com/guides](https://testcontainers.com/guides/) | Free |
| **API security basics** | [OWASP API Security Top 10](https://owasp.org/API-Security/) | Free |

> ⚠️ **Note on the TAU Pact course:** it teaches the concepts using **`pact-js` (JavaScript)**, not Java. The course itself says "Pact supports a variety of languages but in this course we will focus on JavaScript." The concepts transfer cleanly — consumer/provider, matchers, the broker, CI integration — but for Java implementation use the Baeldung article and `au.com.dius.pact.consumer:junit5` alongside it.

Pact is the highest-leverage item on this page. It appears constantly in senior job descriptions and almost no candidates have hands-on experience.

---

## 4. Fix this in your repo first (30 minutes)

Your `src/test/java/com/qa/opencart/tests/GETApiCall.java`:

```java
// Current problems:
Playwright playwright = Playwright.create();     // 1. Created per test, never closed → resource leak
APIRequestContext requestContext = request.newContext();
APIResponse apiResponse = requestContext.get(
    "https://gorest.co.in/public/v2/users");     // 2. Hardcoded URL, no bearer token
...
JsonNode jsonResponse = mapper.readTree(...);    // 3. String-walking instead of POJOs
System.out.println(...);                         // 4. Printing instead of asserting
```

Five concrete fixes:

1. **Move Playwright/APIRequestContext lifecycle into `BaseTest`** (`@BeforeSuite` / `@AfterSuite`) — it currently leaks a browser process per test
2. **Move the base URL into `AppConstants`**, and add a bearer token from an env var (gorest.co.in v2 now expects one; or point at `gorest.in` where GETs are open)
3. **Add a `User` POJO** and use `mapper.readValue(body, User[].class)` instead of `readTree` — this is syllabus item 7.1.10, and it's the single clearest junior→mid signal in API code
4. **Replace `System.out.println` with assertions** — printing isn't testing
5. **Extend to full CRUD** — POST → capture ID → GET → PUT → DELETE, asserting at each step

---

## 5. Two paths

### Path A — Free (8 weeks, ₹0)

| Week | Focus |
|---|---|
| 1 | Postman Academy Student Expert certification (modules 1–3) + MDN status codes (module 5) |
| 2 | Bas Dijkstra's TAU REST Assured course + official Usage Guide (module 7a) |
| 3–4 | Build REST Assured tests inside your existing framework, all auth types (module 7.6) |
| 5 | Baeldung Java HttpClient (module 6) + DevTools instrumentation (module 4) |
| 6 | WireMock official docs (module 8) |
| 7 | **Pact contract testing** (TAU, free) — the differentiator |
| 8 | Dockerise the framework + Newman-in-Jenkins stage |

### Path B — Paid, best value

**[Rahul Shetty's REST Assured course](https://www.udemy.com/course/rest-api-automation-testing-rest-assured/)** (~₹500 on sale, 4.6★ / 47,705 ratings) + everything free in Path A.

That gets you a more complete module-7 treatment than Naveen's course, updated Jan 2026 with GraphQL, for roughly **1/20th the price** — and you skip section 7.2 because you've already built it.

### On Naveen's ₹11,000 course

Buy it if you want one instructor, one voice, one Box folder, and no decisions to make — that convenience is real and some people learn much better that way. His free YouTube content is genuinely good, which is decent evidence the paid material is too.

Don't buy it if: you're comfortable assembling free resources, and you recognise that section 7.2 (a third of the automation content) teaches Maven/TestNG/POI/Allure/Git/Jenkins — all of which you have already shipped in this repo.

---

## Sources

- [Manual & Automation Testing of WebServices/API — Naveen AutomationLabs](https://naveenautomationlabs.com/manual-automation-testing-of-webservices-api/)
- [Rest API Testing (Automation) from Scratch — REST Assured Java (Udemy)](https://www.udemy.com/course/rest-api-automation-testing-rest-assured/)
- [Postman Academy — API Fundamentals Student Expert](https://academy.postman.com/path/postman-api-fundamentals-student-expert)
- [Automating your API tests with REST Assured — Test Automation University](https://testautomationu.applitools.com/automating-your-api-tests-with-rest-assured/)
- [Contract Tests with Pact — Test Automation University](https://testautomationu.applitools.com/pact-contract-tests/)
- [REST Assured Usage Guide](https://github.com/rest-assured/rest-assured/wiki/Usage)
- [WireMock — Java usage](https://wiremock.org/docs/java-usage/)
- [Baeldung — Exploring the New HTTP Client in Java](https://www.baeldung.com/java-9-http-client)
- [Go REST (gorest.co.in)](https://gorest.co.in/) and [GoRest.in](https://gorest.in/)
- [X (Twitter) API pricing 2026 — pay-per-use replaced tiers on 6 Feb 2026](https://postproxy.dev/blog/x-api-pricing-2026/)
