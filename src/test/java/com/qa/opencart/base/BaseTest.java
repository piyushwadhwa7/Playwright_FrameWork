package com.qa.opencart.base;

import com.microsoft.playwright.Page;
import com.qa.opencart.factory.PlaywrightFactory;
import com.qa.opencart.pages.HomePage;
import com.qa.opencart.pages.LoginPage;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.util.Properties;

/**
 * Base class every test extends. It owns the browser lifecycle and the shared
 * page objects so the individual test classes only contain assertions, not setup.
 */
public class BaseTest {
    PlaywrightFactory pf;
    Page page;
    protected HomePage homePage;// this is homepPage class reference
    protected Properties prop;
    protected LoginPage loginPage;// this is loginpage class referece

    /**
     * Runs before each {@code <test>} in the TestNG suite: loads the config,
     * launches the browser/page, and creates the HomePage object for tests to use.
     */
    @BeforeTest
    public void setup(){
        pf= new PlaywrightFactory();
        prop= pf.initProp();
        page=pf.initBrowser(prop);
        homePage= new HomePage(page);
    }

    /**
     * Runs after each {@code <test>}: closes the browser context so the session
     * (cookies, storage) is torn down and resources are released.
     */
    @AfterTest
    public void tearDown(){
        page.context().close();
    }
}
