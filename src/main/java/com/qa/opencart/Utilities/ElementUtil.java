package com.qa.opencart.Utilities;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Common Playwright element and page helpers used by page objects.
 *
 * <p>Playwright locators auto-wait for actionability. The timeout overloads in
 * this class are therefore implemented with Playwright option objects rather
 * than Selenium-style explicit polling.</p>
 */
public class ElementUtil {

    private final Page page;

    /**
     * Creates a utility bound to a live Playwright page.
     *
     * @param page page instance used to create locators and perform page-level actions
     * @throws NullPointerException when {@code page} is null
     */
    public ElementUtil(Page page) {
        this.page = Objects.requireNonNull(page, "page must not be null");
    }

    /** Validates required string inputs before they reach the Playwright API. */
    private void nullCheck(String value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
    }

    /** Validates the utility's timeout values, which are supplied in seconds. */
    private void validateTimeout(int timeOut) {
        if (timeOut < 0) {
            throw new IllegalArgumentException("timeout must be zero or greater");
        }
    }

    /** Converts the public seconds-based timeout contract to Playwright milliseconds. */
    private double timeoutInMillis(int timeOut) {
        validateTimeout(timeOut);
        return timeOut * 1000.0;
    }

    /** Creates a lazy locator from the supplied Playwright selector. */
    private Locator locator(String selector) {
        nullCheck(selector, "selector");
        return page.locator(selector);
    }

    /** Returns a Playwright locator. Locator resolution remains lazy. */
    public Locator getElement(String selector) {
        return locator(selector);
    }

    // ********************** Element actions **********************//

    /**
     * Fills an input using a CSS, XPath, or other Playwright-supported selector.
     * Playwright clears the existing value and auto-waits until the element is actionable.
     *
     * @param selector locator selector for the input element
     * @param value value to enter
     */
    public void doFill(String selector, String value) {
        nullCheck(value, "value");
        locator(selector).fill(value);
    }

    /**
     * Fills an input with an action-specific timeout in seconds.
     *
     * @param selector locator selector for the input element
     * @param value value to enter
     * @param timeOut maximum action wait in seconds
     */
    public void doFill(String selector, String value, int timeOut) {
        nullCheck(value, "value");
        locator(selector).fill(value,
                new Locator.FillOptions().setTimeout(timeoutInMillis(timeOut)));
    }

    /**
     * Playwright equivalent of Selenium {@code sendKeys}; fills after clearing the current value.
     *
     * @param selector locator selector for the input element
     * @param value value to enter
     */
    public void doSendKeys(String selector, String value) {
        doFill(selector, value);
    }

    /**
     * Fills an input using a caller-provided action timeout.
     *
     * @param selector locator selector for the input element
     * @param value value to enter
     * @param timeOut maximum action wait in seconds
     */
    public void doSendKeys(String selector, String value, int timeOut) {
        doFill(selector, value, timeOut);
    }

    /**
     * Sends one or more character sequences after clearing the current value.
     * The values are joined and typed through Playwright's sequential keyboard API.
     *
     * @param selector locator selector for the input element
     * @param value character sequences to type
     */
    public void doSendKeys(String selector, CharSequence... value) {
        Locator element = locator(selector);
        element.clear();
        element.pressSequentially(join(value));
    }

