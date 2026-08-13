package com.qa.opencart.Utilities;

import com.microsoft.playwright.Page;

public class ElementUtil {

    private Page page;

    public ElementUtil(Page page) {
        this.page = page;
    }

    public void doFill(String selector, String value) {
        page.fill(selector, value);
    }
    public void doClick(String selector) {
        page.click(selector);
    }

    public String doGetText(String selector) {
        return page.textContent(selector);
    }

    public boolean doIsVisible(String selector) {
        return page.isVisible(selector);
    }

}
