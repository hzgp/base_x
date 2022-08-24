package com.frame.base.utils.photopicker;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.frame.base.R;
import com.frame.base.utils.photopicker.adapter.PhotoGridAdapter;
import com.frame.base.utils.photopicker.entity.Photo;
import com.frame.base.utils.photopicker.event.UpdatePhotoSelectEvent;
import com.frame.base.utils.photopicker.fragment.ImagePagerFragment;
import com.frame.base.utils.photopicker.utils.PhotoPickerConstants;
import com.frame.base.utils.photopicker.fragment.PhotoPickerFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class PhotoPickerActivity extends AppCompatActivity implements PhotoPickerFragment.CallBack {

    private PhotoPickerFragment pickerFragment;
    private ImagePagerFragment imagePagerFragment;
    private Button menuDoneItem;

    private int maxCount = PhotoPicker.DEFAULT_MAX_COUNT;

    /**
     * to prevent multiple calls to inflate menu
     */
    private boolean menuIsInflated = false;

    private boolean showGif = false;
    private ArrayList<Photo> originalPhotos = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean showCamera = getIntent().getBooleanExtra(PhotoPicker.EXTRA_SHOW_CAMERA, true);
        boolean showGif = getIntent().getBooleanExtra(PhotoPicker.EXTRA_SHOW_GIF, false);
        boolean previewEnabled = getIntent().getBooleanExtra(PhotoPicker.EXTRA_PREVIEW_ENABLED, true);
        int type = getIntent().getIntExtra(PhotoPickerConstants.PHOTO_TYPE,PhotoPickerConstants.TYPE_IMAGE);
        setShowGif(showGif);

        setContentView(R.layout.picker_activity_photo_picker);
        menuDoneItem = findViewById(R.id.btn_send);
        initSubmitButton();
        findViewById(R.id.picker_back).setOnClickListener((v -> onBackPressed()));
        maxCount = getIntent().getIntExtra(PhotoPicker.EXTRA_MAX_COUNT, PhotoPicker.DEFAULT_MAX_COUNT);
        int columnNumber = getIntent().getIntExtra(PhotoPicker.EXTRA_GRID_COLUMN, PhotoPicker.DEFAULT_COLUMN_NUMBER);
        originalPhotos = getIntent().getParcelableArrayListExtra(PhotoPicker.EXTRA_ORIGINAL_PHOTOS);

        pickerFragment = (PhotoPickerFragment) getSupportFragmentManager().findFragmentByTag("tag");
        if (pickerFragment == null) {
            pickerFragment = PhotoPickerFragment
                    .newInstance(showCamera, showGif, previewEnabled, columnNumber,type, maxCount, originalPhotos);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, pickerFragment, "tag")
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
        }
        int enableColor = Color.parseColor("#999999");
        pickerFragment.setCallBack(this);
        final PhotoGridAdapter photoAdapter = pickerFragment.getPhotoGridAdapter();
        photoAdapter.setOnItemCheckListener((position, photo, selectedItemCount) -> {

            menuDoneItem.setEnabled(selectedItemCount > 0);
            menuDoneItem.setTextColor(selectedItemCount > 0? Color.WHITE :enableColor);
            menuDoneItem.setOnClickListener(selectedItemCount > 0 ? (v -> sendUpdatePhotoSelectEvent()) : null);
            if (maxCount <= 1) {
                List<Photo> photos = photoAdapter.getSelectedPhotos();
                if (!photos.contains(photo)) {
                    photoAdapter.clearSelection();
                    photoAdapter.notifyDataSetChanged();
                }
                return true;
            }

            if (selectedItemCount > maxCount) {
                Toast.makeText(this, getString(R.string.picker_over_max_count_tips, maxCount),
                        LENGTH_LONG).show();
                return false;
            }
            menuDoneItem.setText(getString(R.string.picker_done_with_count, selectedItemCount, maxCount));
            return true;
        });

    }

    /**
     * Overriding this method allows us to run our exit animation first, then exiting
     * the activity when it complete.
     */
    @Override
    public void onBackPressed() {
        if (imagePagerFragment != null && imagePagerFragment.isVisible()) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            }
        } else {
            super.onBackPressed();
        }
    }


    public void addImagePagerFragment(ImagePagerFragment imagePagerFragment) {
        this.imagePagerFragment = imagePagerFragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, this.imagePagerFragment)
                .addToBackStack(null)
                .commit();
    }

    private void initSubmitButton() {
        if (!menuIsInflated) {
            // getMenuInflater().inflate(R.menu.picker_menu_picker, menu);
            // menuDoneItem = new MenuButton(this);
            if (originalPhotos != null && originalPhotos.size() > 0) {
                menuDoneItem.setEnabled(true);
                menuDoneItem.setOnClickListener((v -> sendUpdatePhotoSelectEvent()));
                menuDoneItem.setText(
                        getString(R.string.picker_done_with_count, originalPhotos.size(), maxCount));
            } else {
                menuDoneItem.setEnabled(false);
                menuDoneItem.setOnClickListener(null);
            }
            menuIsInflated = true;
        }
    }

    public void sendUpdatePhotoSelectEvent() {
        ArrayList<Photo> selectedPhotos = null;
        if (pickerFragment != null) {
            selectedPhotos = pickerFragment.getPhotoGridAdapter().getSelectedPhotos();
        }
        //当在列表没有选择图片，又在详情界面时默认选择当前图片
        if (selectedPhotos == null || selectedPhotos.isEmpty()) {
            if (imagePagerFragment != null && imagePagerFragment.isResumed()) {
                // 预览界面
                selectedPhotos = imagePagerFragment.getCurrentPath();
            }
        }
        if (selectedPhotos != null && !selectedPhotos.isEmpty()) {
            finish();
            EventBus.getDefault().post(new UpdatePhotoSelectEvent(selectedPhotos));
        }
    }

    public boolean isShowGif() {
        return showGif;
    }

    public void setShowGif(boolean showGif) {
        this.showGif = showGif;
    }

    @Override
    public void onSelectDictionaryChange(String newDictionaryName) {
        ((TextView) findViewById(R.id.picker_tv_title)).setText(newDictionaryName);
    }

    @Override
    public void onDoneStateChange() {
        if (menuIsInflated) {
            if (pickerFragment != null && pickerFragment.isResumed()) {
                List<Photo> photos = pickerFragment.getPhotoGridAdapter().getSelectedPhotos();
                int size = photos == null ? 0 : photos.size();
                menuDoneItem.setEnabled(size > 0);
                menuDoneItem.setOnClickListener(size > 0 ? (v -> sendUpdatePhotoSelectEvent()) : null);
                if (maxCount > 1) {
                    menuDoneItem.setText(getString(R.string.picker_done_with_count, size, maxCount));
                } else {
                    menuDoneItem.setText(getString(R.string.picker_done));
                }

            } else if (imagePagerFragment != null && imagePagerFragment.isResumed()) {
                //预览界面 完成总是可点的，没选就把默认当前图片
                menuDoneItem.setEnabled(true);
                menuDoneItem.setOnClickListener((v -> sendUpdatePhotoSelectEvent()));
            }
        }
    }
}
