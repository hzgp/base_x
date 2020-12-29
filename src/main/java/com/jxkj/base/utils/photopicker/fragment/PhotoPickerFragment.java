package com.jxkj.base.utils.photopicker.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.jxkj.base.frame.R;
import com.jxkj.base.utils.photopicker.PhotoPickerActivity;
import com.jxkj.base.utils.photopicker.adapter.PhotoGridAdapter;
import com.jxkj.base.utils.photopicker.adapter.PopupDirectoryListAdapter;
import com.jxkj.base.utils.photopicker.entity.Photo;
import com.jxkj.base.utils.photopicker.entity.PhotoDirectory;
import com.jxkj.base.utils.photopicker.utils.AndroidLifecycleUtils;
import com.jxkj.base.utils.photopicker.utils.ImageCaptureManager;
import com.jxkj.base.utils.photopicker.utils.MediaStoreHelper;
import com.jxkj.base.utils.photopicker.utils.PermissionsConstant;
import com.jxkj.base.utils.photopicker.utils.PermissionsUtils;
import com.jxkj.base.utils.photopicker.utils.PhotoPickerConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.jxkj.base.utils.photopicker.PhotoPicker.DEFAULT_COLUMN_NUMBER;
import static com.jxkj.base.utils.photopicker.PhotoPicker.EXTRA_PREVIEW_ENABLED;
import static com.jxkj.base.utils.photopicker.utils.MediaStoreHelper.INDEX_ALL_PHOTOS;

/**
 * Created by donglua on 15/5/31.
 */
public class PhotoPickerFragment extends Fragment {

    private ImageCaptureManager captureManager;
    private PhotoGridAdapter photoGridAdapter;

    private PopupDirectoryListAdapter listAdapter;
    //所有photos的路径
    private List<PhotoDirectory> directories;
    //传入的已选照片
    private ArrayList<Photo> originalPhotos;

    private int SCROLL_THRESHOLD = 30;
    int column;
    //目录弹出框的一次最多显示的目录数目
    public static int COUNT_MAX = 4;
    private final static String EXTRA_CAMERA = "camera";
    private final static String EXTRA_COLUMN = "column";
    private final static String EXTRA_COUNT = "count";
    private final static String EXTRA_GIF = "gif";
    private final static String EXTRA_ORIGIN = "origin";
    private ListPopupWindow listPopupWindow;
    private RequestManager mGlideRequestManager;

    public static PhotoPickerFragment newInstance(boolean showCamera, boolean showGif,
                                                  boolean previewEnable, int column, int type, int maxCount, ArrayList<Photo> originalPhotos) {
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_CAMERA, showCamera);
        args.putBoolean(EXTRA_GIF, showGif);
        args.putBoolean(EXTRA_PREVIEW_ENABLED, previewEnable);
        args.putInt(EXTRA_COLUMN, column);
        args.putInt(EXTRA_COUNT, maxCount);
        args.putInt(PhotoPickerConstants.PHOTO_TYPE, type);
        args.putParcelableArrayList(EXTRA_ORIGIN, originalPhotos);
        PhotoPickerFragment fragment = new PhotoPickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mGlideRequestManager = Glide.with(this);

        directories = new ArrayList<>();
        originalPhotos = getArguments().getParcelableArrayList(EXTRA_ORIGIN);

        column = getArguments().getInt(EXTRA_COLUMN, DEFAULT_COLUMN_NUMBER);
        boolean showCamera = getArguments().getBoolean(EXTRA_CAMERA, true);
        boolean previewEnable = getArguments().getBoolean(EXTRA_PREVIEW_ENABLED, true);
        int type = getArguments().getInt(PhotoPickerConstants.PHOTO_TYPE, PhotoPickerConstants.TYPE_IMAGE);
        showCamera = (type == PhotoPickerConstants.TYPE_IMAGE && showCamera);
        photoGridAdapter = new PhotoGridAdapter(getActivity(), mGlideRequestManager, directories, originalPhotos, column);
        photoGridAdapter.setShowCamera(showCamera);
        photoGridAdapter.setPreviewEnable(previewEnable);
        listAdapter = new PopupDirectoryListAdapter(mGlideRequestManager, directories);

