package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class EncodeProblem {

    public static final String dst = "src/main/resources/test.txt";
    public static String content="你好im jack";
    public static void generateFileWithByteStream() throws IOException {
        File file = new File(dst);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte [] bytes = content.getBytes();
        fileOutputStream.write(bytes);
        fileOutputStream.close();
    }

    public static void generateFileWithCharStream() throws IOException {
        File file = new File(dst);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
        outputStreamWriter.append(content);
        outputStreamWriter.close();
    }


}
