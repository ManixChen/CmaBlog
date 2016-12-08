package com.hx.manixchen.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hx.manixchen.cmablog.R;
import com.hx.manixchen.services.DowloadServices;
import com.hx.manixchen.services.DownloadListServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dowload extends AppCompatActivity implements View.OnClickListener {
    private Context mContext = null;
    private ImageButton backBnt;
    private ListView mLvFile = null;
    private List<FileInfo> mFileList = null;
    private FileListAdapter mAdapter = null;
    private TextView app_title;
    private ImageButton search_btn;
    private TextView downloadAll;
    private JSONObject backJsonObj;
    private String delAppName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dowload);
        initView();
    }

    private void initView() {
        backBnt = (ImageButton) findViewById(R.id.backBtn);
        backBnt.setOnClickListener(this);
        mLvFile = (ListView) findViewById(R.id.lvfile);//ListView容器
        app_title = (TextView) findViewById(R.id.app_title);
        app_title.setText("高速下载器");
        search_btn = (ImageButton) findViewById(R.id.search_btn);
        downloadAll = (TextView) findViewById(R.id.downloadAllApp);
        downloadAll.setOnClickListener(this);

        /**
         * 获取所有下载中应用的Service以及应用接收器
         */
        Intent intent = new Intent(Dowload.this, DownloadListServices.class);
        intent.setAction(DowloadServices.ACTION_GETALLAPP);
        System.out.println("30.1>:获取所有下载中应用");
        startService(intent);
        IntentFilter getFilter = new IntentFilter();
        getFilter.addAction(DowloadServices.ACTION_GETALLAPP);
        getFilter.addAction(DowloadServices.ACTION_DELETEALLAPP);
        registerReceiver(getAllApp, getFilter);

        /**
         * 下载线程广播接收器
         */
        IntentFilter filter = new IntentFilter();
        filter.addAction(DowloadServices.ACTION_UPDATE);
        filter.addAction(DowloadServices.ACTION_FINISHED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unregisterReceiver(getAllApp);
    }

    //更新Ui的广播接受器
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DowloadServices.ACTION_UPDATE.equals(intent.getAction())) {//更新进度
                int finished = intent.getIntExtra("finished", 0);
                int id = intent.getIntExtra("id", 0);
                mAdapter.updateProgress(id, finished);

            } else if (DowloadServices.ACTION_FINISHED.equals(intent.getAction())) {//更新进度
                FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
                mAdapter.updateProgress(fileInfo.getId(), 0);
                delAppName= mFileList.get(fileInfo.getId()).getFileName();
                //删除当前下载任务
                Bundle bundle = new Bundle();
                Intent intentDelete = new Intent(Dowload.this, DownloadListServices.class);
                intent.setAction(DowloadServices.ACTION_DELETAPP);
                bundle.putString("app_name", delAppName);
                System.out.println("49.10>:删除已下载应用");
                intentDelete.putExtras(bundle);
                startService(intentDelete);

                Toast.makeText(Dowload.this,
                        delAppName + "已下载完毕", Toast.LENGTH_SHORT).show();
            }
        }
    };
    //获取所有下载中应用接收器
    BroadcastReceiver getAllApp = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DowloadServices.ACTION_GETALLAPP.equals(intent.getAction())) {
                System.out.println("back---接收应用广播");
                // String allAppInfo= (String) intent.getSerializableExtra("allApp");
                try {
                    backJsonObj = DownloadListServices.mJsonObj;
                    JSONArray arr = backJsonObj.getJSONArray("all");
                    System.out.println("arr--" + arr.length());
                    //文件集合
                    mFileList = new ArrayList<FileInfo>();
                    for (int i = 0; i < arr.length(); i++) {
                        final JSONObject jsonObjectSon = (JSONObject) arr.opt(i);
                        String appName = jsonObjectSon.getString("appName");
                        String downloadAddress = jsonObjectSon.getString("downloadAddress");
                        int i1 = downloadAddress.lastIndexOf("/", downloadAddress.length() - 1);
                        String appEnName = downloadAddress.substring(i1 + 1, downloadAddress.length());
                        //System.out.println("appEnName:::"+appEnName);
                        mFileList.add(new FileInfo(i, downloadAddress, appName, 0, 0));
                    }
                    mAdapter = new FileListAdapter(Dowload.this, mFileList);
                    mLvFile.setAdapter(mAdapter);
                    if (arr.length() == 0) {
                        //当存在下载任务时候才执行
                        downloadAll.setVisibility(View.INVISIBLE);
                        search_btn.setVisibility(View.VISIBLE);
                    } else {
                        downloadAll.setVisibility(View.VISIBLE);
                        search_btn.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (DowloadServices.ACTION_DELETEALLAPP.equals(intent.getAction())) {
                System.out.println("delete---接收应用广播");
                //更新页面
                finish();
                startActivity(new Intent(Dowload.this, Dowload.class));
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;
            case R.id.downloadAllApp:
                Intent intent = new Intent(Dowload.this, DownloadListServices.class);
                intent.setAction(DowloadServices.ACTION_DELETEALLAPP);
                System.out.println("30.1>:获取所有下载中应用");
                startService(intent);
                break;
        }
    }
}
