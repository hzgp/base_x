package com.frame.base.core.http;

import com.frame.base.app.SourceApplication;
import com.frame.base.core.http.interceptor.NetCacheInterceptor;
import com.frame.base.core.http.interceptor.OfflineCacheInterceptor;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

/**
 * Created by admin on 2018/8/28.
 */

public class Utils {


  public static OkHttpClient createClient(){
      OkHttpClient.Builder builder = new OkHttpClient.Builder();
      //设置缓存
      File cacheFile =new File(SourceApplication.getAppContext().getCacheDir(), "cache");
      Cache cache =new Cache(cacheFile, 1024 * 1024 * 50); //50M
      builder.addNetworkInterceptor(new NetCacheInterceptor());
      builder.addInterceptor(new OfflineCacheInterceptor());
      builder.cache(cache);
      //设置超时
      builder.connectTimeout(10, TimeUnit.SECONDS);
      builder.readTimeout(20, TimeUnit.SECONDS);
      builder.writeTimeout(20, TimeUnit.SECONDS);
      //错误重连
      builder.retryOnConnectionFailure(true);
      //cookie认证
      builder.cookieJar(new PersistentCookieJar(new SetCookieCache(),
              new SharedPrefsCookiePersistor(SourceApplication.getAppContext())));
      builder.protocols(Collections.unmodifiableList(Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2)));
      return builder.build();
  }

}
