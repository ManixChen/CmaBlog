package com.hx.manixchen.cmablog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hx.manixchen.db.ThreadDaoDownloadList;
import com.hx.manixchen.services.DowloadServices;
import com.hx.manixchen.services.DownloadListServices;
import com.hx.manixchen.views.ThreadInfoDownloadList;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by manixchen on 2016/12/1.
 */

public class NewsAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext=null;
    private List<NewsBean> mList;
    private LayoutInflater mInflater;
    private Map app_map=null;



    public NewsAdapter(List<NewsBean> mList) {
        this.mList = mList;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            System.out.println("msg.obj::::"+msg.obj);
//            Intent intent=new Intent(BlogIndex.this,AppSettings.class);
//            startActivity(intent);
           // new BlogIndex().goToPage();
        }
    };

    public NewsAdapter(Context mContext, List<NewsBean> data) {
        this.mContext=mContext;
        mList = data;
        mInflater = LayoutInflater.from(mContext);

    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.tab1_list_item, null);
            viewHolder.tv_icon = (ImageView) convertView.findViewById(R.id.tv_icon);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.app_downloadTimers = (TextView) convertView.findViewById(R.id.app_downloadTimers);
            viewHolder.app_Size = (TextView) convertView.findViewById(R.id.app_Size);
            viewHolder.tv_description = (TextView) convertView.findViewById(R.id.tv_description);
            viewHolder.app_downloading = (TextView) convertView.findViewById(R.id.app_downloading);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //设置默认头像
        viewHolder.tv_icon.setImageResource(R.mipmap.logo3);
        //设置身份标志
        String url = mList.get(position).tv_icon;
        viewHolder.tv_icon.setTag(url);
        //下载全部对应图片
        new allAppImgLoader().showImgByThread(viewHolder.tv_icon, url);

        viewHolder.tv_title.setText(mList.get(position).tv_title);
        viewHolder.app_downloadTimers.setText(mList.get(position).app_downloadTimers);
        viewHolder.app_Size.setText(mList.get(position).app_Size);
        viewHolder.tv_description.setText(mList.get(position).tv_description);
        viewHolder.app_downloading.setText(mList.get(position).app_downloading);
        //
         app_map = new HashMap();
        app_map.put("app_name",mList.get(position).tv_title);
        System.out.println("app_name::><"+mList.get(position).tv_title);
        app_map.put("version","1.0");
        app_map.put("app_size",mList.get(position).app_Size);
        app_map.put("app_icon",url);
        app_map.put("download_address",mList.get(position).app_addres);
        app_map.put("download_times",mList.get(position).app_downloadTimers);
        viewHolder.app_downloading.setTag(app_map);//绑定下载相关信息
        viewHolder.app_downloading.setOnClickListener(this);
        return convertView;
    }


    @Override
    public void onClick(View v) {
        try {
            View app_downloading = v.findViewById(R.id.app_downloading);
            HashMap currentMap= (HashMap) app_downloading.getTag();
            System.out.println("currentMap::><"+currentMap);
            Message message=Message.obtain();
            message.obj=v.getTag();
            new BlogIndex().bbHandler.sendMessage(message);
            /**
             * 思路一：将程序相关信息都存储在下载按钮上,直接传递Map过去避免多次请求数据库---使用
             * 思路二：获取程序的名称，使用查找数据库功能查找相应的信息
             */
            Intent intent = new Intent(mContext, DownloadListServices.class);
            intent.setAction(DowloadServices.ACTION_START);
            Bundle bundle = new Bundle();
            bundle.putSerializable("app_map", (HashMap) currentMap);
            intent.putExtras(bundle);
           // System.out.println("你点击我y是想下载Y::<>::" + app_map);
            mContext.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public final class ViewHolder {
        public ImageView tv_icon;
        public TextView tv_title;
        public TextView app_downloadTimers;
        public TextView app_Size;
        public TextView tv_description;
        public TextView app_downloading;
    }
}
