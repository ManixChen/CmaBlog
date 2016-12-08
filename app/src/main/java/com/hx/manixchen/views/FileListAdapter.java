package com.hx.manixchen.views;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hx.manixchen.cmablog.R;
import com.hx.manixchen.services.DowloadServices;

import java.util.List;

/**文件列白哦适配器
 * Created by manixchen on 2016/12/4.
 */

public class FileListAdapter extends BaseAdapter {
    private Context mContext=null;
    private List<FileInfo> mFileList=null;

    public FileListAdapter(Context mContext, List<FileInfo> mFileList) {
        this.mContext = mContext;
        this.mFileList = mFileList;
    }

    @Override
    public int getCount() {
        return mFileList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final FileInfo fileInfo=mFileList.get(position);
        ViewHolder holder=null;
        if(view==null){
            //加载试图
            view = LayoutInflater.from(mContext).inflate(R.layout.listitem_appdowlnload, null);
            //获得布局中的控件
            holder=new ViewHolder();
            holder.tvFile=(TextView)view.findViewById(R.id.tvFileName);
            holder.pbFile=(ProgressBar)view.findViewById(R.id.tvProgress);
            holder.btnStart=(Button) view.findViewById(R.id.btStart);
            holder.btnStop=(Button)view.findViewById(R.id.btStop);
//            holder.btnStart.setText("下载");
//            holder.btnStop.setText("暂停");
            holder.tvFile.setText(fileInfo.getFileName());
            holder.pbFile.setMax(100);
            holder.btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //通过Intent传递数据给Services
                    Intent intent = new Intent(mContext, DowloadServices.class);
                    intent.setAction(DowloadServices.ACTION_START);
                    intent.putExtra("fileInfo",fileInfo);
                    System.out.println("1点击开始下载按钮::>:"+fileInfo.toString());
                    mContext.startService(intent);
                }
            });
            holder.btnStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //通过Intent传递数据给Services
                    Intent intent = new Intent(mContext, DowloadServices.class);
                    intent.setAction(DowloadServices.ACTION_STOP);
                    intent.putExtra("fileInfo",fileInfo);
                    System.out.println("1点击开始下载按钮::>:"+fileInfo.toString());
                    mContext.startService(intent);
                }
            });
            view.setTag(holder);
        }else {
            holder=(ViewHolder)view.getTag();
        }
        //设置视图中的控件
        holder.pbFile.setProgress(fileInfo.getFinished());
        return view;
    }

    /**
     * 更新列表项中的进度条
     */
    public void updateProgress(int id,int progress){
        FileInfo fileInfo=mFileList.get(id);
        fileInfo.setFinished(progress);
        notifyDataSetChanged();//通知ListView数据更改
    }


    static  class  ViewHolder{
        TextView tvFile;
        Button btnStart;
        Button btnStop;
        ProgressBar pbFile;
    }
}
