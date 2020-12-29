package com.jxkj.base.utils.photopicker;

import android.os.Build;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.jxkj.base.frame.R;
import com.jxkj.base.utils.photopicker.entity.Photo;
import com.jxkj.base.utils.photopicker.event.UpdatePhotoSelectEvent;
import com.jxkj.base.utils.photopicker.fragment.ImagePagerFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import static com.jxkj.base.utils.photopicker.PhotoPreview.EXTRA_CURRENT_ITEM;
import static com.jxkj.base.utils.photopicker.PhotoPreview.EXTRA_PHOTOS;
import static com.jxkj.base.utils.photopicker.PhotoPreview.EXTRA_SHOW_DELETE;


/**
 * Created by donglua on 15/6/24.
 */
public class PhotoPagerActivity extends AppCompatActivity {

    private ImagePagerFragment pagerFragment;

    private ActionBar actionBar;
    private boolean showDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.picker_activity_photo_pager);

        int currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);
        ArrayList<Photo> paths = getIntent().getParcelableArrayListExtra(EXTRA_PHOTOS);
        showDelete = getIntent().getBooleanExtra(EXTRA_SHOW_DELETE, true);

        if (pagerFragment == null) {
            pagerFragment =
                    (ImagePagerFragment) getSupportFragmentManager().findFragmentById(R.id.photoPagerFragment);
        }
        pagerFragment.setPhotos(paths, currentItem);

        Toolbar mToolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            updateActionBarTitle();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                actionBar.setElevation(25);
            }
        }
        pagerFragment.getViewPager().addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updateActionBarTitle();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (showDelete) {
            getMenuInflater().inflate(R.menu.picker_menu_preview, menu);
        }
        return true;
    }


    @Override
    public void onBackPressed() {
//
//        Intent intent = new Intent();
//        intent.putParcelableArrayListExtra(KEY_SELECTED_PHOTOS, pagerFragment.getPhotos());
//        setResult(RESULT_OK, intent);
//        finish();
        EventBus.getDefault().post(new UpdatePhotoSelectEvent(pagerFragment.getPhotos()));
        finish();
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.delete) {
            final int index = pagerFragment.getCurrentItem();

            final Photo deletedPhoto = pagerFragment.getPhotos().get(index);

            Snackbar snackbar = Snackbar.make(pagerFragment.getView(), R.string.picker_deleted_a_photo,
                    Snackbar.LENGTH_LONG);

            if (pagerFragment.getPhotos().size() <= 1) {

                // show confirm dialog
                new AlertDialog.Builder(this)
                        .setTitle(R.string.picker_confirm_to_delete)
                        .setPositiveButton(R.string.picker_yes, (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            pagerFragment.getPhotos().remove(index);
                            pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
                            onBackPressed();
                        })
                        .setNegativeButton(R.string.picker_cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                        .show();

            } else {

                snackbar.show();

                pagerFragment.getPhotos().remove(index);
                pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
            }

            snackbar.setAction(R.string.picker_undo, view -> {
                if (pagerFragment.getPhotos().size() > 0) {
                    pagerFragment.getPhotos().add(index, deletedPhoto);
                } else {
                    pagerFragment.getPhotos().add(deletedPhoto);
                }
                pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
                pagerFragment.getViewPager().setCurrentItem(index, true);
            });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateActionBarTitle() {
        if (actionBar != null) actionBar.setTitle(
                getString(R.string.picker_image_index, pagerFragment.getViewPager().getCurrentItem() + 1,
                        pagerFragment.getPhotos().size()));
    }
}
