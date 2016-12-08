package com.hx.manixchen.cmablog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.LruCache;
import android.util.SizeF;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.os.Handler;
import java.util.logging.LogRecord;


public class allAppImgLoader{
    private ImageView mImageView;
    private String mUrl;
    //缓存机制
    private LruCache<String,Bitmap> mCache;

    public allAppImgLoader(){
        //获取最大可用内存
        int maxMemory= (int) Runtime.getRuntime().maxMemory();
        //
        int cacheSize=maxMemory/4;
        //设置缓存大小
        mCache=new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //在每次存入缓存实调用
                return value.getByteCount();
            }
        };
    }
    //判断是否增加到缓存
    public void addBitmapToCache(String url,Bitmap bitmap){
        if(getBitMapFromCache(url)==null){
            mCache.put(url,bitmap);
        }
    }
    //从缓存中获取数据
    public  Bitmap getBitMapFromCache(String url){
        System.out.println("cache请求资源::<>::");
        return mCache.get(url);
    }

    private Handler mHhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //设置Tag避免缓存的图片对症的图片产生影响
            if(mImageView.getTag().equals(mUrl)){
                mImageView.setImageBitmap((Bitmap) msg.obj);
            }
        }
    };

    public void showImgByThread(ImageView imageView, final String url){
        mImageView=imageView;
        mUrl=url;
        new Thread(){
            @Override
            public void run() {
                super.run();
                Bitmap bitMap = null;
                try {
                     bitMap=getBitMapFromCache(url);
                    if(bitMap==null){
                        System.out.println("网络请求资源::<>::");
                        bitMap = getBitMapFromUrl(url);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //此方式生命的Message可以使用现有的Message以及会收到的Message
                Message message = Message.obtain();
                message.obj=bitMap;
                mHhandler.sendMessage(message);
            }
        }.start();
    }

    public Bitmap getBitMapFromUrl(String urlString) throws IOException {
        Bitmap bitmap;
        InputStream is = null;

        try {
            URL url=new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            is=new BufferedInputStream(connection.getInputStream());

            bitmap= BitmapFactory.decodeStream(is);
            connection.disconnect();
            addBitmapToCache(urlString,bitmap);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            is.close();
        }

        return null;
    }
}
