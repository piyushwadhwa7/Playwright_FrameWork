package com.qa.opencart.ai;

import org.testng.ITestResult;

public class FailureClassifier {

    public String extractFailureDetails(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String errorMessage = result.getThrowable() != null
                ? result.getThrowable().toString()
                : "Unknown failure";

        return "Test: " + testName + "\nError: " + errorMessage;
    }

}

