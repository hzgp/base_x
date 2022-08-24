package com.frame.base.app;

import android.app.Activity;
import android.os.Bundle;

import com.frame.base.bean.TokenInfo;
import com.frame.base.cache.UserCache;
import com.frame.base.core.constant.BaseConstants;
import com.frame.base.event.ProcessUpdateEvent;
import com.jxkj.utils.AppManager;
import com.jxkj.utils.BaseApplication;
import com.jxkj.utils.CrashHandler;
import com.jxkj.utils.OSUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * Desc:
 * Author:zhujb
 * Date:2020/8/5
 */
public class SourceApplication extends BaseApplication implements  CrashHandler.CatchCrashCallBack {
    public static String packageName;
    public int activeCount=0;
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
            SourceApplication.getAppContext().activeCount++;
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            SourceApplication application=SourceApplication.getAppContext();
            application .activeCount--;
                EventBus.getDefault().post(new ProcessUpdateEvent(OSUtils.isBackground(application)));
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
