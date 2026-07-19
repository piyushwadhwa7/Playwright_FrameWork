package com.qa.opencart.listners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * TestNG retry analyzer: automatically re-runs a failing test a few times
 * before marking it failed, which helps ride out transient/flaky failures.
 */
public class Retry implements IRetryAnalyzer {

    private int count = 0;
    private static final int maxTry = 3;

    /**
     * Called by TestNG after a test fails. Returns true to request a re-run.
     *
     * @param result the failed test's result
     * @return true if the test should run again (up to {@code maxTry} times),
     *         false once the retry budget is exhausted
     */
    @Override
    public boolean retry(ITestResult result) {

        if (count < maxTry) {
            count++;
            System.out.println("Retrying test: "
                    + result.getName()
                    + " | Retry count: " + count);
            return true;
        }
        return false;
    }
}