    /** Joins Selenium-style character arguments into one Playwright text value. */
    private String join(CharSequence... values) {
        if (values == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        StringBuilder result = new StringBuilder();
        for (CharSequence value : values) {
            if (value != null) {
                result.append(value);
            }
        }
        return result.toString();
    }

    /**
     * Clicks an element after Playwright verifies it is actionable.
     *
     * @param selector locator selector for the clickable element
     */
    public void doClick(String selector) {
        locator(selector).click();
    }

    /**
     * Clicks an element with an action-specific timeout in seconds.
     *
     * @param selector locator selector for the clickable element
     * @param timeOut maximum action wait in seconds
     */
    public void doClick(String selector, int timeOut) {
        locator(selector).click(new Locator.ClickOptions()
                .setTimeout(timeoutInMillis(timeOut)));
    }

    /**
     * Returns the visible, rendered text of the first matching element.
     *
     * @param selector locator selector for the element
     * @return rendered inner text
     */
    public String doGetText(String selector) {
        return locator(selector).innerText();
    }

    /**
     * Returns the raw text content of the first matching element, including text
     * that may not be visible to the user.
     *
     * @param selector locator selector for the element
     * @return raw text content, or null when the element has no text content
     */
    public String doGetTextContent(String selector) {
        return locator(selector).textContent();
    }

    /**
     * Reads an attribute from the first matching element.
     *
     * @param selector locator selector for the element
     * @param attrName attribute name to read
     * @return attribute value, or null when the attribute is absent
     */
    public String doGetAttribute(String selector, String attrName) {
        nullCheck(attrName, "attrName");
        return locator(selector).getAttribute(attrName);
    }

    /**
     * Checks whether the first matching element is visible according to Playwright.
     *
     * @param selector locator selector for the element
     * @return true when the element is visible; otherwise false
     */
    public boolean doIsDisplayed(String selector) {
        return locator(selector).isVisible();
    }

    /**
     * Alias for {@link #doIsDisplayed(String)} using Playwright terminology.
     *
     * @param selector locator selector for the element
     * @return true when the element is visible; otherwise false
     */
    public boolean doIsVisible(String selector) {
        return doIsDisplayed(selector);
    }

    /**
     * Verifies that exactly one element matches the selector and that it is visible.
     *
     * @param selector locator selector for the element
     * @return true only for one visible match
     */
    public boolean isElementDisplayed(String selector) {
        Locator element = locator(selector);
        return element.count() == 1 && element.isVisible();
    }

    /**
     * Verifies the expected match count and visibility of the first match when present.
     *
     * @param selector locator selector for the elements
     * @param expectedElementCount expected number of matching elements
     * @return true when the count matches and the result is visible when non-empty
     */
    public boolean isElementDisplayed(String selector, int expectedElementCount) {
        if (expectedElementCount < 0) {
            throw new IllegalArgumentException("expectedElementCount must not be negative");
        }
        Locator element = locator(selector);
        return element.count() == expectedElementCount
                && (expectedElementCount == 0 || element.first().isVisible());
    }

    /**
     * Returns a snapshot list of locators for all current selector matches.
     * The returned objects remain Playwright locators and are not Selenium elements.
     *
     * @param selector locator selector for the elements
     * @return list of matching locators
     */
    public List<Locator> getElements(String selector) {
        return locator(selector).all();
    }

    /**
     * Counts the current matches without resolving them into element handles.
     *
     * @param selector locator selector for the elements
     * @return number of matching elements
     */
    public int getElementsCount(String selector) {
        return locator(selector).count();
    }

    /**
     * Collects non-empty rendered text from all matching elements.
     *
     * @param selector locator selector for the elements
     * @return list of non-empty inner-text values in DOM order
     */
    public List<String> getElementsTextList(String selector) {
        List<String> textList = new ArrayList<>();
        for (Locator element : getElements(selector)) {
            String text = element.innerText();
            if (text != null && !text.isEmpty()) {
                textList.add(text);
            }
        }
        return textList;
    }

    /**
     * Collects non-empty values of an attribute from all matching elements.
     *
     * @param selector locator selector for the elements
     * @param attrName attribute name to read
     * @return list of non-empty attribute values in DOM order
     */
    public List<String> getElementAttributeList(String selector, String attrName) {
        nullCheck(attrName, "attrName");
        List<String> attributeList = new ArrayList<>();
        for (Locator element : getElements(selector)) {
            String value = element.getAttribute(attrName);
            if (value != null && !value.isEmpty()) {
                attributeList.add(value);
            }
        }
        return attributeList;
    }

    // ********************** Select/dropdown helpers **********************//

    /**
     * Selects an option in a native {@code <select>} by its zero-based index.
     *
     * @param selector selector for the {@code <select>} element
     * @param index zero-based option index
     */
    public void doSelectByIndex(String selector, int index) {
        locator(selector).selectOption(new SelectOption().setIndex(index));
    }

    /**
     * Selects an option in a native {@code <select>} by its visible label.
     * The misspelled method name is retained for compatibility with the Selenium utility.
     *
     * @param selector selector for the {@code <select>} element
     * @param visibleText exact visible option label
     */
    public void doSelectByVisbleText(String selector, String visibleText) {
        nullCheck(visibleText, "visibleText");
        locator(selector).selectOption(new SelectOption().setLabel(visibleText));
    }

    /**
     * Correctly-spelled alias for {@link #doSelectByVisbleText(String, String)}.
     *
     * @param selector selector for the {@code <select>} element
     * @param visibleText exact visible option label
     */
    public void doSelectByVisibleText(String selector, String visibleText) {
        doSelectByVisbleText(selector, visibleText);
    }

    /**
     * Selects an option in a native {@code <select>} by its HTML value attribute.
     *
     * @param selector selector for the {@code <select>} element
     * @param value option value attribute
     */
    public void doSelectByValue(String selector, String value) {
        nullCheck(value, "value");
        locator(selector).selectOption(value);
    }

    /**
     * Counts the {@code <option>} elements beneath a native dropdown.
     *
     * @param selector selector for the {@code <select>} element
     * @return number of options
     */
    public int getDropDownOptionsCount(String selector) {
        return locator(selector).locator("option").count();
    }

    /**
     * Returns the visible labels of all options in a native dropdown.
     *
     * @param selector selector for the {@code <select>} element
     * @return option labels in DOM order
     */
    public List<String> getDropDownOptionsTextList(String selector) {
        return locator(selector).locator("option").allInnerTexts();
    }

    /**
     * Selects a native dropdown option by trimmed visible text.
     *
     * @param selector selector for the {@code <select>} element
     * @param optionText visible label to select
     */
    public void selectValueFromDropDown(String selector, String optionText) {
        nullCheck(optionText, "optionText");
        doSelectByVisibleText(selector, optionText.trim());
    }

    /**
     * Clicks an exact text match in a custom, non-native dropdown.
     * This method is for listbox/menu implementations that are not backed by
     * a native {@code <select>} element.
     *
     * @param optionSelector selector matching the custom option elements
     * @param optionText exact trimmed option text to click
     * @throws PlaywrightException when no matching option is found
     */
    public void selectValueFromDropDownWithoutSelectClass(String optionSelector,
                                                          String optionText) {
        nullCheck(optionText, "optionText");
        for (Locator option : getElements(optionSelector)) {
            if (optionText.equals(option.innerText().trim())) {
                option.click();
                return;
            }
        }
        throw new PlaywrightException("Dropdown option not found: " + optionText);
    }

    /**
     * Fills a search field, scans the suggestion locators, and clicks the first
     * suggestion containing the requested value.
     *
     * @param searchField selector for the search input
     * @param searchKey value to enter in the search input
     * @param suggestions selector matching suggestion elements
     * @param value partial text expected in the selected suggestion
     * @throws PlaywrightException when no matching suggestion is found
     */
    public void doSearch(String searchField, String searchKey,
                         String suggestions, String value) {
        doFill(searchField, searchKey);
        nullCheck(value, "value");
        for (Locator suggestion : getElements(suggestions)) {
            if (suggestion.innerText().contains(value)) {
                suggestion.click();
                return;
            }
        }
        throw new PlaywrightException("Search suggestion not found: " + value);
    }

    // ********************** Mouse and keyboard helpers **********************//

    /**
     * Hovers over a parent menu item and clicks its child item.
     * Playwright waits for the hover and click actions to become actionable.
     *
     * @param parentSelector selector for the parent menu item
     * @param childSelector selector for the child menu item
     */
    public void handleParentSubMenu(String parentSelector, String childSelector) {
        locator(parentSelector).hover();
        doClick(childSelector);
    }

    /**
     * Drags one locator-selected element onto another using Playwright's page API.
     *
     * @param sourceSelector selector for the draggable source
     * @param targetSelector selector for the drop target
     */
    public void doDragAndDrop(String sourceSelector, String targetSelector) {
        page.dragAndDrop(sourceSelector, targetSelector);
    }

    /**
     * Types text through Playwright's sequential keyboard API without clearing first.
     *
     * @param selector selector for the focused/input element
     * @param value text to type
     */
    public void doActionsSendKeys(String selector, String value) {
        nullCheck(value, "value");
        locator(selector).pressSequentially(value);
    }

    /**
     * Performs a standard Playwright click through the actions-helper naming convention.
     *
     * @param selector selector for the clickable element
     */
    public void doActionsClick(String selector) {
        doClick(selector);
    }

    /**
     * Types text sequentially with a delay between characters.
     * Playwright's delay is expressed in milliseconds.
     *
     * @param selector selector for the input element
     * @param value text to type
     * @param pauseTime delay between characters in milliseconds
     */
    public void doActionsSendKeysWithPause(String selector, String value, long pauseTime) {
        nullCheck(value, "value");
        if (pauseTime < 0) {
            throw new IllegalArgumentException("pauseTime must not be negative");
        }
        locator(selector).pressSequentially(value,
                new Locator.PressSequentiallyOptions().setDelay(pauseTime));
    }

    /**
     * Types text sequentially with Playwright's default utility delay of 500 ms.
     *
     * @param selector selector for the input element
     * @param value text to type
     */
    public void doActionsSendKeysWithPause(String selector, String value) {
        doActionsSendKeysWithPause(selector, value, 500);
    }

    /**
     * Opens a multi-level menu, hovers through exact-text submenu items, and clicks
     * the fourth-level item.
     *
     * @param level1 selector for the first-level menu trigger
     * @param level2 exact text of the second-level item
     * @param level3 exact text of the third-level item
     * @param level4 exact text of the fourth-level item
     */
    public void level4MenuSubMenuHandlingUsingClick(String level1,
                                                     String level2,
                                                     String level3,
                                                     String level4) {
        doClick(level1);
        getByExactText(level2).hover();
        getByExactText(level3).hover();
        getByExactText(level4).click();
    }

    /**
     * Navigates a multi-level menu by hovering each selector and clicking the final item.
     * This variant expects selectors for all levels.
     *
     * @param level1 selector for the first-level menu item
     * @param level2 selector for the second-level menu item
     * @param level3 selector for the third-level menu item
     * @param level4 selector for the final menu item
     */
    public void level4MenuSubMenuHandlingUsingMouseHover(String level1,
                                                          String level2,
                                                          String level3,
                                                          String level4) {
        locator(level1).hover();
        locator(level2).hover();
        locator(level3).hover();
        doClick(level4);
    }

    /** Creates a Playwright text locator configured for an exact text match. */
    private Locator getByExactText(String text) {
        nullCheck(text, "text");
        return page.getByText(text, new Page.GetByTextOptions().setExact(true));
    }

    // ********************** Wait helpers **********************//

    /**
     * Waits until at least one matching element is attached to the DOM.
     * Playwright performs the polling internally and returns locator snapshots
     * for the matches present when the wait completes.
     *
     * @param selector selector for the expected elements
     * @param timeOut maximum wait in seconds
     * @return matching locators after attachment
     */
    public List<Locator> waitForElementsPresence(String selector, int timeOut) {
        Locator element = locator(selector);
        element.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.ATTACHED)
                .setTimeout(timeoutInMillis(timeOut)));
        return element.all();
    }

    /**
     * Waits until at least one matching element is visible.
     *
     * @param selector selector for the expected elements
     * @param timeOut maximum wait in seconds
     * @return visible element locators after the wait completes
     */
    public List<Locator> waitForVisibilityOfElementsLocated(String selector, int timeOut) {
        Locator element = locator(selector);
        element.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeoutInMillis(timeOut)));
        return element.all();
    }

    /**
     * Compatibility alias preserving the original Selenium utility's misspelled method name.
     *
     * @param selector selector for the expected elements
     * @param timeOut maximum wait in seconds
     * @return visible element locators after the wait completes
     */
    public List<Locator> waitForVisiblityOfElementsLocated(String selector, int timeOut) {
        return waitForVisibilityOfElementsLocated(selector, timeOut);
    }

    /**
     * Waits for one locator to become visible and returns that locator for chaining.
     *
     * @param selector selector for the expected element
     * @param timeOut maximum wait in seconds
     * @return the Playwright locator after it becomes visible
     */
    public Locator waitForElementVisible(String selector, int timeOut) {
        Locator element = locator(selector);
        element.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeoutInMillis(timeOut)));
        return element;
    }

    /**
     * Visible-element wait overload retained for API parity with the Selenium utility.
     * Playwright controls polling internally, so {@code intervalTime} is validated
     * but does not override Playwright's polling strategy.
     *
     * @param selector selector for the expected element
     * @param timeOut maximum wait in seconds
     * @param intervalTime legacy polling interval in seconds
     * @return the Playwright locator after it becomes visible
     */
    public Locator waitForElementVisible(String selector, int timeOut, int intervalTime) {
        validateTimeout(intervalTime);
        return waitForElementVisible(selector, timeOut);
    }

    /**
     * Clicks a locator with an explicit actionability timeout.
     * Playwright automatically checks visibility, stability, enabled state, and
     * receives-events conditions before clicking.
     *
     * @param selector selector for the clickable element
     * @param timeOut maximum action wait in seconds
     */
    public void clickWhenReady(String selector, int timeOut) {
        locator(selector).click(new Locator.ClickOptions()
                .setTimeout(timeoutInMillis(timeOut)));
    }

    /**
     * Waits until the document title contains the supplied text.
     *
     * @param titleFraction title text that must be present
     * @param timeOut maximum wait in seconds
     * @return current page title after the condition succeeds
     */
    public String waitForTitleContains(String titleFraction, int timeOut) {
        nullCheck(titleFraction, "titleFraction");
        page.waitForFunction("expected => document.title.includes(expected)", titleFraction,
                new Page.WaitForFunctionOptions().setTimeout(timeoutInMillis(timeOut)));
        return page.title();
    }

    /**
     * Waits until the document title exactly matches the supplied value.
     *
     * @param titleVal expected complete title
     * @param timeOut maximum wait in seconds
     * @return current page title after the condition succeeds
     */
    public String waitForTitleToBe(String titleVal, int timeOut) {
        nullCheck(titleVal, "titleVal");
        page.waitForFunction("expected => document.title === expected", titleVal,
                new Page.WaitForFunctionOptions().setTimeout(timeoutInMillis(timeOut)));
        return page.title();
    }

    /**
     * Waits until the current page URL contains the supplied fragment.
     *
     * @param urlFraction URL text that must be present
     * @param timeOut maximum wait in seconds
     * @return current page URL after the condition succeeds
     */
    public String waitForURLContains(String urlFraction, int timeOut) {
        nullCheck(urlFraction, "urlFraction");
        page.waitForURL(url -> url.contains(urlFraction),
                new Page.WaitForURLOptions().setTimeout(timeoutInMillis(timeOut)));
        return page.url();
    }

    /**
     * Waits until the current page URL exactly matches the supplied URL.
     *
     * @param urlValue expected complete URL
     * @param timeOut maximum wait in seconds
     * @return current page URL after the condition succeeds
     */
    public String waitForURLToBe(String urlValue, int timeOut) {
        nullCheck(urlValue, "urlValue");
        page.waitForURL(urlValue,
                new Page.WaitForURLOptions().setTimeout(timeoutInMillis(timeOut)));
        return page.url();
    }

    // ********************** Dialog helpers **********************//

    /*
     * A Playwright dialog must be handled while the action that opens it is
     * running. Therefore the Selenium-style "wait, then return Alert" pattern
     * is not valid here. These helpers install a one-shot handler before the
     * triggering action and are the safe Playwright equivalent.
     */

    /**
     * Runs an action, captures the JavaScript dialog message, and accepts the dialog.
     * The handler is registered before the action because Playwright dialogs must
     * be handled while the triggering action is in progress.
     *
     * @param action action that opens the alert, confirm, or prompt
     * @return dialog message
     * @throws PlaywrightException when the action does not open a dialog
     */
    public String getAlertText(Runnable action) {
        AtomicReference<String> text = new AtomicReference<>();
        page.onceDialog(dialog -> {
            text.set(dialog.message());
            dialog.accept();
        });
        action.run();
        if (text.get() == null) {
            throw new PlaywrightException("No JavaScript dialog was opened by the action");
        }
        return text.get();
    }

    /**
     * Runs an action and accepts the JavaScript dialog opened by that action.
     *
     * @param action action that opens the dialog
     * @throws PlaywrightException when the action does not open a dialog
     */
    public void acceptAlert(Runnable action) {
        handleDialog(action, Dialog::accept);
    }

    /**
     * Runs an action and dismisses the JavaScript dialog opened by that action.
     *
     * @param action action that opens the dialog
     * @throws PlaywrightException when the action does not open a dialog
     */
    public void dismissAlert(Runnable action) {
        handleDialog(action, Dialog::dismiss);
    }

    /**
     * Runs an action, enters prompt text, and accepts the JavaScript prompt.
     *
     * @param action action that opens the prompt
     * @param value text supplied to the prompt
     * @throws PlaywrightException when the action does not open a dialog
     */
    public void alertSendKeys(Runnable action, String value) {
        nullCheck(value, "value");
        handleDialog(action, dialog -> dialog.accept(value));
    }

    /** Registers a one-shot dialog handler and runs the action that triggers it. */
    private void handleDialog(Runnable action, java.util.function.Consumer<Dialog> handler) {
        Objects.requireNonNull(action, "action must not be null");
        AtomicReference<Boolean> handled = new AtomicReference<>(false);
        page.onceDialog(dialog -> {
            handler.accept(dialog);
            handled.set(true);
        });
        action.run();
        if (!handled.get()) {
            throw new PlaywrightException("No JavaScript dialog was opened by the action");
        }
    }

    // ********************** Frame helpers **********************//

    /**
     * Waits for an iframe element to attach and returns a lazy {@link FrameLocator}.
     * Playwright uses frame locators instead of switching the driver context.
     *
     * @param frameSelector selector for the iframe element
     * @param timeOut maximum wait in seconds
     * @return frame locator scoped to the iframe
     */
    public FrameLocator waitForFrameByLocator(String frameSelector, int timeOut) {
        locator(frameSelector).waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.ATTACHED)
                .setTimeout(timeoutInMillis(timeOut)));
        return page.frameLocator(frameSelector);
    }

    /**
     * Frame-locator wait overload retained for Selenium API parity.
     * Playwright manages polling internally; {@code intervalTime} is validated
     * but does not change Playwright's polling strategy.
     *
     * @param frameSelector selector for the iframe element
     * @param timeOut maximum wait in seconds
     * @param intervalTime legacy polling interval in seconds
     * @return frame locator scoped to the iframe
     */
    public FrameLocator waitForFrameByLocator(String frameSelector, int timeOut, int intervalTime) {
        validateTimeout(intervalTime);
        return waitForFrameByLocator(frameSelector, timeOut);
    }

    /**
     * Creates a lazy frame locator without waiting for the iframe immediately.
     * Use {@link #waitForFrameByLocator(String, int)} when attachment must be verified first.
     *
     * @param frameSelector selector for the iframe element
     * @return frame locator scoped to the iframe
     */
    public FrameLocator getFrameByLocator(String frameSelector) {
        return page.frameLocator(frameSelector);
    }

    /**
     * Waits for a frame at the requested Playwright frame-list index.
     * The main frame is included in {@link Page#frames()} at index zero.
     *
     * @param frameIndex zero-based frame index
     * @param timeOut maximum wait in seconds
     * @return matching Playwright frame
     * @throws PlaywrightException when the index is unavailable before timeout
     */
    public Frame waitForFrameByIndex(int frameIndex, int timeOut) {
        validateTimeout(timeOut);
        long deadline = System.nanoTime() + (long) (timeoutInMillis(timeOut) * 1_000_000);
        while (System.nanoTime() <= deadline) {
            List<Frame> frames = page.frames();
            if (frameIndex >= 0 && frameIndex < frames.size()) {
                return frames.get(frameIndex);
            }
            page.waitForTimeout(100);
        }
        throw new PlaywrightException("Frame index not found: " + frameIndex);
    }

    /**
     * Waits for a frame identified by its Playwright frame name.
     *
     * @param frameIDOrName frame name used by Playwright's page lookup
     * @param timeOut maximum wait in seconds
     * @return matching Playwright frame
     * @throws PlaywrightException when the frame is unavailable before timeout
     */
    public Frame waitForFrameByIndex(String frameIDOrName, int timeOut) {
        nullCheck(frameIDOrName, "frameIDOrName");
        validateTimeout(timeOut);
        long deadline = System.nanoTime() + (long) (timeoutInMillis(timeOut) * 1_000_000);
        while (System.nanoTime() <= deadline) {
            Frame frame = page.frame(frameIDOrName);
            if (frame != null) {
                return frame;
            }
            page.waitForTimeout(100);
        }
        throw new PlaywrightException("Frame not found: " + frameIDOrName);
    }

    /**
     * Waits until the current browser context contains the expected number of pages.
     * In Playwright, browser tabs/windows are represented by {@code Page} objects
     * in the same {@code BrowserContext}.
     *
     * @param totalWindows expected number of pages in the browser context
     * @param timeOut maximum wait in seconds
     * @return true when the count is reached before timeout; otherwise false
     */
    public boolean waitForWindowsToBe(int totalWindows, int timeOut) {
        if (totalWindows < 0) {
            throw new IllegalArgumentException("totalWindows must not be negative");
        }
        validateTimeout(timeOut);
        long deadline = System.nanoTime() + (long) (timeoutInMillis(timeOut) * 1_000_000);
        while (System.nanoTime() <= deadline) {
            if (page.context().pages().size() == totalWindows) {
                return true;
            }
            page.waitForTimeout(100);
        }
        return page.context().pages().size() == totalWindows;
    }

    // ********************** Page helpers **********************//

    /**
     * Waits for the current page to reach Playwright's {@link LoadState#LOAD} state.
     *
     * @param timeOut maximum wait in seconds
     */
    public void isPageLoaded(int timeOut) {
        page.waitForLoadState(LoadState.LOAD,
                new Page.WaitForLoadStateOptions().setTimeout(timeoutInMillis(timeOut)));
    }

    /**
     * Scrolls the document viewport to the bottom using page-level JavaScript evaluation.
     *
     * @implNote This is a page operation rather than an element action because
     * the target is the document's scroll container.
     */
    public void scrollToBottom() {
        page.evaluate("window.scrollTo(0, document.documentElement.scrollHeight)");
    }
}
