package com.hx.manixchen.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.hx.manixchen.cmablog.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AppDetails extends AppCompatActivity implements View.OnClickListener{
    private String app_name;
    private ImageButton backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);
        //获取上文传递的参数
        Bundle bundle=getIntent().getExtras();
        app_name=bundle.getString("app_name");

        System.out.println("app_name:::><:"+app_name);
        initElement();
        new Thread(){
            @Override
            public void run() {
                super.run();
                String path = "http://192.168.0.105:8080/Servers/servlet/Servers";
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(2000);
                    conn.setConnectTimeout(3000);
                    conn.setRequestMethod("POST");
                    String data = "action=" + URLEncoder.encode("sellCurrentAppInfo", "UTF-8")+
                            "&app_name=" + URLEncoder.encode(app_name, "UTF-8");
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
                        System.out.println("resp-++-->:" + text);
//                        if ("err".equals(text)) {
//                        } else {
//                            JSONObject jsonObject = new JSONObject(text);
//                            JSONArray resp = jsonObject.getJSONArray("resp");
//                            for (int i = 0; i < resp.length(); i++) {
//                                final JSONObject jsonObjectSon = (JSONObject) resp.opt(i);
//                            }
//
//                            System.out.println("resp:++++:::" + jsonObject);
//                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        getCurrentAppAllData();
    }

    private void getCurrentAppAllData() {

    }

    private void initElement() {
        backBtn=(ImageButton)findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backBtn:
                this.finish();
                break;
        }
    }
}
