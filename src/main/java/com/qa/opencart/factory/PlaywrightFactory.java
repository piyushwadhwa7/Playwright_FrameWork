package com.qa.opencart.factory;

import com.microsoft.playwright.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Factory that boots Playwright and hands back a ready-to-use {@link Page}.
 * <p>
 * The Playwright/Browser/Context/Page objects are kept in {@link ThreadLocal}
 * fields so that when tests run in parallel, each test thread gets its own
 * isolated browser stack and one thread can never overwrite another's page.
 * The static getters let non-test classes (e.g. the Allure listener) reach the
 * current thread's objects without a shared/global reference.
 */
public class PlaywrightFactory {
    Playwright playwright;
    Browser browser;
    BrowserContext browserContext;
    Page page;
    Properties prop;

    // Per-thread Playwright stack. Each test thread stores its own instances here,
    // so parallel tests stay isolated and listeners can fetch the right page.
    private static final ThreadLocal<Page> tlPage = new ThreadLocal<>();
    private static final ThreadLocal<Browser> tlBrowser = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> tlBrowserContext = new ThreadLocal<>();
    private static final ThreadLocal<Playwright> tlPlaywright = new ThreadLocal<>();

    /** @return the {@link Page} belonging to the current thread (null if not yet initialised). */
    public static Page getPage() {
        return tlPage.get();
    }

    /** @return the {@link Playwright} engine belonging to the current thread. */
    public static Playwright getPlaywright() {
        return tlPlaywright.get();
    }

    /** @return the {@link Browser} belonging to the current thread. */
    public static Browser getBrowser() {
        return tlBrowser.get();
    }

    /** @return the {@link BrowserContext} (isolated session) belonging to the current thread. */
    public static BrowserContext getBrowserContext() {
        return tlBrowserContext.get();
    }

    /**
     * Launches the browser named in the config, opens a fresh context + page,
     * navigates to the configured URL, and stores each object in its ThreadLocal.
     *
     * @param prop loaded config; must contain {@code browser} and {@code url}
     * @return the newly opened {@link Page}, already navigated to the app
     * @throws IllegalArgumentException if the browser name is not recognised
     */
    public Page initBrowser(Properties prop){
        String browserName=prop.getProperty("browser").trim();
        System.out.println("Initializing Playwright Browser "+browserName);
        // Headed by default for local runs; CI passes -Dheadless=true (no display on runners)
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        // When running through a Selenium Grid (SELENIUM_REMOTE_URL set), Chrome runs
        // inside a container and needs these flags to avoid renderer crashes.
        boolean onGrid = System.getenv("SELENIUM_REMOTE_URL") != null;
        java.util.List<String> chromiumArgs = onGrid
                ? java.util.List.of("--no-sandbox", "--disable-dev-shm-usage")
                : java.util.List.of();
        // Optional slow-motion: pass -Dslowmo=800 to pause 800ms between actions so
        // the run is watchable (e.g. in the Selenium noVNC viewer). Default 0 = full speed.
        double slowMo = Double.parseDouble(System.getProperty("slowmo", "0"));
        tlPlaywright.set(Playwright.create());
        switch(browserName.toLowerCase()){
            case "chromium":
                tlBrowser.set(getPlaywright().chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless).setArgs(chromiumArgs).setSlowMo(slowMo)));
                break;
            case "firefox":
                tlBrowser.set(getPlaywright().firefox().launch(new BrowserType.LaunchOptions().setHeadless(headless).setSlowMo(slowMo)));
                break;
            case "safari":
                tlBrowser.set(getPlaywright().webkit().launch(new BrowserType.LaunchOptions().setHeadless(headless).setSlowMo(slowMo)));

                break;
            case "chrome":
                tlBrowser.set(getPlaywright().chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(headless).setArgs(chromiumArgs).setSlowMo(slowMo)));

                break;
            default:
                // Fail fast: without this, execution falls through to newContext()
                // with browser == null and dies with a NullPointerException instead.
                getPlaywright().close();
                throw new IllegalArgumentException("Invalid Browser Name: " + browserName
                        + " (expected: chromium, firefox, safari, chrome)");
        }

        tlBrowserContext.set(getBrowser().newContext());
        tlPage.set(getBrowserContext().newPage());
        getPage().navigate(prop.getProperty("url").trim());
        return getPage();

    }


    /**
     * Loads {@code config.properties} from the test resources folder into a
     * {@link Properties} object (browser, url, credentials, env, etc.).
     *
     * @return the loaded properties
     * @throws RuntimeException if the file is missing or cannot be read
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
