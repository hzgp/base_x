package com.frame.base.utils.photopicker.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.frame.base.R;
import com.frame.base.utils.photopicker.adapter.PhotoPagerAdapter;
import com.frame.base.utils.photopicker.PhotoPickerActivity;
import com.frame.base.utils.photopicker.entity.Photo;

import java.util.ArrayList;
import java.util.List;


/**
 * Image Pager Fragment
 * Created by donglua on 15/6/21.
 */
public class ImagePagerFragment extends Fragment {

    public final static String ARG_PATH = "PATHS";
    public final static String ARG_CURRENT_ITEM = "ARG_CURRENT_ITEM";

    private ArrayList<Photo> photos;
    private ViewPager mViewPager;
    private PhotoPagerAdapter mPagerAdapter;

    private int currentItem = 0;


    public static ImagePagerFragment newInstance(ArrayList<Photo> photos, int currentItem) {

        ImagePagerFragment f = new ImagePagerFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PATH, photos);
        args.putInt(ARG_CURRENT_ITEM, currentItem);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof PhotoPickerActivity) {
            PhotoPickerActivity photoPickerActivity = (PhotoPickerActivity) getActivity();
            photoPickerActivity.onDoneStateChange();
        }
    }

    public void setPhotos(List<Photo> photos, int currentItem) {
        this.photos.clear();
        this.photos.addAll(photos);
        this.currentItem = currentItem;

        mViewPager.setCurrentItem(currentItem);
        mViewPager.getAdapter().notifyDataSetChanged();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        photos = new ArrayList<>();

        Bundle bundle = getArguments();

        if (bundle != null) {
            ArrayList<Photo> data = bundle.getParcelableArrayList(ARG_PATH);
            photos.clear();
            if (data != null) {
                photos = data;
            }

            currentItem = bundle.getInt(ARG_CURRENT_ITEM);
        }

        mPagerAdapter = new PhotoPagerAdapter(Glide.with(this), photos);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.picker_picker_fragment_image_pager, container, false);

        mViewPager = rootView.findViewById(R.id.vp_photos);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(currentItem);
        mViewPager.setOffscreenPageLimit(5);

        return rootView;
    }


    public ViewPager getViewPager() {
        return mViewPager;
    }


    public ArrayList<Photo> getPhotos() {
        return photos;
    }


    public ArrayList<Photo> getCurrentPath() {
        ArrayList<Photo> list = new ArrayList<>();
        int position = mViewPager.getCurrentItem();
        if (photos != null && photos.size() > position) {
            list.add(photos.get(position));
        }
        return list;
    }


    public int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        photos.clear();
        photos = null;

        if (mViewPager != null) {
            mViewPager.setAdapter(null);
        }
    }
}
