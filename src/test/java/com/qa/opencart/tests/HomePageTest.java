package com.qa.opencart.tests;
import com.qa.opencart.base.BaseTest;
import com.qa.opencart.constants.AppConstants;
import io.qameta.allure.Description;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class HomePageTest extends BaseTest {
    @Description("This test is used to fetch the page title and verify whether the title is correct or not")
    @Test(priority=1)
    public void homePageTitleTest(){
        String actualTitle=homePage.getHomePageTitle();
        Assert.assertEquals(actualTitle, AppConstants.HOME_PAGE_TITLE);
    }
    @Description("This test is used to fetch the actual URL and verify it will the expected one ")
    @Test(priority=2)
    public void homePageUrlTest(){
        String actualUrl=homePage.getHomePageUrl();
        Assert.assertEquals(actualUrl, prop.getProperty("url"));
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
}
