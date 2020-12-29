package com.jxkj.base.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.jxkj.base.bean.TokenInfo;
import com.jxkj.base.cache.UserCache;
import com.jxkj.base.core.constant.BaseConstants;
import com.jxkj.utils.AppManager;
import com.jxkj.utils.BaseApplication;
import com.jxkj.utils.CrashHandler;
import com.jxkj.utils.OSUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

/**
 * Desc:
 * Author:zhujb
 * Date:2020/8/5
 */
public class SourceApplication extends BaseApplication implements HasActivityInjector, CrashHandler.CatchCrashCallBack {
    public static String packageName;
    @Inject
    DispatchingAndroidInjector<Activity> mAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        packageName = this.getPackageName();
        context = this;
        if (OSUtils.isUIProcess(this)) {
            packageName = this.getPackageName();
            CrashHandler.getInstance().context(this);
            CrashHandler.getInstance().init(this).setCrashCallBack(this);
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallback());
            this.initInMainProcess();
        }
        this.initInAnyProcess();
    }

    public void initInMainProcess() {
    }

    public void initInAnyProcess() {
    }


    public static SourceApplication getAppContext() {
        return (SourceApplication) context;
    }

    public static void bindAppContext(BaseApplication appContext) {
          context =appContext;
    }

    @Override
    public void onCatchCrash() {
        AppManager.getAppManager().AppExit(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return this.mAndroidInjector;
    }

    public static boolean isTokenInValid() {
        TokenInfo tokenInfo = UserCache.getTokenInfo();
        if (tokenInfo == null) {
            return false;
        }
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTime(new Date(tokenInfo.getStartTime()));
        calendar.add(Calendar.SECOND, tokenInfo.getDuration() - BaseConstants.TOKEN_VALID_OFFSET);
        return !calendar.after(Calendar.getInstance(Locale.CHINA));
    }

    private static class ActivityLifecycleCallback implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
            AppManager.getAppManager().addActivity(activity);
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity,@NonNull  Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            AppManager.getAppManager().remove(activity);
        }
    }

}
