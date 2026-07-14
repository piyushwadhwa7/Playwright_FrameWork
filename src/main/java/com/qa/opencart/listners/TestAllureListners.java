package com.qa.opencart.listners;

import com.microsoft.playwright.Page;
import com.qa.opencart.ai.AllureHistoryReader;
import com.qa.opencart.ai.FailureClassifier;
import com.qa.opencart.ai.FlakyDetector;
import com.qa.opencart.factory.PlaywrightFactory;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.List;
import java.util.Map;

public class TestAllureListners implements ITestListener {

    private static String getTestMethodName(ITestResult iTestResult) {
        return iTestResult.getMethod().getConstructorOrMethod().getName();
    }

    // Image attachment for Allure (Playwright screenshot)
    @Attachment(value = "Page screenshot", type = "image/png")
    public byte[] saveScreenshotPNG(Page page) {
        return page.screenshot();
    }

    // Text attachment for Allure
    @Attachment(value = "{0}", type = "text/plain")
    public static String saveTextLog(String message) {
        return message;
    }

    // HTML attachment for Allure
    @Attachment(value = "{0}", type = "text/html")
    public static String attachHtml(String html) {
        return html;
    }

    @Override
    public void onStart(ITestContext iTestContext) {
        System.out.println("I am in onStart method " + iTestContext.getName());
    }

    @Override
    public void onFinish(ITestContext context) {

        System.out.println("I am in onFinish method " + context.getName());

        int total = context.getAllTestMethods().length;
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();

        String summary = """
        Test Execution Summary:
        -----------------------
        Total Tests: %d
        Passed: %d
        Failed: %d
        Skipped: %d
        Pass Percentage: %.2f%%
        """
                .formatted(
                        total,
                        passed,
                        failed,
                        skipped,
                        total == 0 ? 0.0 : (passed * 100.0 / total)
                );

        try {

            // ===============================
            // 🔹 1. Run Summary
            // ===============================
            Allure.addAttachment(
                    "Run Summary",
                    "text/plain",
                    summary
            );

            // ===============================
            // 🔹 2. Flaky Detection Section
            // ===============================
            AllureHistoryReader reader = new AllureHistoryReader();
            Map<String, List<String>> historyMap = reader.readHistory();

            FlakyDetector detector = new FlakyDetector();

            StringBuilder flakyReport = new StringBuilder();
            flakyReport.append("Flaky Test Analysis\n\n");

            boolean foundFlaky = false;

            for (String testName : historyMap.keySet()) {

                boolean isFlaky = detector.isFlaky(testName, historyMap);

                if (isFlaky) {
                    foundFlaky = true;

                    flakyReport
                            .append("⚠ Flaky Test: ")
                            .append(testName)
                            .append("\nHistory: ")
                            .append(historyMap.get(testName))
                            .append("\n\n");
                }
            }

            if (!foundFlaky) {
                flakyReport.append("No flaky tests detected based on history.");
            }

            Allure.addAttachment(
                    "AI Flaky Test Report",
                    "text/plain",
                    flakyReport.toString()
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {
        System.out.println("I am in onTestStart method " + getTestMethodName(iTestResult) + " start");
        String className = iTestResult.getTestClass().getName();
        String methodName = iTestResult.getMethod().getMethodName();

        String uniqueId = className + "." + methodName;

        Allure.getLifecycle().updateTestCase(tc -> {
            tc.setHistoryId(uniqueId);     // for trend
            tc.setTestCaseId(uniqueId);   // REQUIRED for Allure v3 history
        });

        System.out.println("History + TestCaseId set for: " + uniqueId);
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        System.out.println("I am in onTestSuccess method " + getTestMethodName(iTestResult) + " succeed");
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {

        String testName = getTestMethodName(iTestResult);
        System.out.println("Test failed: " + testName);

        Page page = PlaywrightFactory.getPage();

        // Screenshot
        if (page != null) {
            saveScreenshotPNG(page);
        }

        // Log basic failure
        saveTextLog("Test failed: " + testName);

        // Attach failure details to Allure
        FailureClassifier classifier = new FailureClassifier();
        String failureDetails = classifier.extractFailureDetails(iTestResult);
        Allure.addAttachment("Failure Details", "text/plain", failureDetails);
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        System.out.println("I am in onTestSkipped method " + getTestMethodName(iTestResult) + " skipped");
        Page page = PlaywrightFactory.getPage();

        if (page != null) {
            saveScreenshotPNG(page);
        }

        saveTextLog("Test skipped: " + iTestResult.getName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        System.out.println("Test failed but it is in defined success ratio " + getTestMethodName(iTestResult));
    }

}
