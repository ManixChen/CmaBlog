package com.hx.manixchen.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.hx.manixchen.cmablog.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AppSettings extends AppCompatActivity implements View.OnClickListener{
    private ImageButton backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);
        backBtn =(ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        //绑定Layout里面的ListView
        ListView list = (ListView) findViewById(R.id.ListViewSettings);
        //生成动态数组，加入数据
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<9;i++) {
            if(i==0){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemImage", R.mipmap.timing_img2);//图像资源的ID
                map.put("ItemTitle", "个人信息");
                map.put("LastImage", R.drawable.ic_menu_send);
                listItem.add(map);
            }else if(i==1){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemImage", R.mipmap.timing_img2);//图像资源的ID
                map.put("ItemTitle", "同时下载数量");
                map.put("LastImage", R.drawable.ic_menu_send);
                listItem.add(map);
            }else if(i==2){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemImage", R.mipmap.timing_img2);//图像资源的ID
                map.put("ItemTitle", "网络设置");
                map.put("LastImage", R.drawable.ic_menu_send);
                listItem.add(map);
            }else if(i==3){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemImage", R.mipmap.timing_img2);//图像资源的ID
                map.put("ItemTitle", "应用自动安装");
                map.put("LastImage", R.drawable.ic_menu_send);
                listItem.add(map);
            }else if(i==4){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemImage", R.mipmap.timing_img2);//图像资源的ID
                map.put("ItemTitle", "创建桌面快捷方式");
                map.put("LastImage", R.drawable.ic_menu_send);
                listItem.add(map);
            }else if(i==5){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemImage", R.mipmap.timing_img2);//图像资源的ID
                map.put("ItemTitle", "清除图片缓存");
                map.put("LastImage", R.drawable.ic_menu_send);
                listItem.add(map);
            }else if(i==6){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemImage", R.mipmap.timing_img2);//图像资源的ID
                map.put("ItemTitle", "意见反馈");
                map.put("LastImage", R.drawable.ic_menu_send);
                listItem.add(map);
            }else if(i==7){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemImage", R.mipmap.timing_img2);//图像资源的ID
                map.put("ItemTitle", "关于产品");
                map.put("LastImage", R.drawable.ic_menu_send);
                listItem.add(map);
            }else if(i==8){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemImage", R.mipmap.timing_img2);//图像资源的ID
                map.put("ItemTitle", "返回");
                map.put("LastImage", R.drawable.ic_menu_send);
                listItem.add(map);
            }

        }
        //生成适配器的Item和动态数组对应的元素
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,// 数据源
                R.layout.list_item,//ListItem的XML实现
                //动态数组与ImageItem对应的子项
                new String[] {"ItemImage","ItemTitle", "LastImage"},
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[] {R.id.ItemImage,R.id.ItemTitle,R.id.lastImage}
        );

        //添加并且显示
        list.setAdapter(listItemAdapter);

        //相应Item点击事件
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if(arg2 == 8){
                    AppSettings.this.finish();
                }
            }
        });

        //添加长按点击
        list.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("设置");
                menu.add(0, 0, 0, "移除");
                menu.add(0, 1, 0, "退出");
            }
        });
    }

    //长按菜单响应函数
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        setTitle("点击了长按菜单里面的第"+item.getItemId()+"个项目");
        return super.onContextItemSelected(item);
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
