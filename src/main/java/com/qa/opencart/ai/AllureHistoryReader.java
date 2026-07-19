package com.qa.opencart.ai;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Reads the accumulated Allure history file ({@code allure-history.jsonl}) and
 * flattens it into a per-test list of past statuses. Used by the flaky-test
 * analysis in the Allure listener.
 */
public class AllureHistoryReader {

    private static final String HISTORY_FILE =
            "allure-history/allure-history.jsonl";

    /**
     * Parses every run recorded in the history file and builds a map of
     * test full-name to the list of statuses it has had across runs
     * (e.g. {@code "...appLoginTest" -> [passed, failed, passed]}).
     *
     * @return the history map; empty if the file does not exist or can't be read
     */
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
