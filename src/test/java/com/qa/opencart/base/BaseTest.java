package com.qa.opencart.base;

import com.microsoft.playwright.Page;
import com.qa.opencart.factory.PlaywrightFactory;
import com.qa.opencart.pages.HomePage;
import com.qa.opencart.pages.LoginPage;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.util.Properties;

public class BaseTest {
    PlaywrightFactory pf;
    Page page;
    protected HomePage homePage;// this is homepPage class reference
    protected Properties prop;
    protected LoginPage loginPage;// this is loginpage class referece
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
