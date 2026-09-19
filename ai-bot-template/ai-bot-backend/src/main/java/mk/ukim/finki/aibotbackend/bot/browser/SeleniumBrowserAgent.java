package mk.ukim.finki.aibotbackend.bot.browser;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Placeholder so the application boots before the assignment is implemented.
 * TODO(student): Replace this bean with your Playwright/Selenium-backed implementation.
 */
@Component
public class SeleniumBrowserAgent implements BrowserAgent {
    private WebDriver webDriver;
    @Override
    public void start() {
        webDriver = new ChromeDriver();
        webDriver.manage().window().setSize(new Dimension(1280, 720));

    }

    @Override
    public void navigateTo(String url){
        System.out.println("Navigating to: " + url);
        webDriver.get(url);
        System.out.println(webDriver.getCurrentUrl());


    }

    @Override
    public void click(String elementDescription) {
        WebElement element;



        if (elementDescription.startsWith("xpath=")) {
            element = webDriver.findElements(By.xpath(elementDescription.substring(6)))
                    .stream()
                    .filter(WebElement::isDisplayed)
                    .filter(WebElement::isEnabled)
                    .findFirst()
                    .orElseThrow();
        } else {
            List<WebElement> elements = webDriver.findElements(By.cssSelector(elementDescription));

            element = elements.stream()
                    .filter(WebElement::isDisplayed)
                    .filter(WebElement::isEnabled)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("No interactable element found"));
        }

        element.click();
    }

    @Override
    public void type(String elementDescription, String text) {
        List<WebElement> inputs = webDriver.findElements(By.cssSelector("input"));

        for (int i = 0; i < inputs.size(); i++) {
            WebElement input = inputs.get(i);

            System.out.println(
                    i
                            + " | type=" + input.getAttribute("type")
                            + " | id=" + input.getAttribute("id")
                            + " | displayed=" + input.isDisplayed()
                            + " | enabled=" + input.isEnabled()
            );
        }
        WebElement element;

        if (elementDescription.startsWith("xpath=")) {
            element = webDriver.findElements(By.xpath(elementDescription.substring(6)))
                    .stream()
                    .filter(WebElement::isDisplayed)
                    .filter(WebElement::isEnabled)
                    .findFirst()
                    .orElseThrow();
        } else {
            List<WebElement> elements = webDriver.findElements(By.cssSelector(elementDescription));

            element = elements.stream()
                    .filter(WebElement::isDisplayed)
                    .filter(WebElement::isEnabled)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("No interactable element found"));
        }

        element.clear();
        element.sendKeys(text);
    }

    @Override
    public void scrollDown() {
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        js.executeScript("window.ScrollBy(0,720);");
    }

    @Override
    public byte[] takeScreenshot() {
        return ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
    }

    @Override
    public PageSnapshot snapshot() {
        JavascriptExecutor js = (JavascriptExecutor) webDriver;

        String visibleText = (String) js.executeScript(
                "return document.body.innerText;"
        );

        List<WebElement> buttons = webDriver.findElements(
                By.cssSelector("button, a, input")
        );

        String interactables = buttons.stream()
                .map(element -> {
                    try {
                        String tag = element.getTagName();
                        String text = element.getText();
                        String placeholder = element.getAttribute("placeholder");
                        String aria = element.getAttribute("aria-label");

                        return String.format(
                                "%s | text='%s' | placeholder='%s' | aria='%s'",
                                tag, text, placeholder, aria
                        );
                    }catch(StaleElementReferenceException e){
                        return null;
                    }

                })
                .limit(50)
                .collect(Collectors.joining("\n"));

        String domContent = """
        VISIBLE TEXT:
        %s

        INTERACTABLE ELEMENTS:
        %s
        """.formatted(visibleText, interactables);


        return new PageSnapshot(
                webDriver.getCurrentUrl(),
                webDriver.getTitle(),
                domContent,
                ""
        );
    }

    @Override
    public void close() {
       if(webDriver != null){
           webDriver.close();
       }

    }
}
