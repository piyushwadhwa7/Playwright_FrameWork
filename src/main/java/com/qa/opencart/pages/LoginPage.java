package com.qa.opencart.pages;

import com.microsoft.playwright.Page;

public class LoginPage {
    private Page page;

    //1.String Locators
    private String emailId= "//input[@name='email']";
    private String password= "//input[@name='password']";
    private String loginBtn="//input[@value='Login']";
    private String forgotPassword= "(//a[text()='Forgotten Password'])[1]";
    private String logutbtn="(//a[text()='Logout'])[2]";

    //2.Login page constructor
    public LoginPage(Page page) {
        this.page = page;
    }

    //3. page actions / methods

    //3.page actions/methods
    public String loginPageTitle() {
        String pageTitle=page.title();
        System.out.println("Login Page title is: "+pageTitle);
        return pageTitle;
    }
    public String loginPageUrl() {
        String pageUrl=page.url();
        System.out.println("Login Page url is: "+pageUrl);
        return pageUrl;
    }
    public boolean isForgotPasswordPresent() {
        return page.isVisible(forgotPassword);
    }
    public boolean doLogin(String Email, String Password) {
        System.out.println("APP Creds are :  " + Email +":"+ password);
        page.fill(emailId, Email);
        page.fill(password, Password);
        page.click(loginBtn);
        if (page.isVisible(logutbtn)) {
            System.out.println("user is logged in successfully.......");
            return true;
        }else  {
            System.out.println("user is not logged in successfully.......");
            return false;
        }

    }
}
