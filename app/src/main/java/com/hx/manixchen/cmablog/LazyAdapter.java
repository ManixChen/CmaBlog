package com.hx.manixchen.cmablog;

/**
 * Created by manixchen on 2016/12/1.
 */

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.hx.manixchen.cmablog.ImageLoader;

public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, Object>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; //用来下载图片的类，后面有介绍

    public LazyAdapter(Activity a, ArrayList<HashMap<String, Object>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_raw, null);

        TextView tv_title = (TextView)vi.findViewById(R.id.tv_title); // 标题
        TextView app_downloadTimers = (TextView)vi.findViewById(R.id.app_downloadTimers); // 歌手名
        TextView app_Size = (TextView)vi.findViewById(R.id.app_Size); // 时长
        TextView tv_description = (TextView)vi.findViewById(R.id.tv_description); // 时长
        TextView app_downloading = (TextView)vi.findViewById(R.id.app_downloading); // 时长
        ImageView tv_icon=(ImageView)vi.findViewById(R.id.tv_icon); // 缩略图

        HashMap<String, Object> song = new HashMap<>();
        song = data.get(position);

        // 设置ListView的相关值
        tv_title.setText((Integer) song.get("tv_title"));
        app_downloadTimers.setText((Integer) song.get("app_downloadTimers"));
        app_Size.setText((Integer) song.get("app_Size"));
        tv_description.setText((Integer) song.get("tv_description"));
        app_downloading.setText((Integer) song.get("app_downloading"));
        imageLoader.DisplayImage((String) song.get("tv_icon"), tv_icon);
        return vi;
       }
}