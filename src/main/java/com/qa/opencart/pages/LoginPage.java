package com.qa.opencart.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.qa.opencart.Utilities.ElementUtil;

/**
 * Page Object for the OpenCart login page. Holds the login locators and exposes
 * the actions a test can perform: read the title/URL, check the "Forgotten
 * Password" link, and log in.
 */
public class LoginPage {
    private Page page;
    private ElementUtil eleUtil;

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
        eleUtil = new ElementUtil(page);
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
        eleUtil.doFill(emailId, Email);
        page.fill(password, Password);
        page.click(loginBtn);

        // OpenCart redirects to the account dashboard (route=account/account) on a
        // successful login, and stays on account/login on failure. The URL is a far
        // more reliable success signal than the Logout link (which lives in a hidden
        // nav dropdown, so waiting for it to be "visible" times out even when logged in).
        try {
            page.waitForURL(java.util.regex.Pattern.compile("account/account"),
                    new Page.WaitForURLOptions().setTimeout(5000));
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
