package com.qa.opencart.base;

import com.qa.opencart.factory.PlaywrightFactory;
import org.testng.annotations.BeforeTest;

import java.util.Properties;

/**
 * Base class for API-only tests. It loads test configuration without launching
 * a Playwright browser.
 */
public class BaseApiTest {
    protected Properties prop;

    @BeforeTest
    public void setupApi() {
        PlaywrightFactory pf = new PlaywrightFactory();// Initilizing the playwright server
        prop = pf.initProp();
    }

    protected String getRequiredProperty(String key) {
        String value = prop.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required config property: " + key);
        }
        return value.trim();
    }
}
