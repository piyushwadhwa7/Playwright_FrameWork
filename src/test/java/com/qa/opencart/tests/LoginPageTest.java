package com.qa.opencart.tests;

import com.qa.opencart.base.BaseTest;
import com.qa.opencart.constants.AppConstants;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginPageTest extends BaseTest {
    @Test(priority = 1)
    public void navigateToLoginPageTest(){
        loginPage=homePage.navigateToLoginPage();//Page channing method
        String actualTitle =loginPage.loginPageTitle();
        Assert.assertEquals(actualTitle, AppConstants.LOGIN_PAGE_TITLE);
    }
    @Test(priority = 2)
    public void forgotPasswordLinkExistTest(){
        Assert.assertTrue(loginPage.isForgotPasswordPresent());
    }
    @Test(priority = 3)
    public void appLoginTest(){
        loginPage.doLogin(prop.getProperty("username").trim(), prop.getProperty("password").trim());
    }

}
