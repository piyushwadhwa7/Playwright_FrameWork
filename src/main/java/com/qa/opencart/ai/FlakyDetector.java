package com.qa.opencart.ai;
import java.util.List;
import java.util.Map;

/**
 * Decides whether a test is "flaky" — i.e. inconsistent — by looking at its
 * past run statuses. A test is flaky if, over enough runs, it has both passed
 * and failed.
 */
public class FlakyDetector {

    /**
     * @param testName the test's full name to check
     * @param history  map of test name to its list of past statuses
     * @return true if the test has at least 3 recorded runs and has both
     *         "passed" and "failed" among them; false otherwise
     */
    public boolean isFlaky(String testName, Map<String, List<String>> history) {

        List<String> pastResults = history.get(testName);

        if (pastResults == null || pastResults.size() < 3)
            return false;

        boolean hasPass = pastResults.contains("passed");
        boolean hasFail = pastResults.contains("failed");

        return hasPass && hasFail;
    }
}
