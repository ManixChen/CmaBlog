package com.hx.manixchen.services;

import android.content.Context;
import android.content.Intent;

import com.hx.manixchen.db.ThreadDAO;
import com.hx.manixchen.db.ThreadDaoImpl;
import com.hx.manixchen.views.FileInfo;
import com.hx.manixchen.views.ThreadInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 下载任务类
 * Created by manixchen on 2016/12/4.
 */

public class DowloadTask {
    private Context mContent = null;
    private FileInfo mFileInfo = null;
    private ThreadDAO mDao = null;
    private int mFinished = 0;
    public boolean isPause = false;
    private int mThreadCount = 1;//线程数量
    private List<DowloadThread> mThreadList = null;//线程集合，方便管理所有

    public static ExecutorService sExecutorService = Executors.newCachedThreadPool();//线程池子


    public DowloadTask(Context mContent, FileInfo mFileInfo, int mThreadCount) {
        this.mContent = mContent;
        this.mFileInfo = mFileInfo;
        this.mThreadCount = mThreadCount;
        mDao = new ThreadDaoImpl(mContent);
    }

    public void download() {
        //读取数据库的线程信息
        List<ThreadInfo> mThreadInfos = mDao.getThreads(mFileInfo.getUrl());
        if (mThreadInfos.size() == 0) {
            //获得每个线程下载的长度
            int length = mFileInfo.getLength() / mThreadCount;
            for (int i = 0; i < mThreadCount; i++) {
                //创建不同的线程信息
                ThreadInfo threadInfo = new ThreadInfo(i, mFileInfo.getUrl(), length * i, ((i + 1) * length - 1), 0);
                if (i == mThreadCount - 1) {//最后一个除不尽
                    threadInfo.setEnd(mFileInfo.getLength());
                }
                //添加到线程集合中
                mThreadInfos.add(threadInfo);
                //向数据插入线程信息
                System.out.println("8.1向数据插入线程信息----"+threadInfo);
                mDao.insertThread(threadInfo);
            }
        }
        //启动多个线程进行下载
        mThreadList = new ArrayList<DowloadThread>();
        for (ThreadInfo info : mThreadInfos) {
            DowloadThread thread = new DowloadThread(info);
            // thread.start();
            //使用线程池子
            DowloadTask.sExecutorService.execute(thread);
            //添加线程到线程集合
            mThreadList.add(thread);
        }

    }

    /** 判断是否所有线程执行能完毕*/
    private synchronized void checkAllThreadsFinished() {
        boolean allFinished = true;
        //遍历线程集合，判断线程是否执行完毕
        for (DowloadThread thred : mThreadList) {
            if (!thred.isFinished) {
                allFinished = false;
                break;
            }
        }
        if (allFinished) {
            //删除线程信息
            System.out.println("10.01删除线程信息----");
            mDao.deleteThread(mFileInfo.getUrl());
            //发送广播通知UI下载任务结束
            Intent intent = new Intent(DowloadServices.ACTION_FINISHED);
            intent.putExtra("fileInfo", mFileInfo);//可在下载中数据库中删除该(在Dowload中实现)
            mContent.sendBroadcast(intent);
        }
    }

    //下载线程
    class DowloadThread extends Thread {
        private ThreadInfo mThreadInfo = null;
        public boolean isFinished = false;//线程是否执行完毕

        public DowloadThread(ThreadInfo mThreadInfo) {
            this.mThreadInfo = mThreadInfo;
        }

        //@Override
        public void run() {
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            InputStream input = null;
            try {
                System.out.println("8.2连接资源----");
                URL url = new URL(mThreadInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
                //找到线程位置，设置下载位置
                int start = mThreadInfo.getStart() + mThreadInfo.getFinished();//初始开始位置加上完成进度
                conn.setRequestProperty("Range", "bytes=" + start + "-" + mThreadInfo.getEnd());
                //设置文件写入位置
                File file = new File(DowloadServices.DOWNLOAD_PATH, mFileInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");//随机访问文件
                raf.seek(start);//设置文件的写入位置
                //定义广播
                Intent intent = new Intent(DowloadServices.ACTION_UPDATE);
                mFinished += mThreadInfo.getFinished();//完成的进度
                //开始下载
                System.out.println("9.0判断是否响应成功----");
                if (conn.getResponseCode() == 206) {//200完全下载，206部分下载
                    System.out.println("9返回资源码206进行软件字符流解析----");
                    //读取数据
                    input = conn.getInputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int len = -1;
                    long time = System.currentTimeMillis();
                    while ((len = input.read(buffer)) != -1) {
                        System.out.println("9.1写入文件----");
                        //写入文件
                        raf.write(buffer, 0, len);
                        //下载进度发送广播给Activity
                        mFinished += len;
                        //累加每个线程完成的进度
                        mThreadInfo.setFinished(mThreadInfo.getFinished() + len);
                        System.out.println("9.2写入文件完毕----");
                        if (System.currentTimeMillis() - time > 300) {//间隔超过500时才发送广播
                            //System.out.println("9.30准备发送广播----");
                            time = System.currentTimeMillis();
                            intent.putExtra("finished", mFinished * 100 / mFileInfo.getLength());
                            intent.putExtra("id", mFileInfo.getId());
                            System.out.println("9.31发送广播----");
                            mContent.sendBroadcast(intent);//发送广播
                        }
                        //下载暂停时把下载进度保存到数据库
                        if (isPause) {
                            System.out.println("9.50下载暂停时----");
                            // mDao.updateThread(mThreadInfo.getUrl(),mThreadInfo.getId(),mFinished);
                            //System.out.println("9.50下载暂停时----"+mThreadInfo.getUrl());
                            mDao.updateThread(mThreadInfo.getUrl(), mThreadInfo.getId(), mThreadInfo.getFinished());
                            return;
                        }
                    }
                    //标示线程执行完毕
                    isFinished = true;
                    //检查下载任务是否执行完毕
                    checkAllThreadsFinished();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.disconnect();
                    input.close();
                    raf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
