package com.qa.opencart.factory;

import com.microsoft.playwright.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PlaywrightFactory {
    Playwright playwright;
    Browser browser;
    BrowserContext browserContext;
    Page page;
    Properties prop;
    public Page initBrowser(Properties prop){
        String browserName=prop.getProperty("browser").trim();
        System.out.println("Initializing Playwright Browser "+browserName);
        // Headed by default for local runs; CI passes -Dheadless=true (no display on runners)
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        playwright = Playwright.create();
        switch(browserName.toLowerCase()){
            case "chromium":
                browser=playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
                break;
            case "firefox":
                browser=playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(headless));
                break;
            case "safari":
                browser=playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(headless));
                break;
            case "chrome":
                browser=playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(headless));
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
        page.navigate(prop.getProperty("url").trim());
        return page;

    }


    /**
     * This method is used for initalize te properties from config file
     *
     * @return
     */
    public Properties initProp() {
        try {
            FileInputStream ip= new FileInputStream("./src/test/resources/config/config.properties");
            prop = new Properties();
            try {
                prop.load(ip);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return prop;
    }
}
