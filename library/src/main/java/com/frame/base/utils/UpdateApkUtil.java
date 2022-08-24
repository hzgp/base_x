package com.frame.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.frame.base.R;
import com.frame.base.app.SourceApplication;
import com.frame.base.core.http.Utils;
import com.jxkj.utils.AlertDialogUtil;
import com.jxkj.utils.AppManager;
import com.jxkj.utils.CrashHandler;
import com.jxkj.utils.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.core.content.FileProvider;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Desc:
 * Author:Zhu
 * Date:2022/7/12
 */
public class UpdateApkUtil {
    private final OkHttpClient okHttpClient;

    static class Holder{
        private static UpdateApkUtil instance=new UpdateApkUtil();
    }
    public static UpdateApkUtil getInstance() {
        return Holder.instance;
    }

    private UpdateApkUtil() {
        okHttpClient = Utils.createClient();
    }

    /**
     * @param url      下载连接
     * @param saveDir  储存下载文件的SDCard目录
     * @param listener 下载监听
     */
    public void download(final String url, final String saveDir, final OnDownloadListener listener) {
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                listener.onDownloadFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                try {
                    ResponseBody body = response.body();
                    if (body == null) {
                        listener.onDownloadFailed();
                        return;
                    }
                    is = body.byteStream();
                    long total = body.contentLength();
                    File file = new File(saveDir, getNameFromUrl(url));
                    if (file.exists())file.delete();
                    file.createNewFile();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    // 下载完成
                    listener.onDownloadSuccess(file);
                } catch (Exception e) {
                    listener.onDownloadFailed();
                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                        CrashHandler.getInstance().saveCrashInfoToFile(e);
                    }
                    try {
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                        CrashHandler.getInstance().saveCrashInfoToFile(e);
                    }
                }
            }
        });
    }


    /**
     * @param url
     * @return 从下载连接中解析出文件名
     */
    private String getNameFromUrl(String url) {
        return System.currentTimeMillis()+"_"+url.substring(url.lastIndexOf("/") + 1);
    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess(File str);

        /**
         * @param progress 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }
    private File checkDir(File target){
        if (!target.exists()){
            target.mkdirs();
        }
        return target;
    }
    private String getDownloadFile(Context context){
        File downloadFileDir= checkDir(new File(context.getExternalFilesDir(null),"download_cache"));
        return downloadFileDir.getAbsolutePath();
    }
    public void update(String url,String updateContent,String version,boolean isMust){
        Activity context = AppManager.getAppManager().getTaskTop();
        AlertDialogUtil.Builder builder =new AlertDialogUtil.Builder(context);
        View content = LayoutInflater.from(context).inflate(R.layout.layout_update_apk,null);
        Button btn = content.findViewById(R.id.download);
        Button cancel = content.findViewById(R.id.cancel);
        if (isMust) cancel.setVisibility(View.GONE);
        else cancel.setOnClickListener(v-> builder.cancel());
        ((TextView)content.findViewById(R.id.content)).setText(updateContent);
        ((TextView)content.findViewById(R.id.title)).setText("版本更新  "+version);
        btn.setOnClickListener(v ->{
            btn.setClickable(false);
                download(url, getDownloadFile(context), new OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File str) {
                installAPK(context,str);
                btn.post(()->{
                    btn.setClickable(true);
                    btn.setText("立即更新");
                    cancel.setEnabled(true);
                });
            }

            @Override
            public void onDownloading(int progress) {

                btn.post(()->{
                    btn.setText("下载中：" + progress + "%");
                    cancel.setEnabled(false);
                });
            }

            @Override
            public void onDownloadFailed() {
                btn.post(()-> {
                    btn.setClickable(true);
                    btn.setText("立即更新");
                    cancel.setEnabled(true);
                    ToastUtils.showToast(SourceApplication.getAppContext(), "下载失败");
                });

            }
        });
        });
        builder.customView(content).cancelable(false).isCircular(true).show();
    }

    /**
     * 安装APK内容
     */
    public void installAPK(Context mContext, File apkName) {
        try {
            if (!apkName.exists()) {
                return;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//安装完成后打开新版本

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判断版本大于等于7.0
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // 给目标应用一个临时授权
                //如果SDK版本>=24，即：Build.VERSION.SDK_INT >= 24，使用FileProvider兼容安装apk
                String packageName = mContext.getApplicationContext().getPackageName();
                String authority = packageName + ".provider";
                Uri apkUri = FileProvider.getUriForFile(mContext, authority, apkName);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(apkName), "application/vnd.android.package-archive");
            }
            mContext.startActivity(intent);
            //android.os.Process.killProcess(android.os.Process.myPid());//安装完之后会提示”完成” “打开”。

        } catch (Exception e) {
            CrashHandler.getInstance().saveCrashInfoToFile(e);
        }
    }

}