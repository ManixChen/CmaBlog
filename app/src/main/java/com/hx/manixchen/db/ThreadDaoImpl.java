package com.hx.manixchen.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hx.manixchen.views.ThreadInfo;

import java.util.ArrayList;
import java.util.List;

/**数据访问接口实现
 * Created by manixchen on 2016/12/4.
 */

public class ThreadDaoImpl implements ThreadDAO {
    private DBHelper mHelper=null;

    public ThreadDaoImpl(Context context) {
        mHelper = DBHelper.getInstance(context);
    }
    //线程安全synchronized
    @Override
    public synchronized void insertThread(ThreadInfo threadInfo) {
        SQLiteDatabase database=mHelper.getWritableDatabase();
        database.execSQL(
                "insert into thread_info(thread_id,url,start,end,finished) values(?,?,?,?,?)",
                new Object[]{threadInfo.getId(),threadInfo.getUrl(),threadInfo.getStart(),threadInfo.getEnd(),threadInfo.getFinished()});
        database.close();
    }

    @Override
    public synchronized void deleteThread(String url) {
        SQLiteDatabase database=mHelper.getWritableDatabase();
        database.execSQL(
                "delete from thread_info where url=?",
                new Object[]{url});
        database.close();
    }

    @Override
    public synchronized void updateThread(String url, int thread_id, int finished) {
        SQLiteDatabase database=mHelper.getWritableDatabase();
        database.execSQL(
                "update thread_info set finished=?where url=? and thread_id =?",
                new Object[]{finished,url,thread_id});
        database.close();
    }

    @Override
    public List<ThreadInfo> getThreads(String url) {
        SQLiteDatabase database=mHelper.getReadableDatabase();
        List<ThreadInfo> list=new ArrayList<ThreadInfo>();
        Cursor cursor = database.rawQuery("select * from thread_info where url=?", new String[]{url});

        while (cursor.moveToNext()){
            ThreadInfo thread=new ThreadInfo();
            thread.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
            thread.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            thread.setStart(cursor.getInt(cursor.getColumnIndex("start")));
            thread.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
            thread.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            list.add(thread);
        }
        cursor.close();
        database.close();
        return list;
    }

    @Override
    public boolean isExists(String url, int thread_id) {
        SQLiteDatabase database=mHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from thread_info where url=? and thread_id=?",
                new String[]{url,thread_id+""});
        boolean exists=cursor.moveToNext();
        cursor.close();
        database.close();
        return exists;
    }
}
