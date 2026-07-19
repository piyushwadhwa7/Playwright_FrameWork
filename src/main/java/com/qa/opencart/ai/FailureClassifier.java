package com.qa.opencart.ai;

import org.testng.ITestResult;

/**
 * Turns a failed TestNG result into a short, human-readable failure summary
 * (test name + error message) that the Allure listener attaches to the report.
 */
public class FailureClassifier {

    /**
     * Builds a two-line summary of a failed test.
     *
     * @param result the TestNG result for the failed test
     * @return a string like {@code "Test: appLoginTest\nError: ..."};
     *         uses "Unknown failure" when no throwable is present
     */
    public String extractFailureDetails(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String errorMessage = result.getThrowable() != null
                ? result.getThrowable().toString()
                : "Unknown failure";

        return "Test: " + testName + "\nError: " + errorMessage;
    }

}
