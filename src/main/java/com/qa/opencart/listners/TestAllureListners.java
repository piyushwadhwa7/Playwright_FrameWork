package com.qa.opencart.listners;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import com.qa.opencart.ai.AllureHistoryReader;
import com.qa.opencart.ai.FailureClassifier;
import com.qa.opencart.ai.FlakyDetector;
import com.qa.opencart.factory.PlaywrightFactory;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.model.Parameter;
import org.json.JSONObject;
import org.testng.IAnnotationTransformer;
import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/** Adds metadata, evidence, retries, and history identity to Allure results. */
public class TestAllureListners implements
        ITestListener,
        IAnnotationTransformer,
        IExecutionListener {

    private static final String BROWSER_ATTRIBUTE = "report.browser";
    private static final String ENVIRONMENT_ATTRIBUTE = "report.environment";
    private static final String HISTORY_KEY_ATTRIBUTE = "report.history.key";
    private static final Path HISTORY_FILE = Path.of("allure-history", "allure-history.jsonl");
    private static final Object METADATA_LOCK = new Object();
    private static final ThreadLocal<Boolean> TRACE_ACTIVE = ThreadLocal.withInitial(() -> false);
    private static final Map<String, String> CURRENT_RUN_RESULTS = new ConcurrentHashMap<>();

    /** @return the plain method name of the given test result. */
    private static String getTestMethodName(ITestResult result) {
        return result.getMethod().getConstructorOrMethod().getName();
    }

    /** Captures a PNG screenshot of the given page for Allure. */
    @Attachment(value = "Page screenshot", type = "image/png")
    public byte[] saveScreenshotPNG(Page page) {
        return page.screenshot();
    }

    /** Attaches a plain-text message to the current Allure test. */
    @Attachment(value = "{0}", type = "text/plain")
    public static String saveTextLog(String message) {
        return message;
    }

    /** Attaches an HTML snippet to the current Allure test. */
    @Attachment(value = "{0}", type = "text/html")
    public static String attachHtml(String html) {
        return html;
    }

    @Override
    public void onStart(ITestContext context) {
        writeRunMetadata(context);
        System.out.println("Starting TestNG block: " + context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        String browser = resolveBrowser(result);
        String environment = resolveEnvironment(result);
        String suite = result.getTestContext().getSuite().getName();
        String testBlock = result.getTestContext().getName();
        String dataHash = shortHash(Arrays.deepToString(result.getParameters()));
        String rawIdentity = result.getTestClass().getName()
                + "." + result.getMethod().getMethodName()
                + "|suite=" + suite
                + "|test=" + testBlock
                + "|browser=" + browser
                + "|environment=" + environment
                + "|parameters=" + Arrays.deepToString(result.getParameters());
        String historyKey = result.getTestClass().getName()
                + "." + result.getMethod().getMethodName()
                + "|browser=" + browser
                + "|environment=" + environment
                + "|data=" + dataHash;

        result.setAttribute(BROWSER_ATTRIBUTE, browser);
        result.setAttribute(ENVIRONMENT_ATTRIBUTE, environment);
        result.setAttribute(HISTORY_KEY_ATTRIBUTE, historyKey);

        Allure.label("browser", browser);
        Allure.label("environment", environment);
        Allure.label("commit", commit());
        Allure.label("buildNumber", buildNumber());
        Allure.label("suite", suite);
        Allure.label("testBlock", testBlock);
        if (result.getParameters().length > 0) {
            Allure.parameter("dataProviderId", dataHash, false, Parameter.Mode.DEFAULT);
        }

        String identityHash = sha256(rawIdentity);
        Allure.getLifecycle().updateTestCase(test -> {
            test.setHistoryId(identityHash);
            test.setTestCaseId(identityHash);
        });

        startTrace();
        System.out.println("Allure identity set for: " + historyKey);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        maskAllureParameters();
        stopTrace(false, result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        maskAllureParameters();
        stopTrace(true, result);

        Page page = PlaywrightFactory.getPage();
        if (page != null) {
            saveScreenshotPNG(page);
        }

        String testName = getTestMethodName(result);
        saveTextLog("Test failed: " + testName);

        FailureClassifier classifier = new FailureClassifier();
        String failureDetails = classifier.extractFailureDetails(result);
        Allure.addAttachment("Failure Details", "text/plain", failureDetails);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        maskAllureParameters();
        stopTrace(false, result);
        Page page = PlaywrightFactory.getPage();
        if (page != null) {
            saveScreenshotPNG(page);
        }
        saveTextLog("Test skipped: " + result.getName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        stopTrace(false, result);
    }

    /** Records final TestNG statuses after retries have been resolved. */
    @Override
    public void onFinish(ITestContext context) {
        recordStatuses(context.getPassedTests().getAllResults(), "passed");
        recordStatuses(context.getFailedTests().getAllResults(), "failed");
        recordStatuses(context.getSkippedTests().getAllResults(), "skipped");

        int total = context.getAllTestMethods().length;
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();
        String summary = "Test Execution Summary:\n"
                + "Total Tests: " + total + "\n"
                + "Passed: " + passed + "\n"
                + "Failed: " + failed + "\n"
                + "Skipped: " + skipped + "\n"
                + "Pass Percentage: "
                + String.format("%.2f", total == 0 ? 0.0 : passed * 100.0 / total)
                + "%\n";

        try {
            Allure.addAttachment("Run Summary", "text/plain", summary);
            AllureHistoryReader reader = new AllureHistoryReader();
            Map<String, List<String>> historyMap = reader.readHistory();
            FlakyDetector detector = new FlakyDetector();
            StringBuilder flakyReport = new StringBuilder("Flaky Test Analysis\n\n");
            boolean foundFlaky = false;
            for (String testName : historyMap.keySet()) {
                if (detector.isFlaky(testName, historyMap)) {
                    foundFlaky = true;
                    flakyReport.append("Flaky Test: ").append(testName)
                            .append("\nHistory: ").append(historyMap.get(testName)).append("\n\n");
                }
            }
            if (!foundFlaky) {
                flakyReport.append("No flaky tests detected based on history.");
            }
            Allure.addAttachment("AI Flaky Test Report", "text/plain", flakyReport.toString());
        } catch (RuntimeException exception) {
            System.err.println("Unable to attach run summary: " + exception.getMessage());
        }
    }

    /** Appends one complete run to the history artifact after all TestNG blocks finish. */
    @Override
    public void onExecutionFinish() {
        if (CURRENT_RUN_RESULTS.isEmpty()) {
            return;
        }

        JSONObject testResults = new JSONObject();
        CURRENT_RUN_RESULTS.forEach((key, status) -> {
            JSONObject result = new JSONObject();
            result.put("fullName", key);
            result.put("status", status);
            testResults.put(sha256(key), result);
        });

        JSONObject run = new JSONObject();
        run.put("runId", UUID.randomUUID().toString());
        run.put("recordedAt", Instant.now().toString());
        run.put("testResults", testResults);

        try {
            Files.createDirectories(HISTORY_FILE.getParent());
            Files.writeString(
                    HISTORY_FILE,
                    run + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException exception) {
            System.err.println("Unable to persist Allure history: " + exception.getMessage());
        } finally {
            CURRENT_RUN_RESULTS.clear();
        }
    }

    /** Enables retries only when explicitly requested, preserving current default behavior. */
    @Override
    public void transform(
            ITestAnnotation annotation,
            Class testClass,
            Constructor testConstructor,
            Method testMethod) {
        if (Boolean.parseBoolean(System.getProperty("retry.enabled", "false"))
                && annotation.getRetryAnalyzerClass() == null) {
            annotation.setRetryAnalyzer(Retry.class);
        }
    }

    private static void recordStatuses(Iterable<ITestResult> results, String status) {
        for (ITestResult result : results) {
            String key = (String) result.getAttribute(HISTORY_KEY_ATTRIBUTE);
            if (key == null) {
                key = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
            }
            CURRENT_RUN_RESULTS.put(key, status);
        }
    }

    /** Prevents the Allure TestNG adapter from persisting raw data-provider values. */
    private static void maskAllureParameters() {
        Allure.getLifecycle().updateTestCase(test -> {
            test.getParameters().forEach(parameter -> {
                if (!"dataProviderId".equals(parameter.getName())) {
                    parameter.setValue("[MASKED]");
                    parameter.setMode(Parameter.Mode.MASKED);
                }
            });
            test.getLabels().forEach(label -> {
                if ("thread".equals(label.getName())) {
                    label.setValue("[MASKED]");
                }
            });
        });
    }

    private static void startTrace() {
        if (!Boolean.parseBoolean(System.getProperty("trace.on.failure", "true"))) {
            return;
        }
        BrowserContext context = PlaywrightFactory.getBrowserContext();
        if (context == null) {
            return;
        }
        try {
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));
            TRACE_ACTIVE.set(true);
        } catch (RuntimeException exception) {
            TRACE_ACTIVE.set(false);
            System.err.println("Unable to start Playwright trace: " + exception.getMessage());
        }
    }

    private static void stopTrace(boolean attach, ITestResult result) {
        if (!Boolean.TRUE.equals(TRACE_ACTIVE.get())) {
            return;
        }

        BrowserContext context = PlaywrightFactory.getBrowserContext();
        try {
            if (context == null) {
                return;
            }
            if (!attach) {
                context.tracing().stop();
                return;
            }

            Files.createDirectories(Path.of("target", "traces"));
            Path tracePath = Path.of(
                    "target", "traces",
                    safeFileName(getTestMethodName(result)) + "-" + System.nanoTime() + ".zip"
            );
            context.tracing().stop(new Tracing.StopOptions().setPath(tracePath));
            try (InputStream trace = Files.newInputStream(tracePath)) {
                Allure.addAttachment("Playwright trace", "application/zip", trace, ".zip");
            }
        } catch (IOException | RuntimeException exception) {
            System.err.println("Unable to persist Playwright trace: " + exception.getMessage());
        } finally {
            TRACE_ACTIVE.remove();
        }
    }

    private static void writeRunMetadata(ITestContext context) {
        synchronized (METADATA_LOCK) {
            try {
                Path results = Path.of("allure-results");
                Files.createDirectories(results);
                Path environment = results.resolve("environment.properties");
                if (!Files.exists(environment)) {
                    String browsers = context.getSuite().getXmlSuite().getTests().stream()
                            .map(test -> test.getParameter("browser"))
                            .filter(browser -> browser != null && !browser.isBlank())
                            .distinct()
                            .collect(Collectors.joining(","));
                    String properties = "browser=" + (browsers.isBlank() ? "n/a" : browsers) + "\n"
                            + "environment=" + environment(context) + "\n"
                            + "commit=" + commit() + "\n"
                            + "buildNumber=" + buildNumber() + "\n"
                            + "suite=" + context.getSuite().getName() + "\n";
                    Files.writeString(environment, properties);
                }

                Path executor = results.resolve("executor.json");
                if (!Files.exists(executor)) {
                    JSONObject metadata = new JSONObject();
                    metadata.put("name", "Jenkins");
                    metadata.put("type", "jenkins");
                    metadata.put("buildOrder", buildNumber());
                    metadata.put("buildName", System.getenv().getOrDefault("JOB_NAME", "local"));
                    metadata.put("buildUrl", System.getenv().getOrDefault("BUILD_URL", ""));
                    Files.writeString(executor, metadata.toString());
                }
            } catch (IOException exception) {
                System.err.println("Unable to write Allure run metadata: " + exception.getMessage());
            }
        }
    }

    private static String resolveBrowser(ITestResult result) {
        String browser = PlaywrightFactory.getBrowserName();
        if (browser == null || browser.isBlank()) {
            browser = result.getTestContext().getCurrentXmlTest().getParameter("browser");
        }
        return browser == null || browser.isBlank() ? "n/a" : browser.trim();
    }

    private static String resolveEnvironment(ITestResult result) {
        String environment = PlaywrightFactory.getEnvironmentName();
        if (environment == null || environment.isBlank()) {
            environment = environment(result.getTestContext());
        }
        return environment;
    }

    private static String environment(ITestContext context) {
        String environment = System.getProperty("test.env", System.getenv().getOrDefault("TEST_ENV", "local"));
        return environment == null || environment.isBlank() ? "local" : environment.trim();
    }

    private static String commit() {
        return firstNonBlank(
                System.getenv("GIT_COMMIT"),
                System.getenv("GITHUB_SHA"),
                "local"
        );
    }

    private static String buildNumber() {
        return firstNonBlank(
                System.getenv("BUILD_NUMBER"),
                System.getenv("BUILD_ID"),
                "local"
        );
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "unknown";
    }

    private static String safeFileName(String value) {
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static String shortHash(String value) {
        return sha256(value).substring(0, 16);
    }

    private static String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte item : digest) {
                hex.append(String.format("%02x", item));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
