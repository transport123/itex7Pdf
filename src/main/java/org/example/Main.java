package org.example;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.font.FontProvider;
import freemarker.template.*;
import org.example.freemark.FMHelper;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class Main {
    public static String dest = "src/main/resources/hello.pdf";
    public static final String HTML = "<h1>Test</h1><p>Hello World</p>";

    public static final String src = "src/main/resources/hello-template.html";

    public static final String absoluteSrc = "file:////E:/sam/gitroot/itex7Pdf/src/main/resources/hello-template.html";

    public static final String absoluteSrcSvg = "file:////E:/sam/gitroot/itex7Pdf/src/main/resources/svg.html";

    public static final String ftls = "src/main/resources/ftls/";


    public static void main(String[] args)  {
        try {
            htmlToPdf();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void testGenerateDocx()throws IOException{
        Map<String, Object> root = new HashMap<>();
        root.put("first", "firstseason");
        root.put("second","2季度!");
        root.put("firstvalue",15);
        root.put("secondvalue",20);
        List<String> names = new ArrayList<>();
        List<InputStream> streams = new ArrayList<>();
        InputStream excelStream = FMHelper.generateInputStream("Workbook2.xml", root);//excel流
        InputStream chartStream = FMHelper.generateInputStream("chart1.xml", root);//chart流
        String chart1Name="word/charts/chart1.xml";
        String workName  = "word/embeddings/Workbook2.xlsx";
        names.add(chart1Name);
        names.add(workName);
        streams.add(chartStream);
        streams.add(excelStream);
        ZipHelper.replaceItem(names,streams);
    }

    public static ConverterProperties createProperties() {
        ConverterProperties props = new ConverterProperties();
        FontProvider fp = new FontProvider();
        fp.addFont("src/main/resources/simsunbd/simsunbd.ttf");
        props.setFontProvider(fp);
        return props;
    }

    public static void ftlToHtml() throws IOException, TemplateException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        // Where do we load the templates from:
        // Some other recommended settings:
        cfg.setDefaultEncoding("UTF-8");
        cfg.setDirectoryForTemplateLoading(new File(ftls));
        cfg.setLocale(Locale.CHINESE);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        Template template = cfg.getTemplate("chart.ftl");
        Map<String, Object> root = new HashMap<>();
        // Put string ``user'' into the root
        root.put("user", "Big Joe");
        // Create the hash for ``latestProduct''
        Map<String, Object> latest = new HashMap<>();
        // and put it into the root
        root.put("latestProduct", latest);
        // put ``url'' and ``name'' into latest
        latest.put("url", "products/greenmouse.html");
        latest.put("name", "green mouse");

        // For the sake of example, also write output into a file:
        Writer fileWriter = new FileWriter(new File("src/main/resources/output.html"), StandardCharsets.UTF_8);
        try {
            template.process(root, fileWriter);
        } finally {
            fileWriter.close();
        }
    }

    public static void htmlToPdf() throws IOException {
        //String html = new String(Files.readAllBytes(Paths.get(src)),StandardCharsets.UTF_8);
        // We start by setting up our browser driver. The driver version you need here might differ depending on your Selenium version.
        // It is also possible to use a different 3rd party library to automatically download the necessary driver.
        // This way you don't have to bother with setting the path to the executable. An example of such a library is https://github.com/bonigarcia/webdrivermanager.
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        // Spin up the browser in "headless" mode, this way Selenium will not open up a graphical user interface.
        options.addArguments("--headless");
        ChromeDriver driver = new ChromeDriver(options);
        // Open up the HTML file in our headless browser. This step will evaluate the JavaScript for us.
        //driver.get("data:text/html;charset=utf-8," + html);
        driver.get(absoluteSrc);
        //new WebDriverWait(driver, Duration.ofSeconds(5)).until(webdriver -> webdriver.findElement(By.tagName("p")));
        boolean success = new WebDriverWait(driver, Duration.ofSeconds(5)).until(webdriver -> ((JavascriptExecutor) webdriver).executeScript("return document.readyState").equals("complete"));
        if (!success) {
            driver.close();
            return;
        }

        //String rawHtml = driver.getPageSource();
        // Now all we have to do is extract the evaluated HTML syntax and convert it to a PDF using iText's HtmlConverter.
        String evaluatedHtml = (String) driver.executeScript("return document.documentElement.innerHTML;");
        PdfWriter pdfWriter = new PdfWriter(new FileOutputStream(dest));

        HtmlConverter.convertToPdf(evaluatedHtml,pdfWriter , createProperties());

    }



}