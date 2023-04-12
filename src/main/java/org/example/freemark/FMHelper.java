package org.example.freemark;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;


import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FMHelper {


    public static void streamToFile(InputStream inputStream,String dst) throws IOException {
        if(inputStream==null)
            return;
        File file = new File(dst);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStream out = new BufferedOutputStream(fileOutputStream);
        BufferedInputStream in = new BufferedInputStream(inputStream);
        int len =-1;
        byte[] bytes=new byte[1024];
        while ((len=in.read(bytes))!=-1)
        {
            out.write(bytes,0,len);
        }
        out.close();
        in.close();
        fileOutputStream.close();

    }

    public static InputStream generateInputStream(String templatePath, Object root) throws IOException {
        Configuration cfg = FMInstance.getFMConfig();
        Template tplt;
        //创建output流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(16*1024);
        System.out.println(Charset.defaultCharset());
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        try {
            //将数据bean转换为map
            Map<String,Object> data=transformMap(root);
            //定义output writer
            tplt = cfg.getTemplate(templatePath);
            //通过writer向output流中写内容
            tplt.process(data,writer);
        } catch (IOException e) {
            System.out.println(" creating writer failed");
        } catch (TemplateException e) {
            System.out.println(" creating template failed");
        }finally {
            writer.flush();
            writer.close();
            outputStream.close();
        }
        byte[] bytes=outputStream.toByteArray();
        return new ByteArrayInputStream(bytes);
    }

    public static Map<String,Object> transformMap(Object root) {
        Map<String,Object> result = new HashMap<>();
        if (root == null) {
            return result;
        }
        if (root instanceof Map){
            result.putAll((Map)root);
        }
        //使用内省机制将bean转为map
        return result;
    }
}
