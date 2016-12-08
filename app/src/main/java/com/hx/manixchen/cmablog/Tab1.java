package com.hx.manixchen.cmablog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by manixchen on 2016/11/30.
 */

public class Tab1 {
    public static String getAllAppList(String doAction){
        String path="http://192.168.50.16:8080/Servers/servlet/Servers";
        try {
            System.out.println("path--："+path);
            URL url=new URL(path);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();
            conn.setReadTimeout(2000);
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("POST");
            String data = "action=" + URLEncoder.encode(doAction, "UTF-8");
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
            System.out.println("code--："+code);
            if(code==200){
                //请求成功返回流
                InputStream is = conn.getInputStream();
                String text = StreamTools.readInputStream(is);
                System.out.println("resp-++-->:"+text);
                if("err".equals(text)){
                    return null;
                }else{
                    //存储用户名信息到SQLLite
                    //JSONTokener jsonTokener = new JSONTokener(text);
                    JSONObject jsonObject = new JSONObject(text);
                    JSONArray resp = jsonObject.getJSONArray("resp");
//                    String[] appVersion={};
//                    String[] appSize={};
//                    String[] appName={};
//                    String[] appId={};
                    ArrayList<HashMap<String, Object>> Tab03Item = new ArrayList<>();

                    //List<String> appIconList = new ArrayList<String>();
                   // List<String> appSizeList = new ArrayList<String>();
                    //List<String> appNameList = new ArrayList<String>();
                    //List<String> appIdList = new ArrayList<String>();
                    //List<String> appVersionList = new ArrayList<String>();
                    for (int i=0;i<resp.length();i++)
                    {
                        JSONObject jsonObjectSon= (JSONObject)resp.opt(i);

                        //appIconList.add(jsonObjectSon.getString("app_icon"));
                        //appSizeList.add(jsonObjectSon.getString("app_size"));
                       // appNameList.add(jsonObjectSon.getString("app_name"));
                       // appIdList,..add(jsonObjectSon.getString("id"));
                        //appVersionList.add(jsonObjectSon.getString("version"));

                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("tv_icon", jsonObjectSon.getString("app_icon"));//图像资源的ID
                        map.put("tv_title", jsonObjectSon.getString("app_name"));
                        map.put("app_downloadTimers", "22万次");
                        map.put("app_Size", jsonObjectSon.getString("app_size"));
                        map.put("tv_description", jsonObjectSon.getString("app_icon"));
                        map.put("app_downloading","下载");
                        Tab03Item.add(map);

                        //bianliAllList(Tab03Item);

                    }

                    System.out.println("resp:++++:::"+jsonObject);
                    //System.out.println("appIconList:++++:::"+appIconList);
                }
                return text;
            }else{
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
