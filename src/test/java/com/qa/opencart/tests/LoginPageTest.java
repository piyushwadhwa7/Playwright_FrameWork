package com.qa.opencart.tests;

import com.qa.opencart.base.BaseTest;
import com.qa.opencart.constants.AppConstants;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests for the OpenCart login flow. Extends {@link BaseTest}, so the browser and
 * {@code homePage} object are already set up before each test runs.
 */
public class LoginPageTest extends BaseTest {

    /**
     * Navigates from the home page to the login page (page-chaining) and
     * verifies the login page title.
     */
    @Test(priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Piyush")
    @Description("This test is to check if user is able to navigate to the login page section")
    public void navigateToLoginPageTest(){
        loginPage=homePage.navigateToLoginPage();//Page channing method
        String actualTitle =loginPage.loginPageTitle();
        Assert.assertEquals(actualTitle, AppConstants.LOGIN_PAGE_TITLE);
    }

    /** Verifies the "Forgotten Password" link is present on the login page. */
    @Severity(SeverityLevel.NORMAL)
    @Description("This test is used to check if forgotPassword Link exists or not ")
    @Owner("Piyush")
    @Test(priority = 2)
    public void forgotPasswordLinkExistTest(){
        Assert.assertTrue(loginPage.isForgotPasswordPresent());
    }

    /** Logs in using the credentials from {@code config.properties} and asserts login succeeded. */
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Piyush")
    @Description("This test verifies a user can log in with valid credentials")
    @Test(priority = 3)
    public void appLoginTest(){
        boolean isLoggedIn = loginPage.doLogin(prop.getProperty("username").trim(), prop.getProperty("password").trim());
        Assert.assertTrue(isLoggedIn, "Login failed for user: " + prop.getProperty("username"));
    }

}
