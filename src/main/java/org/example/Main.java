package org.example;

import com.itextpdf.html2pdf.HtmlConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class Main {
    public static String dest ="src/main/resources/hello.pdf";
    public static final String HTML = "<h1>Test</h1><p>Hello World</p>";

    public static final String src="src/main/resources/hello-template.html";

    public static void main(String[] args) throws IOException {

        String html =  new String(Files.readAllBytes(Paths.get(src)));
        // We start by setting up our browser driver. The driver version you need here might differ depending on your Selenium version.
        // It is also possible to use a different 3rd party library to automatically download the necessary driver.
        // This way you don't have to bother with setting the path to the executable. An example of such a library is https://github.com/bonigarcia/webdrivermanager.
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        // Spin up the browser in "headless" mode, this way Selenium will not open up a graphical user interface.
        options.addArguments("--headless");
        ChromeDriver driver = new ChromeDriver(options);
        // Open up the HTML file in our headless browser. This step will evaluate the JavaScript for us.
        driver.navigate().to("data:text/html;charset=utf-8," + html);
        WebElement foo = new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(xdriver -> xdriver.findElement(By.tagName("p")));
        System.out.println(foo.getText());

        // Now all we have to do is extract the evaluated HTML syntax and convert it to a PDF using iText's HtmlConverter.
        String evaluatedHtml = (String) driver.executeScript("return document.documentElement.innerHTML;");
        HtmlConverter.convertToPdf(evaluatedHtml, new FileOutputStream(dest));
    }
}