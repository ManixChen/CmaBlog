package com.hx.manixchen.views;

/**
 * Created by manixchen on 2016/12/3.
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

class StreamTools {
    public static String readInputStream(InputStream is) throws IOException {
        System.out.println("解析数据开始……");
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len=0;
            byte[] buffer = new byte[1024];

            while ((len=is.read(buffer))!=-1){
                baos.write(buffer,0,len);
            }
            is.close();
            baos.close();
            byte[] results = baos.toByteArray();
            //试着解析 result中的字符串
            String temp=new String(results);
            System.out.println("temp--->:"+temp);
            return temp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}