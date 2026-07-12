package com.qa.opencart.base;

import com.microsoft.playwright.Page;
import com.qa.opencart.factory.PlaywrightFactory;
import com.qa.opencart.pages.HomePage;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.util.Properties;

public class BaseTest {
    PlaywrightFactory pf;
    Page page;
    protected HomePage homePage;
    protected Properties prop;
    @BeforeTest
    public void setup(){
        pf= new PlaywrightFactory();
        prop= pf.initProp();
        page=pf.initBrowser(prop);
        homePage= new HomePage(page);
    }

    @AfterTest
    public void tearDown(){
        page.context().close();
    }
}
