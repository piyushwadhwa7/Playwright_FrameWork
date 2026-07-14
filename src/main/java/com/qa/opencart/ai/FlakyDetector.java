package com.qa.opencart.ai;
import java.util.List;
import java.util.Map;

public class FlakyDetector {

    public boolean isFlaky(String testName, Map<String, List<String>> history) {

        List<String> pastResults = history.get(testName);

        if (pastResults == null || pastResults.size() < 3)
            return false;

        boolean hasPass = pastResults.contains("passed");
        boolean hasFail = pastResults.contains("failed");

        return hasPass && hasFail;
    }
}

