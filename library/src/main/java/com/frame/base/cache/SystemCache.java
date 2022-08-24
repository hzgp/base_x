package com.frame.base.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.frame.base.core.constant.BaseConstants;
import com.jxkj.utils.BaseApplication;


public class SystemCache {

    public static void setVersion(String version) {
        SharedPreferences mPreferences = BaseApplication.getContext().getSharedPreferences(BaseConstants.MY_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        mPreferences.edit().putString(BaseConstants.VERSION, version).apply();
    }

    public static String getVersion() {
        SharedPreferences mPreferences = BaseApplication.getContext().getSharedPreferences(BaseConstants.MY_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return mPreferences.getString(BaseConstants.VERSION,"");
    }

    public static void setSysConfig(String sysConfig) {
        SharedPreferences mPreferences = BaseApplication.getContext().getSharedPreferences(BaseConstants.MY_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        mPreferences.edit().putString(BaseConstants.CONFIG, sysConfig).apply();
    }

    public static String getSysConfig() {
        SharedPreferences mPreferences = BaseApplication.getContext().getSharedPreferences(BaseConstants.MY_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return mPreferences.getString(BaseConstants.CONFIG,"");
    }

    public static void setHisDocSearch(String hisDocSearch) {
        SharedPreferences mPreferences = BaseApplication.getContext().getSharedPreferences(BaseConstants.MY_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        mPreferences.edit().putString(BaseConstants.HisDocSearch, hisDocSearch).apply();
    }

    public static String getHisDocSearch() {
        SharedPreferences mPreferences = BaseApplication.getContext().getSharedPreferences(BaseConstants.MY_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return mPreferences.getString(BaseConstants.HisDocSearch,"");
    }

    public static void setHXMessageCacheStatus(boolean hasRead) {
        SharedPreferences mPreferences = BaseApplication.getContext().getSharedPreferences(BaseConstants.MY_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        mPreferences.edit().putBoolean(BaseConstants.HX_MESSAGE_CACHE_STATUS, hasRead).apply();
    }

    public static boolean hxMessageCacheHasRead() {
        SharedPreferences mPreferences = BaseApplication.getContext().getSharedPreferences(BaseConstants.MY_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return mPreferences.getBoolean(BaseConstants.HX_MESSAGE_CACHE_STATUS,false);
    }
}
