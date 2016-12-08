package com.hx.manixchen.services;

import android.content.Context;

import com.hx.manixchen.db.ThreadDaoDownLoadListImpl;
import com.hx.manixchen.db.ThreadDaoDownloadList;
import com.hx.manixchen.views.ThreadInfoDownloadList;

import java.util.HashMap;
import java.util.List;

/**
 * Created by manixchen on 2016/12/6.
 */

public class DownloadListAddTask {
    private Context mContent = null;
    private ThreadDaoDownloadList mDao;
    private HashMap getMap = null;

    public DownloadListAddTask(Context mContent, HashMap map) {
        System.out.println("10.1开启下载Task----");
        this.mContent = mContent;
        this.getMap = map;
        mDao = (ThreadDaoDownloadList) new ThreadDaoDownLoadListImpl(mContent);
        /***
         * 问题————————！！！！
         */
        // mDao= (ThreadDaoDownloadList) new ThreadDaoDownLoadListImpl(mContent,getMap);
    }

    public void download() {
        if (getMap != null) {
            System.out.println("11.1:map:---" + getMap);
        }
        try {
            //获得当前需要插入的数据
            /***/
            System.out.println("qqq:map:---" + getMap.get("app_name").toString());
             ThreadInfoDownloadList threadInfoDownloadList=new ThreadInfoDownloadList
             ( getMap.get("app_name").toString(),
             getMap.get("version").toString(),
             getMap.get("app_size").toString(),
             getMap.get("app_icon").toString(),
             getMap.get("download_address").toString(),
             111);

            List<ThreadInfoDownloadList> threadInfoDownloadLists = mDao.selsAllApp(getMap.get("app_name").toString());
            if(threadInfoDownloadLists.size()==0){
                mDao.insertApp(threadInfoDownloadList);
            }else{
                System.out.println("20.12::threadInfoDownloadLists:>" + threadInfoDownloadLists.get(0).getVersion());
            }

            //判断数据库中是否存在该App
//            boolean hasApp = mDao.isAppExists("aaa1");
//            if(!hasApp){
//                mDao.insertApp(threadInfoDownloadList);
//            }else {
//                System.out.println("22.11eee::><此逻辑不通，将下载按钮文字修改为已下载" );
//            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
