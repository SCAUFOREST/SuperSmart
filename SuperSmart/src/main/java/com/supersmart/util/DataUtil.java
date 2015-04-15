package com.supersmart.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by forest on 2015/3/10.
 */
public class DataUtil {
    private  static Context mContext ;

    /**
     * init DataUtil
     * @param context application context suggested !
     */
    public static void init(Context context){
        mContext = context ;
    }

    /**
     * get all SharedPreferences data by name
     * @return all SharedPreferences data record by given name
     */
    public static HashMap<String , String> getSharedPreferencesData(final String name){
        SharedPreferences sp = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        return (HashMap<String, String>) sp.getAll();
    }

    /**
     * record user data when login
     * @param key username
     * @param value user's password
     */
    @SuppressLint("NewApi")
    public  static void recordSharedPreferencesData(String name,String key, String value) {
        SharedPreferences sp = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key , value);
        editor.commit();
    }

    /**
     * remove user login history by username
     * @param name user to be removed
     */
    public static void removeUserData(String name , String key){
        SharedPreferences sp = mContext.getSharedPreferences(name,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }
}
