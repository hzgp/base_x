package com.jxkj.base.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.jxkj.base.bean.IToken;
import com.jxkj.base.bean.TokenInfo;
import com.jxkj.base.core.constant.BaseConstants;
import com.jxkj.utils.BaseApplication;


public class UserCache {

    public static void setPushToken(String pushToken) {
        putString(BaseConstants.PUSH_TOKEN, pushToken);
    }

    public static String getPushToken() {
        return getString(BaseConstants.PUSH_TOKEN);
    }


    public static void setUserInfo(String userInfo) {
        putString(BaseConstants.USER_INFO, userInfo);
    }

    public static void cacheLogin(IToken token) {
        setTokenInfo(System.currentTimeMillis(), token.getExpiresIn());
        setToken(token.getToken());
        setLogStatus(true);
    }

    public static String getUserInfo() {
        return getString(BaseConstants.USER_INFO);
    }

    private static void setToken(String token) {
        putString(BaseConstants.TOKEN, token);
    }

    public static String getToken() {
        return getString(BaseConstants.TOKEN);
    }

    private static void setTokenInfo(long startTime, int duration) {
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setStartTime(startTime);
        tokenInfo.setDuration(duration);
        putString(BaseConstants.TOKEN_INFO, new Gson().toJson(tokenInfo));
    }

    public static TokenInfo getTokenInfo() {
        return new Gson().fromJson(getString(BaseConstants.TOKEN_INFO), TokenInfo.class);
    }

    public static boolean getLogStatus() {
        return cachePreferences().getBoolean(BaseConstants.LOGIN_STATUS, false);
    }

    private static void setLogStatus(boolean login) {
        cachePreferences().edit().putBoolean(BaseConstants.LOGIN_STATUS, login).apply();
    }

    public static void clearCache() {
        SharedPreferences mPreferences = cachePreferences();
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(BaseConstants.TOKEN, "");
        editor.putBoolean(BaseConstants.LOGIN_STATUS, false);
        editor.putString(BaseConstants.USER_INFO, "");
        editor.putString(BaseConstants.TOKEN_INFO, "");
        editor.apply();
    }

    private static String getString(String key) {
        return cachePreferences().getString(key, "");
    }

    private static String getString(String key, String defValue) {
        return cachePreferences().getString(key, defValue);
    }

    private static void putString(String key, String value) {
        cachePreferences().edit().putString(key, value).apply();
    }

    private static SharedPreferences cachePreferences() {
        return BaseApplication.getContext().getSharedPreferences(BaseConstants.MY_SHARED_PREFERENCE, Context.MODE_PRIVATE);
    }
}
