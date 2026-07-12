package com.qa.opencart.factory;

import com.microsoft.playwright.*;

public class PlaywrightFactory {
    Playwright playwright;
    Browser browser;
    BrowserContext browserContext;
    Page page;
    public Page initBrowser(String browserName){
        System.out.println("Initializing Playwright Browser "+browserName);
        playwright = Playwright.create();
        switch(browserName.toLowerCase()){
            case "chromium":
                browser=playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
                break;
            case "firefox":
                browser=playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
                break;
            case "safari":
                browser=playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
                break;
            case "chrome":
                browser=playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
                break;
            default:
                // Fail fast: without this, execution falls through to browser.newContext()
                // with browser == null and dies with a NullPointerException instead.
                playwright.close();
                throw new IllegalArgumentException("Invalid Browser Name: " + browserName
                        + " (expected: chromium, firefox, safari, chrome)");
        }

        browserContext=browser.newContext();
        page = browserContext.newPage();
        page.navigate("https://naveenautomationlabs.com/opencart/");
        return page;

    }
}
