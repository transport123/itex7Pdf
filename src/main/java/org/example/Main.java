package org.example;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.font.FontProvider;
import freemarker.template.*;
import org.example.freemark.FMHelper;
import org.example.freemark.FMInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
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
            EncodeProblem.generateFileWithCharStream();
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
//        cfg.setClassForTemplateLoading(MainTest.class, "templates");

        // Some other recommended settings:
        cfg.setDefaultEncoding("UTF-8");
        cfg.setDirectoryForTemplateLoading(new File(ftls));
//        cfg.setLocale(Locale.CHINESE);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // 2. Proccess template(s)
        //
        // You will do this for several times in typical applications.

        // 2.1. Prepare the template input:

//        Map<String, Object> input = new HashMap<String, Object>();
//
//        input.put("title", "Vogella example");
//
//        input.put("exampleObject", new ValueExampleObject("Java object", "me"));
//
//        List<ValueExampleObject> systems = new ArrayList<ValueExampleObject>();
//        systems.add(new ValueExampleObject("Android", "Google"));
//        systems.add(new ValueExampleObject("iOS States", "Apple"));
//        systems.add(new ValueExampleObject("Ubuntu", "Canonical"));
//        systems.add(new ValueExampleObject("Windows7", "Microsoft"));
//        input.put("systems", systems);

        // 2.2. Get the template

        Template template = cfg.getTemplate("chart.ftl");

        // 2.3. Generate the output

        // Write output to the console
//        Writer consoleWriter = new OutputStreamWriter(System.out);
//        template.process(input, consoleWriter);

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
        //        String html =  new String(Files.readAllBytes(Paths.get(src)));
        // We start by setting up our browser driver. The driver version you need here might differ depending on your Selenium version.
        // It is also possible to use a different 3rd party library to automatically download the necessary driver.
        // This way you don't have to bother with setting the path to the executable. An example of such a library is https://github.com/bonigarcia/webdrivermanager.
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
//        // Spin up the browser in "headless" mode, this way Selenium will not open up a graphical user interface.
        options.addArguments("--headless");
        ChromeDriver driver = new ChromeDriver(options);
        // Open up the HTML file in our headless browser. This step will evaluate the JavaScript for us.
//        driver.navigate().to("data:text/html;charset=utf-8," + html);
        driver.get(absoluteSrc);
//        WebElement foo = new WebDriverWait(driver, Duration.ofSeconds(30))
//                .until(xdriver -> xdriver.findElement(By.tagName("p")));
        boolean success = new WebDriverWait(driver, Duration.ofSeconds(20)).until(webdriver -> ((JavascriptExecutor) webdriver).executeScript("return document.readyState").equals("complete"));
        if (!success) {
            driver.close();
            return;
        }
        String rawHtml = driver.getPageSource();
//         driver.close();
//        System.out.println(foo.getText());
//        ConverterProperties properties = new ConverterProperties().setBaseUri(url);
        // Now all we have to do is extract the evaluated HTML syntax and convert it to a PDF using iText's HtmlConverter.
//        String evaluatedHtml = (String) driver.executeScript("return document.documentElement.innerHTML;");
        HtmlConverter.convertToPdf(rawHtml, new FileOutputStream(dest), createProperties());
//        PdfWriter pdfWriter = new PdfWriter("");
    }


//        public static Map<String,Object> Xml2Docx(File outFile, Map<String,Object> dataMap, String document, String documentXmlRels, String originalTemplate){
//            Map<String,Object> retMap = new HashMap<>();
//            retMap.put("succ",true);
//            ZipOutputStream zipout = null;
//            OutputStream outputStream = FileUtil.getOutputStream(outFile);
//            try {
//                //图片配置文件模板
//                ByteArrayInputStream documentXmlRelsInput = FreeMarkUtils.getFreemarkerContentInputStream(dataMap, documentXmlRels);
//                //内容模板
//                ByteArrayInputStream documentInput = FreeMarkUtils.getFreemarkerContentInputStream(dataMap, document);
//                ZipFile zipFile = new ZipFile(originalTemplate);
//                Enumeration<? extends ZipEntry> zipEntrys = zipFile.entries();
//                zipout = new ZipOutputStream(outputStream);
//                //开始覆盖文档------------------
//                int len = -1;
//                byte[] buffer = new byte[1024];
//                while (zipEntrys.hasMoreElements()) {
//                    ZipEntry next = zipEntrys.nextElement();
//                    InputStream is = zipFile.getInputStream(next);
//                    if (next.toString().indexOf("media") < 0) {
//                        zipout.putNextEntry(new ZipEntry(next.getName()));
//                        if (next.getName().indexOf("document.xml.rels") > 0) { //如果是document.xml.rels由我们输入
//                            if (documentXmlRelsInput != null) {
//                                while ((len = documentXmlRelsInput.read(buffer)) != -1) {
//                                    zipout.write(buffer, 0, len);
//                                }
//                                documentXmlRelsInput.close();
//                            }
//                        } else if ("word/document.xml".equals(next.getName())) {//如果是word/document.xml由我们输入
//                            if (documentInput != null) {
//                                while ((len = documentInput.read(buffer)) != -1) {
//                                    zipout.write(buffer, 0, len);
//                                }
//                                documentInput.close();
//                            }
//                        } else {
//                            while ((len = is.read(buffer)) != -1) {
//                                zipout.write(buffer, 0, len);
//                            }
//                            is.close();
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.getStackTrace();
//                retMap.put("succ",false);
//                retMap.put("msg",e.getMessage());
//            }
//            finally {
//                if(zipout!=null){
//                    try {
//                        zipout.close();
//                    } catch (IOException e) {
//                        e.getStackTrace();
//                        retMap.put("succ",false);
//                        retMap.put("msg",e.getMessage());
//                    }
//                }
//                if(outputStream!=null){
//                    try {
//                        outputStream.close();
//                    } catch (IOException e) {
//                        e.getStackTrace();
//                        retMap.put("succ",false);
//                        retMap.put("msg",e.getMessage());
//                    }
//                }
//            }
//            return retMap;
//        }
//    }
}