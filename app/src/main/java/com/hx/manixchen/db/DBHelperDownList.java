package com.hx.manixchen.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by manixchen on 2016/12/5.
 */

public class DBHelperDownList extends SQLiteOpenHelper{
    private static final String DB_NAME = "download.db";
    private static final int VERSION = 3;
    private static final String SQL_CREATE_DOWNLOAD_LIST = "create table if not exists download_app(_id integer primary key autoincrement," +
            "app_name text,version text,app_size text,app_icon text,download_address text,download_times integer)";
    private static final String SQL_DROP_DOWNLOAD_LIST = "drop table if exists download_app";

//    private static DBHelperDownList mDbHelper=null;
//
//    public  static DBHelperDownList getInstance(Context context){
//        if(mDbHelper==null){
//            mDbHelper=new DBHelperDownList(context);
//        }
//        return mDbHelper;
//    }

    public DBHelperDownList(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("创建app_download数据表");
        db.execSQL(SQL_CREATE_DOWNLOAD_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_DOWNLOAD_LIST);
        db.execSQL(SQL_CREATE_DOWNLOAD_LIST);
    }
}
