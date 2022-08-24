package com.frame.base.utils.photopicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.frame.base.R;
import com.frame.base.utils.photopicker.entity.Photo;
import com.frame.base.utils.photopicker.utils.AndroidLifecycleUtils;

import java.io.File;
import java.util.List;


/**
 * Created by donglua on 15/6/21.
 */
public class PhotoPagerAdapter extends PagerAdapter {

    private List<Photo> photos;
    private RequestManager mGlide;

    public PhotoPagerAdapter(RequestManager glide, List<Photo> photos) {
        this.photos = photos;
        this.mGlide = glide;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Context context = container.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.picker_picker_item_pager, container, false);

        final ImageView imageView = itemView.findViewById(R.id.iv_pager);

        final String path = photos.get(position).getPath();
        final Uri uri;
        if (path.startsWith("http")) {
            uri = Uri.parse(path);
        } else {
            uri = Uri.fromFile(new File(path));
        }

        boolean canLoadImage = AndroidLifecycleUtils.canLoadImage(context);

        if (canLoadImage) {
            mGlide.setDefaultRequestOptions(new RequestOptions().dontAnimate()
                    .dontTransform()
                    .placeholder(R.mipmap.picker_ic_photo_black_48dp)
                    .error(R.mipmap.picker_ic_broken_image_black_48dp)
                    .diskCacheStrategy(DiskCacheStrategy.NONE))
                    .load(uri)
                    .thumbnail(0.1f)
                    .into(imageView);
        }

        imageView.setOnClickListener(view -> {
            if (context instanceof Activity) {
                if (!((Activity) context).isFinishing()) {
                    ((Activity) context).onBackPressed();
                }
            }
        });

        container.addView(itemView);

        return itemView;
    }


    @Override
    public int getCount() {
        return photos.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mGlide.clear((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
