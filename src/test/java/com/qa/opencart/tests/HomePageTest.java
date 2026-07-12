package com.qa.opencart.tests;

import com.microsoft.playwright.Page;
import com.qa.opencart.factory.PlaywrightFactory;
import com.qa.opencart.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class HomePageTest {
    PlaywrightFactory pf;
    Page page;
    HomePage homePage;
    @BeforeTest
    public void setup(){
        pf= new PlaywrightFactory();
        page=pf.initBrowser("chromium");
        homePage= new HomePage(page);
    }
    @Test(priority=1)
    public void homePageTitleTest(){
        String actualTitle=homePage.getHomePageTitle();
        Assert.assertEquals(actualTitle, "Your Store");
    }
    @Test(priority=2)
    public void homePageUrlTest(){
        String actualUrl=homePage.getHomePageUrl();
        Assert.assertEquals(actualUrl, "https://naveenautomationlabs.com/opencart/");
    }
    @Test(priority=3)
    public void homePageSearchTest(){
        String actualHeader=homePage.doSearch("iphone");
        Assert.assertEquals(actualHeader, "Search - iphone");
    }
    @AfterTest
    public void tearDown(){
        page.context().close();
    }
}
