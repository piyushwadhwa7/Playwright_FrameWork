package com.qa.opencart.ai;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class AllureHistoryReader {

    private static final String HISTORY_FILE =
            "allure-history/allure-history.jsonl";

    public Map<String, List<String>> readHistory() {

        Map<String, List<String>> historyMap = new HashMap<>();

        try {

            if (!Files.exists(Paths.get(HISTORY_FILE))) {
                return historyMap;
            }

            List<String> lines = Files.readAllLines(
                    Paths.get(HISTORY_FILE)
            );

            for (String line : lines) {

                JSONObject run = new JSONObject(line);
                JSONObject testResults = run.getJSONObject("testResults");

                for (String key : testResults.keySet()) {

                    JSONObject test = testResults.getJSONObject(key);

                    String testName = test.getString("fullName");
                    String status = test.getString("status");

                    historyMap
                            .computeIfAbsent(testName, k -> new ArrayList<>())
                            .add(status);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return historyMap;
    }
}
