package com.hx.manixchen.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hx.manixchen.views.ThreadInfo;
import com.hx.manixchen.views.ThreadInfoDownloadList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by manixchen on 2016/12/6.
 */

public class ThreadDaoDownLoadListImpl implements ThreadDaoDownloadList{
    private DBHelperDownList mDBHelperList=null;
    private HashMap mHasMap=null;

    public ThreadDaoDownLoadListImpl(Context context) {

//        mDBHelperList=DBHelperDownList.getInstance(context);
        mDBHelperList=new DBHelperDownList(context);
        System.out.println("11.2:getMap:---getHasMap");
    }
//app_name,version,app_size,app_icon,download_address,download_times
    //线程安全synchronized

    @Override
    public void insertApp(ThreadInfoDownloadList threadInfoDownloadList) {
        SQLiteDatabase database=mDBHelperList.getWritableDatabase();
        System.out.println("12.1:threadInfoDownloadList:"+threadInfoDownloadList);

        database.execSQL("insert into download_app(app_name,version,app_size," +
                        "app_icon,download_address,download_times) values(?,?,?,?,?,?)",
                new Object[]{threadInfoDownloadList.getAppName(),threadInfoDownloadList.getVersion(),
                        threadInfoDownloadList.getAppSize(),threadInfoDownloadList.getAppIcon(),
                        threadInfoDownloadList.getDownloadAddress(),threadInfoDownloadList.getDownloadTimes()}
//                new Object[]{getHashMAP.get("app_name"),getHashMAP.get("version"),
//                            getHashMAP.get("app_size"),getHashMAP.get("app_icon"),
//                            getHashMAP.get("download_address"),getHashMAP.get("download_times")}
        );
        System.out.println("download_app数据插入成功");
        database.close();
    }

    @Override
    public void deleteApp(String app_name) {
        SQLiteDatabase database=mDBHelperList.getWritableDatabase();
        database.execSQL(
                "delete from download_app where app_name=?",
                new Object[]{app_name});
        database.close();
    }

    //删除所有的
    @Override
    public void deleteALL() {
        SQLiteDatabase database=mDBHelperList.getWritableDatabase();
        database.execSQL("delete from download_app" );
        database.close();
    }

    @Override
    public void updateApp(String app_name) {
        SQLiteDatabase database=mDBHelperList.getWritableDatabase();
        database.execSQL(
                "update download_app set download_times=?where app_name=?",
                new Object[]{app_name});
        database.close();
    }

    @Override
    public List<ThreadInfoDownloadList> selsAllApp(String app_name) {
        ArrayList<ThreadInfoDownloadList> threadInfoDownloadLists = new ArrayList<ThreadInfoDownloadList>();

        SQLiteDatabase database=mDBHelperList.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from download_app where app_name=?", new String[]{app_name});
        System.out.println("查找结果集");
        while (cursor.moveToNext()){
//app_name,version,app_size,app_icon,download_address,download_times
            String app_nameS = cursor.getString(cursor.getColumnIndex("app_name"));
            String app_versionS = cursor.getString(cursor.getColumnIndex("version"));
            String app_sizeS = cursor.getString(cursor.getColumnIndex("app_size"));
            String app_app_iconS = cursor.getString(cursor.getColumnIndex("app_icon"));
            String app_download_addressS = cursor.getString(cursor.getColumnIndex("download_address"));
            String app_download_timesS = cursor.getString(cursor.getColumnIndex("download_times"));
            System.out.println("查询结果集app_name1：："+app_nameS+",>"+app_versionS+",>"+app_sizeS+",>"+app_app_iconS
                    +",>"+app_download_addressS+",>"+app_download_timesS);
            ThreadInfoDownloadList threadInfoDownloadList=new ThreadInfoDownloadList();
            threadInfoDownloadList.setAppName(cursor.getString(cursor.getColumnIndex("app_name")));
            threadInfoDownloadList.setVersion(cursor.getString(cursor.getColumnIndex("version")));
            threadInfoDownloadList.setAppSize(cursor.getString(cursor.getColumnIndex("app_size")));
            threadInfoDownloadList.setAppIcon(cursor.getString(cursor.getColumnIndex("app_icon")));
            threadInfoDownloadList.setDownloadAddress(cursor.getString(cursor.getColumnIndex("download_address")));
            threadInfoDownloadList.setDownloadTimes(cursor.getInt(cursor.getColumnIndex("download_times")));
            threadInfoDownloadLists.add(threadInfoDownloadList);
        }
        database.close();
        return  threadInfoDownloadLists;
    }

    /**
     * 查询所有下载中App
     * @return
     */
    @Override
    public List<ThreadInfoDownloadList> selAllApp() {
        ArrayList<ThreadInfoDownloadList> threadInfoDownloadLists = new ArrayList<ThreadInfoDownloadList>();
        SQLiteDatabase database=mDBHelperList.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from download_app",null);
        System.out.println("查找结果集");
        while (cursor.moveToNext()){
            String app_nameS = cursor.getString(cursor.getColumnIndex("app_name"));
            String app_versionS = cursor.getString(cursor.getColumnIndex("version"));
            String app_sizeS = cursor.getString(cursor.getColumnIndex("app_size"));
            String app_app_iconS = cursor.getString(cursor.getColumnIndex("app_icon"));
            String app_download_addressS = cursor.getString(cursor.getColumnIndex("download_address"));
            String app_download_timesS = cursor.getString(cursor.getColumnIndex("download_times"));
            System.out.println("查询结果集app_name1：："+app_nameS+",>"+app_versionS+",>"+app_sizeS+",>"+app_app_iconS
                    +",>"+app_download_addressS+",>"+app_download_timesS);
            ThreadInfoDownloadList threadInfoDownloadList=new ThreadInfoDownloadList();
            threadInfoDownloadList.setAppName(cursor.getString(cursor.getColumnIndex("app_name")));
            threadInfoDownloadList.setVersion(cursor.getString(cursor.getColumnIndex("version")));
            threadInfoDownloadList.setAppSize(cursor.getString(cursor.getColumnIndex("app_size")));
            threadInfoDownloadList.setAppIcon(cursor.getString(cursor.getColumnIndex("app_icon")));
            threadInfoDownloadList.setDownloadAddress(cursor.getString(cursor.getColumnIndex("download_address")));
            threadInfoDownloadList.setDownloadTimes(cursor.getInt(cursor.getColumnIndex("download_times")));
            threadInfoDownloadLists.add(threadInfoDownloadList);
        }
        database.close();
        return  threadInfoDownloadLists;
    }

    @Override
    public boolean isAppExists(String app_name) {
        SQLiteDatabase database=mDBHelperList.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from download_app where app_name=?",
                new String[]{app_name});
        boolean exists=cursor.moveToNext();
        cursor.close();
        database.close();
        return exists;
    }



}
