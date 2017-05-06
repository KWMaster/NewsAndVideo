package com.lvlw.myapp.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by w9859 on 2017/3/14.
 */
public class MyAppImageCache implements ImageLoader.ImageCache{
    private LruCache<String,Bitmap> mCache;

    public MyAppImageCache() {
        mCache = new LruCache<String,Bitmap>((int) (Runtime.getRuntime().maxMemory()/10)){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    @Override
    public Bitmap getBitmap(String s) {
        return mCache.get(s);
    }

    @Override
    public void putBitmap(String s, Bitmap bitmap) {
        if (null==getBitmap(s)){
            mCache.put(s,bitmap);
        }
    }
}
