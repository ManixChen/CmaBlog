package com.hx.manixchen.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.hx.manixchen.db.ThreadDaoDownLoadListImpl;
import com.hx.manixchen.db.ThreadDaoDownloadList;
import com.hx.manixchen.views.Dowload;
import com.hx.manixchen.views.ThreadInfoDownloadList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by manixchen on 2016/12/5.
 */

public class DownloadListServices extends Service implements Serializable{
    HashMap app_map=null;
    private  DownloadListAddTask mDownLoadTask=null;
    public static JSONObject mJsonObj;
    private String deleteAppNam;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(DowloadServices.ACTION_START.equals(intent.getAction())){//开始下载
            app_map = (HashMap) intent.getSerializableExtra("app_map");
            System.out.println("app_map+++:::<>:"+app_map);
           new InitThread(app_map).start();
        }else  if(DowloadServices.ACTION_GETALLAPP.equals(intent.getAction())){
            System.out.println("30.2>:获取所有下载中应用");
          new GetAllApp().start();
        }else if(DowloadServices.ACTION_DELETEALLAPP.equals(intent.getAction())){
            new DeleteAllDownApp().start();
        }else if(DowloadServices.ACTION_DELETAPP.equals(intent.getAction())){
            Bundle extras = intent.getExtras();
            deleteAppNam=extras.getString("app_name");
            System.out.println("49.11>:删除已下载应用"+deleteAppNam);
            new DeleteCurrentApp().start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case DowloadServices.MSG_INIT:
                    Object obj = msg.obj;
                    System.out.println("添加相应下载信息到数据库handler:::>"+obj);
                    if (obj!=null){
                        mDownLoadTask= new DownloadListAddTask(DownloadListServices.this, (HashMap) obj);
                       // System.out.println("11.2:mDownLoadTask:"+mDownLoadTask);
                         mDownLoadTask.download();
                    }
                    break;
                case  DowloadServices.MSG_GET:
                    try {
                        System.out.println("30.3获取数据库中所有下载信息到handler:::<>");
                        GetAllAppInfo getAllAppInfo = new GetAllAppInfo(DownloadListServices.this);
                        getAllAppInfo.getallApp();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("30.4获取数据库中所有下载信息到handler:::<>");
                    break;
                case  DowloadServices.MSG_DELETE:
                    DeleteAllDownAppInfo deleteAllDownAppInfo = new DeleteAllDownAppInfo(DownloadListServices.this);
                    deleteAllDownAppInfo.deleteAllDownApp();
                    break;
                case DowloadServices.MSG_DELETECURRENT:
                    DeleteCApp deleteCApp = new DeleteCApp(DownloadListServices.this);
                    System.out.println("当前删除的应用程序"+deleteAppNam);
                    deleteCApp.deleteCurrentApp(deleteAppNam);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     *定义一个初始化的子线程
     */
    class InitThread extends Thread{
        private HashMap getMAP;
        public InitThread(HashMap map) {
            this.getMAP = map;
        }
        @Override
        public void run() {
            super.run();
            mHandler.obtainMessage(DowloadServices.MSG_INIT,app_map).sendToTarget();
        }
    }

    /**
     * 获取所有下载中应用信息
     */
    class  GetAllApp extends  Thread{
        @Override
        public void run() {
            super.run();
            mHandler.obtainMessage(DowloadServices.MSG_GET).sendToTarget();
        }
    }

    class GetAllAppInfo implements Serializable{
        private Context mContent = null;
        private ThreadDaoDownloadList mDao;
        public GetAllAppInfo(Context mContent) {
            this.mContent = mContent;
            mDao = (ThreadDaoDownloadList) new ThreadDaoDownLoadListImpl(mContent);
        }
        public void getallApp(){
            try {
                List<ThreadInfoDownloadList> threadInfoDownloadLists = mDao.selAllApp();
                JSONArray jsonArray = new JSONArray();

                System.out.println("所有应用获取成功"+threadInfoDownloadLists.size());
                for(int i=0;i<threadInfoDownloadLists.size();i++){
                    ThreadInfoDownloadList mThreadInfoDownloadList=new ThreadInfoDownloadList();
                    JSONObject jsonObject = new JSONObject();
                    String appName = threadInfoDownloadLists.get(i).getAppName();
                    String version = threadInfoDownloadLists.get(i).getVersion();
                    String appSize = threadInfoDownloadLists.get(i).getAppSize();
                    String appIcon = threadInfoDownloadLists.get(i).getAppIcon();
                    String downloadAddress = threadInfoDownloadLists.get(i).getDownloadAddress();
                    int downloadTimes = threadInfoDownloadLists.get(i).getDownloadTimes();

                    jsonObject.put("appName",appName);
                    jsonObject.put("version",version);
                    jsonObject.put("appSize",appSize);
                    jsonObject.put("appIcon",appIcon);
                    jsonObject.put("downloadAddress",downloadAddress);
                    jsonObject.put("downloadTimes",downloadTimes);
                    jsonArray.put(i,jsonObject);
                }
                /**  */
                mJsonObj=new JSONObject();
                Intent intent=new Intent(DowloadServices.ACTION_GETALLAPP);
//                Bundle bundle = new Bundle();
                mJsonObj.put("all",jsonArray);
//                bundle.putSerializable("allApp","allApp");
//                intent.putExtras(bundle);
                System.out.println("所有应用获取成功并发送广播");
                 mContent.sendBroadcast(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 删除当前下载完成APP
     */
    class DeleteCurrentApp extends Thread{
        @Override
        public void run() {
            super.run();
            mHandler.obtainMessage(DowloadServices.MSG_DELETECURRENT).sendToTarget();
        }
    }

    class  DeleteCApp{
        private Context mContent = null;
        private ThreadDaoDownloadList mDao;
        public DeleteCApp(Context mContent) {
            this.mContent = mContent;
            mDao = (ThreadDaoDownloadList) new ThreadDaoDownLoadListImpl(mContent);
        }
        public void deleteCurrentApp(String appName){
            System.out.println("当前删除的应用程序：：："+appName);
            mDao.deleteApp(appName);
        }
    }

    /**
     * 删除所有下载中应用
     */
    class  DeleteAllDownApp extends  Thread{
        @Override
        public void run() {
            super.run();
            mHandler.obtainMessage(DowloadServices.MSG_DELETECURRENT).sendToTarget();
        }
    }
    class  DeleteAllDownAppInfo {
        private Context mContent = null;
        private ThreadDaoDownloadList mDao;

        public DeleteAllDownAppInfo(Context mContent) {
            this.mContent = mContent;
            mDao = (ThreadDaoDownloadList) new ThreadDaoDownLoadListImpl(mContent);
        }
        public void deleteAllDownApp(){
            System.out.println("40.1>:删除所有应用");
            mDao.deleteALL();
            Intent intent=new Intent(DowloadServices.ACTION_DELETEALLAPP);
            mContent.sendBroadcast(intent);
        }
    }
}