        boolean showGif = getArguments().getBoolean(EXTRA_GIF);
        MediaStoreHelper.getPhotoDirs(getActivity(), showGif, type,
                dirs -> {
                    directories.clear();
                    directories.addAll(dirs);
                    update();
                });
        captureManager = new ImageCaptureManager(getActivity());
    }

    /**
     * 在主线程中刷新UI
     */
    public void update() {
        Activity attached = getActivity();
        if (attached == null || attached.isFinishing()) {
            return;
        }
        attached.runOnUiThread(() -> {
            photoGridAdapter.notifyDataSetChanged();
            listAdapter.notifyDataSetChanged();
            adjustHeight();
            if (callBack != null) {
                callBack.onSelectDictionaryChange(directories.get(0).getName());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callBack != null) {
            callBack.onDoneStateChange();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.picker_fragment_photo_picker, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.rv_photos);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(column, OrientationHelper.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(photoGridAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final Button btSwitchDirectory = rootView.findViewById(R.id.button);

        listPopupWindow = new ListPopupWindow(getActivity());
        listPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
        listPopupWindow.setAnchorView(btSwitchDirectory);
        listPopupWindow.setAdapter(listAdapter);
        listPopupWindow.setModal(true);
        listPopupWindow.setDropDownGravity(Gravity.BOTTOM);

        listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
            listPopupWindow.dismiss();

            PhotoDirectory directory = directories.get(position);
            btSwitchDirectory.setText(directory.getName());
            if (callBack != null) {
                callBack.onSelectDictionaryChange(directory.getName());
            }
            photoGridAdapter.setCurrentDirectoryIndex(position);
            photoGridAdapter.notifyDataSetChanged();
        });

        photoGridAdapter.setOnPhotoClickListener((v, position, showCamera) -> {
            final int index = showCamera ? position - 1 : position;

            ArrayList<Photo> photos = photoGridAdapter.getCurrentPhotoPaths();

            ImagePagerFragment imagePagerFragment =
                    ImagePagerFragment.newInstance(photos, index);

            ((PhotoPickerActivity) getActivity()).addImagePagerFragment(imagePagerFragment);
        });

        photoGridAdapter.setOnCameraClickListener(view -> {
            if (!PermissionsUtils.checkCameraPermission(PhotoPickerFragment.this)) return;
            if (!PermissionsUtils.checkWriteStoragePermission(PhotoPickerFragment.this)) return;
            openCamera();
        });

        btSwitchDirectory.setOnClickListener(v -> {

            if (listPopupWindow.isShowing()) {
                listPopupWindow.dismiss();
            } else if (!getActivity().isFinishing()) {
                adjustHeight();
                listPopupWindow.show();
            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Log.d(">>> Picker >>>", "dy = " + dy);
                if (Math.abs(dy) > SCROLL_THRESHOLD) {
                    mGlideRequestManager.pauseRequests();
                } else {
                    resumeRequestsIfNotDestroyed();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    resumeRequestsIfNotDestroyed();
                }
            }
        });

        return rootView;
    }

    private void openCamera() {
        try {
            Intent intent = captureManager.dispatchTakePictureIntent();
            startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ActivityNotFoundException e) {
            Log.e("PhotoPickerFragment", "No Activity Found to handle Intent", e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageCaptureManager.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            if (captureManager == null) {
                FragmentActivity activity = getActivity();
                captureManager = new ImageCaptureManager(activity);
            }

            captureManager.galleryAddPic();
            if (directories.size() > 0) {
                String path = captureManager.getCurrentPhotoPath();
                PhotoDirectory directory = directories.get(INDEX_ALL_PHOTOS);
                directory.getPhotos().add(INDEX_ALL_PHOTOS, new Photo(path.hashCode(), path));
                directory.setCoverPath(path);
                photoGridAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PermissionsConstant.REQUEST_CAMERA:
                case PermissionsConstant.REQUEST_EXTERNAL_WRITE:
                    if (PermissionsUtils.checkWriteStoragePermission(this) &&
                            PermissionsUtils.checkCameraPermission(this)) {
                        openCamera();
                    }
                    break;
            }
        }
    }

    public PhotoGridAdapter getPhotoGridAdapter() {
        return photoGridAdapter;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        captureManager.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        captureManager.onRestoreInstanceState(savedInstanceState);
        super.onViewStateRestored(savedInstanceState);
    }

    public ArrayList<Photo> getSelectedPhotos() {
        return photoGridAdapter.getSelectedPhotos();
    }

    public void adjustHeight() {
        if (listAdapter == null) return;
        int count = listAdapter.getCount();
        count = count < COUNT_MAX ? count : COUNT_MAX;
        if (listPopupWindow != null) {
            listPopupWindow.setHeight(count * getResources().getDimensionPixelOffset(R.dimen.picker_item_directory_height));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (directories == null) {
            return;
        }

        for (PhotoDirectory directory : directories) {
            directory.getPhotoPaths().clear();
            directory.getPhotos().clear();
            directory.setPhotos(null);
        }
        directories.clear();
        directories = null;
    }

    private void resumeRequestsIfNotDestroyed() {
        if (!AndroidLifecycleUtils.canLoadImage(this)) {
            return;
        }

        mGlideRequestManager.resumeRequests();
    }

    private CallBack callBack;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {
        /**
         * 更新选择的相册集
         */
        void onSelectDictionaryChange(String newDictionaryName);

        /**
         * 刷新完成按钮文案和状态
         */
        void onDoneStateChange();
    }

}
