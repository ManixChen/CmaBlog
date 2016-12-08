package com.hx.manixchen.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.hx.manixchen.views.FileInfo;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by manixchen on 2016/12/3.
 */

public class DowloadServices extends Service {
    //SD卡根路径
    public  static final String DOWNLOAD_PATH= Environment.getExternalStorageDirectory().
            getAbsolutePath()+"/downloads1/";
    public  static final  String ACTION_START="ACTION_START";
    public  static final  String ACTION_STOP="ACTION_STOP";
    public  static final  String ACTION_FINISHED="ACTION_FINISHED";
    public  static final  String ACTION_UPDATE="ACTION_UPDATE";
    public  static final  String ACTION_GETALLAPP="ACTION_GETALLAPP";
    public  static final  String ACTION_DELETAPP="ACTION_DELETAPP";
    public  static final  String ACTION_DELETEALLAPP="ACTION_DELETEALLAPP";
    public static final int MSG_INIT=0;
    public static final int MSG_GET=1;
    public static final int MSG_DELETE=2;
    public static final int MSG_DELETECURRENT=3;
    private DowloadTask mDownloadTask=null;
    //下载任务的集合
    private Map<Integer,DowloadTask> mTasks=
            new LinkedHashMap<Integer,DowloadTask>();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //获得Activity传输的数据
        if(ACTION_START.equals(intent.getAction())){
            FileInfo fileInfo =(FileInfo) intent.getSerializableExtra("fileInfo");
            System.out.println("2获得Activity传输的数据::>:"+fileInfo.toString());
            //启动初始化线程
          //  new InitThread(fileInfo).start();
            DowloadTask.sExecutorService.execute(new InitThread(fileInfo));
        }else if(ACTION_STOP.equals(intent.getAction())){
            FileInfo fileInfo =(FileInfo) intent.getSerializableExtra("fileInfo");
            System.out.println("fileInfo结束::>:"+fileInfo.toString());
            //从下载集合中取出下载任务
            DowloadTask task=mTasks.get(fileInfo.getId());
            //暂停下载
            if (task!=null){
                task.isPause=true;
            }
//            if(mDownloadTask!=null){
//                mDownloadTask.isPause=true;
//            }
        }

        return super.onStartCommand(intent, flags, startId);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_INIT://初始化时候
                    FileInfo fileInfo= (FileInfo) msg.obj;
                    System.out.println("test::fileInfo::>:"+fileInfo.toString());

                    System.out.println("7Handler接收信息::>:");
                    //启动下载任务
                    mDownloadTask=new DowloadTask(DowloadServices.this,fileInfo,3);
                    System.out.println("7.1Handler开始下载::>:");
                    mDownloadTask.download();
                    //把下载任务添加到集合中
                    mTasks.put(fileInfo.getId(),mDownloadTask);
                    break;
            }
        }
    };


    /**
     *定义一个初始化的子线程
     */
    class InitThread extends Thread{
        private FileInfo mfileInfo;

        public InitThread(FileInfo mfileInfo) {
            System.out.println("3开启新的线程::>:");
            this.mfileInfo = mfileInfo;
        }

        @Override
        public void run() {
            super.run();
            HttpURLConnection conn = null;
            RandomAccessFile raf=null;
            try {
                //连接诶网络文件
                System.out.println("4连接诶网络文件::>:");
                URL url = new URL(mfileInfo.getUrl());
                conn =(HttpURLConnection) url.openConnection();
                conn.setReadTimeout(8000);
                conn.setConnectTimeout(8000);
                conn.setRequestMethod("GET");

                int length=-1;
                // 并获取长度
                System.out.println("5获取长度::>:");
                if(conn.getResponseCode()==200){
                    //得到内容长度
                    length=conn.getContentLength();
                }
                if(length<=0){
                    return;
                }
                System.out.println("5.1获取长度::>:"+length);

                File dir=new File(DOWNLOAD_PATH);
                if(!dir.exists()){
                    System.out.println("6.0创建文件::>:"+dir);
                    dir.mkdir();
                }
                //在本地创建文件
                System.out.println("6创建文件::>:");
                File file=new File(dir,mfileInfo.getFileName());
//                if(file.exists()){
//                    System.out.print("该文件已经存在，请删除后再重新下载");
//                    return;
//                }
                //随时随地操作权限
                 raf=new RandomAccessFile(file,"rwd");//rwd--可读可写
                //设置文件长度
                raf.setLength(length);
                mfileInfo.setLength(length);
                //发送给Handler
                System.out.println("6.2发送Handler::>:");
                mHandler.obtainMessage(MSG_INIT,mfileInfo).sendToTarget();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    conn.disconnect();
                    //raf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
