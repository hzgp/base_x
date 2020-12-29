package com.jxkj.base.core.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.jxkj.base.core.constant.BaseConstants;
import com.jxkj.base.core.http.SSLSocketFactoryUtils;

import java.io.File;
import java.io.InputStream;

import androidx.annotation.NonNull;
import okhttp3.OkHttpClient;

/**
 * Desc:
 * Author:zhujb
 * Date:2020/9/27
 */
@com.bumptech.glide.annotation.GlideModule
public class GlideModule extends AppGlideModule {
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        File externalFilesDir = context.getExternalFilesDir(null);
        if (externalFilesDir != null) {
            String diskCacheFolder = externalFilesDir.getAbsolutePath();
            builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_RGB_565));
            builder.setDiskCache(new DiskLruCacheFactory(diskCacheFolder, BaseConstants.IMAGE_CACHE_DIR, BaseConstants.IMAGE_CACHE_MAX_SIZE));
        }
    }

    @Override
    public void registerComponents(@NonNull Context context, Glide glide, @NonNull Registry registry) {
        OkHttpClient client = new OkHttpClient().newBuilder().
                sslSocketFactory(SSLSocketFactoryUtils.createSSLSocketFactory(), SSLSocketFactoryUtils
                        .createTrustAllManager())
                .hostnameVerifier(new SSLSocketFactoryUtils.TrustAllHostnameVerifier()).followRedirects(false)
                .build();
                registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
