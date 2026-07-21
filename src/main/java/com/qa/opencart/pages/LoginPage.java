package com.qa.opencart.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;

/**
 * Page Object for the OpenCart login page. Holds the login locators and exposes
 * the actions a test can perform: read the title/URL, check the "Forgotten
 * Password" link, and log in.
 */
public class LoginPage {
    private Page page;

    //1.String Locators
    private String emailId= "//input[@name='email']";
    private String password= "//input[@name='password']";
    private String loginBtn="//input[@value='Login']";
    private String forgotPassword= "(//a[text()='Forgotten Password'])[1]";
    private String logoutBtn="//a[text()='Logout']";
    private String loginWarning=".alert-danger";

    /**
     * @param page the live Playwright page this object drives (injected by the test).
     */
    public LoginPage(Page page) {
        this.page = page;
    }

    //3. page actions / methods

    /** @return the browser tab's title text for the login page. */
    public String loginPageTitle() {
        String pageTitle=page.title();
        System.out.println("Login Page title is: "+pageTitle);
        return pageTitle;
    }

    /** @return the current URL of the login page. */
    public String loginPageUrl() {
        String pageUrl=page.url();
        System.out.println("Login Page url is: "+pageUrl);
        return pageUrl;
    }

    /** @return true if the "Forgotten Password" link is visible on the login page. */
    public boolean isForgotPasswordPresent() {
        return page.isVisible(forgotPassword);
    }

    /**
     * Enters the credentials, clicks Login, and checks whether login succeeded
     * (by looking for the Logout link).
     *
     * @param Email    the account email
     * @param Password the account password
     * @return true if login succeeded (Logout link visible), false otherwise
     */
    public boolean doLogin(String Email, String Password) {
        System.out.println("Trying to log in with username/email: " + Email);
        System.out.println("Trying to log in with password: " + Password);
        page.fill(emailId, Email);
        page.fill(password, Password);
        page.click(loginBtn);

        Locator logoutLink = page.locator(logoutBtn).first();
        try {
            logoutLink.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            System.out.println("user is logged in successfully.......");
            return true;
        } catch (PlaywrightException e) {
            Locator warning = page.locator(loginWarning).first();
            if (warning.isVisible()) {
                System.out.println("Login warning: " + warning.innerText().trim());
            }
            System.out.println("Current URL after login attempt: " + page.url());
        }

        System.out.println("user is not logged in successfully.......");
        return false;
    }
}
