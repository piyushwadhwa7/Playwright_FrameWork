package com.qa.opencart.tests;

import com.microsoft.playwright.Page;
import com.qa.opencart.factory.PlaywrightFactory;
import com.qa.opencart.pages.HomePage;
import io.qameta.allure.Description;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
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
    @Description("This test is used to fetch the page title and verify wherther the titile is correct or not")
    @Test(priority=1)
    public void homePageTitleTest(){
        String actualTitle=homePage.getHomePageTitle();
        Assert.assertEquals(actualTitle, "Your Store");
    }
    @Description("This test is used to fetch the actual URL and vrify it will the expected one ")
    @Test(priority=2)
    public void homePageUrlTest(){
        String actualUrl=homePage.getHomePageUrl();
        Assert.assertEquals(actualUrl, "https://naveenautomationlabs.com/opencart/");
    }
    @DataProvider
    public Object[][]getProductData(){
        return new Object[][]{
                {"iMac"},
                {"iphone"},
                {"samsung"},
        };
    }
    @Description("This test is used to verify if the search is working fine or not")
    @Test(priority=3,dataProvider = "getProductData")
    public void homePageSearchTest(String productName){
        String actualHeader=homePage.doSearch(productName);
        Assert.assertEquals(actualHeader, "Search - "+ productName);
    }
    @AfterTest
    public void tearDown(){
        page.context().close();
    }
}
