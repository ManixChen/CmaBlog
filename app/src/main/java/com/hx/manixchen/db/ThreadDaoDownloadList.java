package com.hx.manixchen.db;



import com.hx.manixchen.views.ThreadInfoDownloadList;

import java.util.HashMap;
import java.util.List;

/**
 * Created by manixchen on 2016/12/6.
 */

public interface ThreadDaoDownloadList {
    //插入下载信息
    public void  insertApp(ThreadInfoDownloadList threadInfoDownloadList);

    /**
     * 删除线程
     */
    public void  deleteApp(String app_name);

    /**
     * 删除所有线程
     */
    public  void deleteALL();

    /**
     * 更新线程下载进度
     */
    public void  updateApp(String app_name);

    /**
     * 查询对应app信息
     */
    public List<ThreadInfoDownloadList> selsAllApp(String app_name);

    /***
     * 查询所有下载中app
     */
    public List<ThreadInfoDownloadList> selAllApp();
    /**
     * 判断线程信息是否存在
     */
    public  boolean isAppExists(String app_name);
}
