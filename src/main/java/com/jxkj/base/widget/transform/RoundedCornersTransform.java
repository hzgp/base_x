package com.jxkj.base.widget.transform;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import androidx.annotation.NonNull;

import com.bumptech.glide.BuildConfig;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * Created by DY on 2019/10/22.
 */

public class RoundedCornersTransform extends BitmapTransformation {
    private float mRadius;
    private static final int VERSION = 1;
    private static final String ID = BuildConfig.APPLICATION_ID+"GlideRoundedCornersTransform." + VERSION;
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);
    private int flag;
    public final static int NORMAL=1;
    public final static int CENTER_CROP=2;

    public RoundedCornersTransform(float radius) {
        mRadius = radius;//dp ->px
    }
    public RoundedCornersTransform(float radius, int flag) {
        mRadius = radius;//dp ->px
        this.flag = flag;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool,@NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return roundCrop(pool, toTransform,  outWidth,  outHeight);
    }


    public Bitmap roundCrop(BitmapPool pool, Bitmap source, int outWidth, int outHeight) {
        if (source == null) {
            return null;
        }
        Bitmap result = pool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0,outWidth, outHeight);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, mRadius,mRadius, paint);
        int width = source.getWidth();
        int height = source.getHeight();
        if (flag == NORMAL) {
            float scale = Math.min(outWidth * 1.0f / width, outHeight * 1.0f / height);
            Matrix matrix = new Matrix();
            matrix.setScale(scale, scale);
            Bitmap bitmap = Bitmap.createBitmap(source, 0, 0, width, height, matrix, false);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
        }else if (flag == CENTER_CROP){
            float scale = Math.max(outWidth * 1.0f / width, outHeight * 1.0f / height);
            Matrix matrix = new Matrix();
            matrix.setScale(scale, scale);
            int offsetX= (int) ((width*scale-outWidth)/2);
            int offsetY=(int) ((height*scale-outHeight)/2);
            Bitmap bitmap = Bitmap.createBitmap(source, offsetX, offsetY, width-(2*offsetX), height-(2*offsetY), matrix, false);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
        }
        return result;
    }


    @Override
    public boolean equals(Object o) {
        return o instanceof RoundedCornersTransform;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }
}