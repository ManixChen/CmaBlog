package com.hx.manixchen.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库帮助类，主要创建数据库
 * Created by manixchen on 2016/12/3.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "download.db";
    private static final int VERSION = 4;
    private static final String SQL_CREATE = "create table thread_info(_id integer primary key autoincrement," +
            "thread_id integer,url text,start integer,end integer,finished integer)";
    private static final String SQL_DROP = "drop table if exists thread_info";

    private static DBHelper sHelper = null;//静态对象引用


    //单列模式,不能使用new
    private DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    /**
     * 获得这个类的对象
     */
    public static DBHelper getInstance(Context context) {
        if (sHelper == null) {//永远只会创建一个
            sHelper = new DBHelper(context);//私有的构造方法
        }
        return  sHelper;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    //数据库升级
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        db.execSQL(SQL_CREATE);
    }
}
