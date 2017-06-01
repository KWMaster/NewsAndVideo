package com.lvlw.myapp.shareprefrence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lvlw.myapp.entity.User;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Wantrer on 2017/3/29 0029.
 */

public class ListDataSave {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public ListDataSave(Context mContext) {
        preferences=PreferenceManager.getDefaultSharedPreferences(mContext);
//        preferences = mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        editor = preferences.edit();

}
    public void clear(Context mContext,String tag){
        preferences=PreferenceManager.getDefaultSharedPreferences(mContext);
        //        preferences = mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.remove(tag);
        editor.apply();
    }

    /**
     * 保存List
     * @param tag
     * @param datalist
     */
    public void setDataList(String tag, List<User> datalist,String tag2, List<User> datalist2) {
        if (null == datalist || datalist.size() <= 0)
            return;

        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        String strJson2 = gson.toJson(datalist2);
        editor.clear();
        editor.putString(tag, strJson);
        editor.putString(tag2, strJson2);
        editor.commit();

    }
    /**
     * 获取List
     * @param tag
     * @return
     */
    public  List<User> getDataList(String tag) {
        List<User> datalist=new ArrayList<>();
        String strJson = preferences.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<User>>() {
        }.getType());
        return datalist;

    }
}

