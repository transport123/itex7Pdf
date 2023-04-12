package org.example.freemark;


import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FMInstance {
    private final Configuration fmConfig;
    //这是一个应用级别的单例，不需要重复创建
    //多线程情况下修改设置是不安全的，需要谨慎

    public static final String ftls = "src/main/resources/docs/templates/";

    private static final FMInstance fmInstance= new FMInstance();

    private FMInstance()
    {
        fmConfig=new Configuration(Configuration.VERSION_2_3_22);
        try {
            initConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initConfig() throws IOException {


        // Where do we load the templates from:
//        cfg.setClassForTemplateLoading(MainTest.class, "templates");

        // Some other recommended settings:
        fmConfig.setDefaultEncoding("UTF-8");
        fmConfig.setDirectoryForTemplateLoading(new File(ftls));
//        cfg.setLocale(Locale.CHINESE);
        fmConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public static Configuration getFMConfig()
    {
        return fmInstance.fmConfig;
    }


}
