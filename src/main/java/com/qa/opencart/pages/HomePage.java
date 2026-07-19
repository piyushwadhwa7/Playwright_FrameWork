package com.qa.opencart.pages;

import com.microsoft.playwright.Page;

/**
 * Page Object for the OpenCart home page. Holds the home page locators and
 * exposes the actions a test can perform on it (read title/URL, search, and
 * jump to the login page).
 */
public class HomePage {
     private Page page;
    //1. String locators -or

    private String search= "//input[@name='search']";
    private String searcIcon="(//button[@type='button'])[4]";
    private String searchPageHeader= "div#content h1";
    private String accountbtn="//a[@title='My Account']";
    private String loginLink="//a[text()='Login']";

    /**
     * @param page the live Playwright page this object drives (injected by the test).
     */
    public HomePage(Page page) {
        this.page = page;
    }

    //3.page actions/methods

    /** @return the browser tab's title text for the home page. */
    public String getHomePageTitle() {
        String pageTitle=page.title();
        System.out.println("Page title is: "+pageTitle);
        return pageTitle;
    }

    /** @return the current URL of the home page. */
    public String getHomePageUrl() {
        String pageUrl=page.url();
        System.out.println("Page url is: "+pageUrl);
        return pageUrl;
    }

    /**
     * Types a product name into the search box, clicks search, and reads the
     * results-page heading.
     *
     * @param productName the product to search for
     * @return the results heading text (e.g. "Search - iphone")
     */
    public String doSearch(String productName) {
        page.fill(search,productName);
        page.click(searcIcon);
        String header=page.textContent(searchPageHeader);
        System.out.println("Header is: "+header);
        return header;
    }

    /**
     * Opens "My Account" and clicks "Login" to move to the login page.
     *
     * @return a {@link LoginPage} for the page now on screen (enables page-chaining).
     */
    public LoginPage navigateToLoginPage() {
        page.click(accountbtn);
        page.click(loginLink);
        return new LoginPage(page);
    }

}
