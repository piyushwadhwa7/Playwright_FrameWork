package com.qa.opencart.listners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retry implements IRetryAnalyzer {

    private int count = 0;
    private static final int maxTry = 3;

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
