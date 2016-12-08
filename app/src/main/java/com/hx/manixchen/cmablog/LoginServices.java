package com.hx.manixchen.cmablog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoginServices {
    public static String loginByGet(String username, String password, String loginway) {
        //服务器请求
        //String path="http://192.168.50.16:8080/Servers/servlet/Servers?username="+username+"&password="+password;
        String path = "http://192.168.0.105:8080/Servers/servlet/Servers";
        try {
            System.out.println("path--：" + path);
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(2000);
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("POST");
            String data = "username=" + URLEncoder.encode(username, "UTF-8")
                    + "&password=" + URLEncoder.encode(password, "UTF-8")
                    + "&action=" + URLEncoder.encode(loginway, "UTF-8");
            // 设置请求的头
            conn.setRequestProperty("Connection", "keep-alive");
            // 设置请求的头
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            // 设置请求的头
            conn.setRequestProperty("Content-Length",
                    String.valueOf(data.getBytes().length));
            // 设置请求的头
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");

            conn.setDoOutput(true); // 发送POST请求必须设置允许输出
            conn.setDoInput(true); // 发送POST请求必须设置允许输入
            //获取输出流
            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes());
            os.flush();

            int code = conn.getResponseCode();
            System.out.println("code--：" + code);
            if (code == 200) {
                //请求成功返回流
                InputStream is = conn.getInputStream();
                String text = StreamTools.readInputStream(is);
                System.out.println("resp--->:" + text);
                if ("err".equals(text)) {
                    return null;
                } else {
                    //存储用户名信息到SQLLite

                }
                return text;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
