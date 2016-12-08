package com.hx.manixchen.cmablog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.hx.manixchen.views.AppDetails;
import com.hx.manixchen.views.AppSettings;
import com.hx.manixchen.views.Dowload;
import com.hx.manixchen.views.HasInstallApk;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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
import java.util.List;

public class BlogIndex extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    //所有Tab頁面
    private View view1, view2, view3, view4;
    //对应的viewPager
    private ViewPager viewPager;
    //view数组
    private List<View> viewList;
    //所有切換按鈕
    private TextView tv_1, tv_2, tv_3, tv_4, btnTab1, btnTab2, btnTab3;
    //適配器
    private PagerAdapter mAdapter;
    private Context blogIndexContent;
    private List<NewsBean> newsBeanList;
    public String URL = "http://192.168.50.16:8080/Servers/servlet/Servers";
    private BlogIndex blogIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_index);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("应用管理");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);

        //初始化相关的元素以及事件
        initEvents();
        //初始化ViewPager
        initPagerView();

    }

    private void initEvents() {
        //所有元素
        tv_1 = (TextView) findViewById(R.id.tv_tab1);
        tv_2 = (TextView) findViewById(R.id.tv_tab2);
        tv_3 = (TextView) findViewById(R.id.tv_tab3);
        tv_4 = (TextView) findViewById(R.id.tv_tab4);
        btnTab1 = (TextView) findViewById(R.id.tv_tab1_bottom);
        btnTab2 = (TextView) findViewById(R.id.tv_tab2_bottom);
        btnTab3 = (TextView) findViewById(R.id.tv_tab3_bottom);

        tv_1.setBackground(getResources().getDrawable(R.drawable.borderbottom));
        tv_1.setOnClickListener(this);
        tv_2.setOnClickListener(this);
        tv_3.setOnClickListener(this);
        tv_4.setOnClickListener(this);
        btnTab1.setOnClickListener(this);
        btnTab2.setOnClickListener(this);
        btnTab3.setOnClickListener(this);
        //ViewPager导航部分
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        LayoutInflater inflater = getLayoutInflater();
        view1 = inflater.inflate(R.layout.activity_tab1, null);
        view2 = inflater.inflate(R.layout.activity_tab2, null);
        view3 = inflater.inflate(R.layout.activity_tab3, null);
        view4 = inflater.inflate(R.layout.activity_tab4, null);
        //处理相应数据
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        viewList.add(view4);
        //初始化选项一的应用
        new Thread() {
            @Override
            public void run() {
                super.run();
                final List result = getAllAppList("sellAllApp");
            }
        }.start();
    }

    private void initPagerView() {
        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                // TODO Auto-generated method stub
                //System.out.println("position++>:"+position);
                container.removeView(viewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                container.addView(viewList.get(position));
                //System.out.println("position-->:"+position);
                return viewList.get(position);
            }
        };
        viewPager.setAdapter(pagerAdapter);
        //监听pager改变
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                resetBottomBar();
                int currentItem = viewPager.getCurrentItem();
                switch (currentItem) {
                    case 0:
                        settingBackground(tv_1);
                        break;
                    case 1:
                        settingBackground(tv_2);
                        initAppList();
                        break;
                    case 2:
                        settingBackground(tv_3);
                        break;
                    case 3:
                        settingBackground(tv_4);
                        break;
                }
            }


            @Override
            public void onPageScrollStateChanged(int state) {
                System.out.println(state);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.blog_index, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(BlogIndex.this, AppSettings.class);
            startActivity(intent);
        } else if (id == R.id.action_exit) {
            Intent intent = new Intent(BlogIndex.this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {//已安装应用
            // Toast.makeText(BlogIndex.this,"",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(BlogIndex.this, HasInstallApk.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        resetBottomBar();
        switch (v.getId()) {
            //顶部选项卡
            case R.id.tv_tab1:
                viewPager.setCurrentItem(0);
                settingBackground(tv_1);
                break;
            case R.id.tv_tab2:
                viewPager.setCurrentItem(1);
                settingBackground(tv_2);
                initAppList();
                break;
            case R.id.tv_tab3:
                viewPager.setCurrentItem(2);
                settingBackground(tv_3);
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        new NewsAsyncTask().execute(URL);
                    }
                }.start();
                break;
            case R.id.tv_tab4:
                viewPager.setCurrentItem(3);
                settingBackground(tv_4);
                break;
            //底部选项卡
            case R.id.tv_tab1_bottom:
                Intent intent=new Intent(BlogIndex.this,Dowload.class);
                startActivity(intent);
                break;
            case R.id.tv_tab2_bottom:
                Intent intent1=new Intent(BlogIndex.this,HasInstallApk.class);
                startActivity(intent1);
                break;
            case R.id.tv_tab3_bottom:
                break;

        }
    }

    private void settingBackground(TextView elem) {
        elem.setBackground(getResources().getDrawable(R.drawable.borderbottom));
    }

    //重設置所有按鈕的顏色
    private void resetBottomBar() {
        // tv_1.setBackground(getDrawable(R.drawable.ic_info_black_24dp));
        tv_1.setBackgroundColor(Color.rgb(1, 154, 96));
        tv_2.setBackgroundColor(Color.rgb(1, 154, 96));
        tv_3.setBackgroundColor(Color.rgb(1, 154, 96));
        tv_4.setBackgroundColor(Color.rgb(1, 154, 96));
//        tv_5.setBackgroundColor(Color.rgb(1, 154, 96));
//        tv_6.setBackgroundColor(Color.rgb(1, 154, 96));
    }


    /**Tab1获取数据*/
    public List<NewsBean> getAllAppList(String doAction) {
        String path = "http://192.168.50.16:8080/Servers/servlet/Servers";
        try {
            System.out.println("path--：" + path);
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
            System.out.println("code--：" + code);
            if (code == 200) {
                //请求成功返回流
                InputStream is = conn.getInputStream();
                String text = StreamTools.readInputStream(is);
                System.out.println("resp-++-->:" + text);
                if ("err".equals(text)) {
                    return null;
                } else {
                    //JSONTokener jsonTokener = new JSONTokener(text);
                    JSONObject jsonObject = new JSONObject(text);
                    JSONArray resp = jsonObject.getJSONArray("resp");
                    final ArrayList<HashMap<String, Object>> Tab03Item = new ArrayList<HashMap<String, Object>>();

                    newsBeanList = new ArrayList<>();
                    NewsBean newsBean;

                    for (int i = 0; i < resp.length(); i++) {
                        final JSONObject jsonObjectSon = (JSONObject) resp.opt(i);
                        final HashMap<String, Object> map = new HashMap<String, Object>();


                        map.put("tv_icon", R.mipmap.logo2);//图像资源的ID
                        // new allAppImgLoader().showImgByThread(R.id.tv_icon, jsonObjectSon.getString("app_icon"));
                        map.put("tv_title", jsonObjectSon.getString("app_name"));
                        map.put("app_downloadTimers", "22万次");
                        map.put("app_Size", jsonObjectSon.getString("app_size"));
                        map.put("tv_description", jsonObjectSon.getString("app_icon"));
                        map.put("app_downloading", "下载");
                        Tab03Item.add(map);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ListView TabO3ListView = (ListView) findViewById(R.id.Tab01_listview);

                                SimpleAdapter tab01Adapter = new SimpleAdapter(BlogIndex.this, Tab03Item, R.layout.tab1_list_item,
                                        new String[]{"tv_icon", "tv_title", "app_downloadTimers", "app_Size", "tv_description", "app_downloading"},
                                        new int[]{R.id.tv_icon, R.id.tv_title, R.id.app_downloadTimers, R.id.app_Size, R.id.tv_description, R.id.app_downloading});
                                TabO3ListView.setAdapter(tab01Adapter);
                                //天添加相应点击事件
                                TabO3ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        // Toast.makeText(BlogIndex.this,position,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                    }
                    System.out.println("resp:++++:::" + jsonObject);
                }
                return newsBeanList;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Tab2的模拟数据
    private void initAppList() {
        ListView TabO1ListView = (ListView) findViewById(R.id.Tab02_listview);
        ArrayList<HashMap<String, Object>> Tab01Item = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("tv_icon", R.mipmap.logo3);//图像资源的ID
            map.put("tv_title", "应用宝");
            map.put("app_downloadTimers", "33万次");
            map.put("app_Size", "25MB");
            map.put("tv_description", "啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊");
            map.put("app_downloading", "下载");
            Tab01Item.add(map);
        }
        SimpleAdapter tab01Adapter = new SimpleAdapter(BlogIndex.this, Tab01Item, R.layout.tab1_list_item,
                new String[]{"tv_icon", "tv_title", "app_downloadTimers", "app_Size", "tv_description", "app_downloading"},
                new int[]{R.id.tv_icon, R.id.tv_title, R.id.app_downloadTimers, R.id.app_Size, R.id.tv_description, R.id.app_downloading});
        TabO1ListView.setAdapter(tab01Adapter);
        //天添加相应点击事件
        TabO1ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(BlogIndex.this,position,Toast.LENGTH_SHORT).show();
            }
        });
    }


    public Handler bbHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            System.out.println("bbMsg.obj::::" + msg.obj);
            goToDowload();
        }
    };

    public void goToDowload() {
        System.out.println("==========开始下载=============");
    }

    private List<NewsBean> getJsonData(String url) {
        List<NewsBean> newsBeanList = new ArrayList<>();
        System.out.println("事实上已经开始执行了该逻辑，请不要担心……会按照你的思想进行下去的，稍后当然还需要对这些进行严格的封装，使效率达到一定的高度，不然你懂得");

        String text = null;
        text = readStream(url);
        System.out.println("jsonStream::<>::" + text);
        if ("err".equals(text)) {
            return null;
        } else {
            try {
                newsBeanList = new ArrayList<>();
                NewsBean newsBean;
                JSONObject jsonObject = new JSONObject(text);
                JSONArray resp = jsonObject.getJSONArray("resp");
                System.out.println("resp::<>::" + resp);
                for (int i = 0; i < resp.length(); i++) {
                    final JSONObject jsonObjectSon = (JSONObject) resp.opt(i);
                    System.out.println("jsonObjectSon::><:"+jsonObjectSon);
                    //初始化NESBean
                    newsBean = new NewsBean();
                    newsBean.tv_icon = jsonObjectSon.getString("app_icon");
                    newsBean.tv_title = jsonObjectSon.getString("app_name");
                    newsBean.app_downloadTimers = "22万次";
                    newsBean.app_Size = jsonObjectSon.getString("app_size");
                    newsBean.tv_description = jsonObjectSon.getString("app_icon");
                    newsBean.app_downloading = "下载";
                    newsBean.app_addres=jsonObjectSon.getString("download_address");
                    newsBeanList.add(newsBean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return newsBeanList;
    }

    private String readStream(String is) {
        try {
            URL url = new URL(is);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(2000);
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("POST");
            String data = "action=" + URLEncoder.encode("sellAllApp", "UTF-8");
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
                InputStream isr = conn.getInputStream();
                String text = StreamTools.readInputStream(isr);
                System.out.println("resp-++-->:" + text);
                if ("err".equals(text)) {
                    return null;
                }
                return text;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    class NewsAsyncTask extends AsyncTask<String, Void, List<NewsBean>> {
        @Override
        protected List<NewsBean> doInBackground(String... params) {
            return getJsonData(URL);
        }

        @Override
        protected void onPostExecute(List<NewsBean> newsBeen) {
            super.onPostExecute(newsBeen);
            //将适配器设置给tab3选项卡内容
            NewsAdapter adapter = new NewsAdapter(BlogIndex.this, newsBeen);
            final ListView TabO3ListView = (ListView) findViewById(R.id.Tab03_listview);
            TabO3ListView.setAdapter(adapter);

            TabO3ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    System.out.println("position:::>:" + position);
                    String tag = (String) view.findViewById(R.id.app_downloading).getTag();
                    System.out.println("view:::>:" + tag);
                    //传参数给详情页
                    Bundle bundle = new Bundle();
                    bundle.putString("app_name", tag);

                    Intent intent = new Intent(BlogIndex.this, AppDetails.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });


        }
    }

}
