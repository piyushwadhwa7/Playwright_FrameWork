package com.qa.opencart.pages;

import com.microsoft.playwright.Page;

public class HomePage {
     private Page page;
    //1. String locators -or

    private String search= "//input[@name='search']";
    private String searcIcon="(//button[@type='button'])[4]";
    private String searchPageHeader= "div#content h1";

    //2. page constructor
    public HomePage(Page page) {
        this.page = page;
    }

    //3.page actions/methods
    public String getHomePageTitle() {
        String pageTitle=page.title();
        System.out.println("Page title is: "+pageTitle);
        return pageTitle;
    }
    public String getHomePageUrl() {
        String pageUrl=page.url();
        System.out.println("Page url is: "+pageUrl);
        return pageUrl;
    }
    public String doSearch(String productName) {
        page.fill(search,productName);
        page.click(searcIcon);
        String header=page.textContent(searchPageHeader);
        System.out.println("Header is: "+header);
        return header;
    }

}
