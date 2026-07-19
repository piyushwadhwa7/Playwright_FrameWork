package com.qa.opencart.tests;
import com.qa.opencart.base.BaseTest;
import com.qa.opencart.constants.AppConstants;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests for the OpenCart home page. Extends {@link BaseTest}, so the browser and
 * {@code homePage} object are already set up before each test runs.
 */
public class HomePageTest extends BaseTest {

    /** Verifies the home page title matches the expected value. */
    @Description("This test is used to fetch the page title and verify whether the title is correct or not")
    @Test(priority=1)
    @Severity(SeverityLevel.NORMAL)
    @Owner("Piyush")
    public void homePageTitleTest(){
        String actualTitle=homePage.getHomePageTitle();
        Assert.assertEquals(actualTitle, AppConstants.HOME_PAGE_TITLE);
    }

    /** Verifies the home page URL matches the configured {@code url}. */
    @Description("This test is used to fetch the actual URL and verify it will the expected one ")
    @Test(priority=2)
    @Severity(SeverityLevel.NORMAL)
    @Owner("Piyush")
    public void homePageUrlTest(){
        String actualUrl=homePage.getHomePageUrl();
        Assert.assertEquals(actualUrl, prop.getProperty("url"));
    }

    /** Supplies the product names used to drive the data-driven search test. */
    @DataProvider
    public Object[][]getProductData(){
        return new Object[][]{
                {"iMac"},
                {"iphone"},
                {"samsung"},
        };
    }

    /**
     * Searches for each product from the data provider and verifies the
     * results-page heading reads "Search - &lt;product&gt;".
     *
     * @param productName the product to search for (injected by the DataProvider)
     */
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Piyush")
    @Description("This test is used to verify if the search is working fine or not")
    @Test(priority=3,dataProvider = "getProductData")
    public void homePageSearchTest(String productName){
        String actualHeader=homePage.doSearch(productName);
        Assert.assertEquals(actualHeader, "Search - "+ productName);
    }
}
