package org.example;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipHelper {

    public static final String zipSrcPath = "src/main/resources/docs/test.zip";
    public static final String zipDstPath = "src/main/resources/docs/test-out.docx";


    //一次只替换一项 虽然会多次io 但是不用的流可以关闭 减少内存消耗
    //但是由于新文件是重新生成的，如果要替换多个item还是用list的方式好一点，不过需要注意stream的close
    public static void replaceItem(List<String> itemNames, List<InputStream> itemStreams) throws IOException {
        if (itemStreams == null || itemStreams.isEmpty())
            return;
        if (itemNames == null || itemNames.isEmpty())
            return;
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipDstPath));
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipSrcPath));
        ZipEntry inEntry;
        byte[]buffer=new byte[1024*8];
        int len;
        while ((inEntry = zipInputStream.getNextEntry()) != null) {
            String entryName = inEntry.getName();
            ZipEntry outEntry = new ZipEntry(entryName);
            //名称是确定的
            zipOutputStream.putNextEntry(outEntry);
            int index = itemNames.indexOf(entryName);
            System.out.println(entryName);
            //找到了需要替换的
            if (index>=0){
                //使用自定义流
                InputStream itemStream = itemStreams.get(index);
                while ((len=itemStream.read(buffer))>0){
                    zipOutputStream.write(buffer,0,len);
                }
                itemStream.close();
            }else {
                //使用原始流
                while ((len=zipInputStream.read(buffer))>0) {
                    zipOutputStream.write(buffer,0,len);
                }
            }
            zipOutputStream.closeEntry();
        }
        zipOutputStream.close();
        zipInputStream.close();
    }



}
