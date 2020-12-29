package com.jxkj.base.utils.photopicker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jxkj.base.frame.R;
import com.jxkj.base.utils.photopicker.entity.Photo;
import com.jxkj.base.utils.photopicker.entity.PhotoDirectory;
import com.jxkj.base.utils.photopicker.event.OnItemCheckListener;
import com.jxkj.base.utils.photopicker.event.OnPhotoClickListener;
import com.jxkj.base.utils.photopicker.utils.MediaStoreHelper;
import com.jxkj.base.utils.photopicker.widget.SquareImageView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by donglua on 15/5/31.
 */
public class PhotoGridAdapter extends SelectableAdapter<PhotoGridAdapter.PhotoViewHolder> {

    private RequestManager glide;

    private OnItemCheckListener onItemCheckListener = null;
    private OnPhotoClickListener onPhotoClickListener = null;
    private View.OnClickListener onCameraClickListener = null;

    public final static int ITEM_TYPE_CAMERA = 100;
    public final static int ITEM_TYPE_PHOTO = 101;
    public final static int ITEM_TYPE_VIDEO = 102;
    private final static int COL_NUMBER_DEFAULT = 3;

    private boolean hasCamera = true;
    private boolean previewEnable = true;

    private int imageSize;
    private int columnNumber = COL_NUMBER_DEFAULT;
    private RecyclerView mRecyclerView;

    public PhotoGridAdapter(Context context, RequestManager requestManager, List<PhotoDirectory> photoDirectories) {
        this.photoDirectories = photoDirectories;
        this.glide = requestManager;
        setColumnNumber(context, columnNumber);
    }

    public PhotoGridAdapter(Context context, RequestManager requestManager, List<PhotoDirectory> photoDirectories, ArrayList<Photo> orginalPhotos, int colNum) {
        this(context, requestManager, photoDirectories);
        setColumnNumber(context, colNum);
        selectedPhotos = new ArrayList<>();
        if (orginalPhotos != null) selectedPhotos.addAll(orginalPhotos);
    }

    private void setColumnNumber(Context context, int columnNumber) {
        this.columnNumber = columnNumber;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        imageSize = widthPixels / columnNumber;
    }

    @Override
    public int getItemViewType(int position) {
        boolean showCamera = showCamera();
        return (showCamera && position == 0) ? ITEM_TYPE_CAMERA : (getCurrentPhotos().get(position - (showCamera ? 1 : 0)).isImgType() ? ITEM_TYPE_PHOTO : ITEM_TYPE_VIDEO);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (mRecyclerView == null) mRecyclerView = recyclerView;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutRes = viewType == ITEM_TYPE_VIDEO ? R.layout.gallery_video_item : R.layout.gallery_image_item;

        final View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        final PhotoViewHolder holder = new PhotoViewHolder(itemView);
        if (viewType == ITEM_TYPE_CAMERA) {
            holder.appCompatCheckBox.setVisibility(View.GONE);
            holder.ivPhoto.setScaleType(ImageView.ScaleType.CENTER);
            holder.ivPhoto.setOnClickListener(view -> {
                if (onCameraClickListener != null) {
                    onCameraClickListener.onClick(view);
                }
            });
        }
        return holder;
    }


    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType != ITEM_TYPE_CAMERA) {

            List<Photo> photos = getCurrentPhotos();
            final Photo photo;

            if (showCamera()) {
                photo = photos.get(position - 1);
            } else {
                photo = photos.get(position);
            }
            if (viewType == ITEM_TYPE_VIDEO) {
                holder.tvVideoDuration.setText(convertDuration(photo.getDuration()));
            }
            glide.load(photo.getPath())
                    .dontAnimate()
                    .error(R.mipmap.picker_ic_broken_image_black_48dp)
                    .placeholder(android.R.color.black)
                    .priority(Priority.IMMEDIATE)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(holder.ivPhoto);

            final boolean isChecked = isSelected(photo);

            holder.appCompatCheckBox.setChecked(isChecked);
            holder.ivPhoto.justSetShowShade(isChecked);

            holder.ivPhoto.setOnClickListener(view -> {
                if (onPhotoClickListener != null) {
                    int pos = holder.getAdapterPosition();
                    if (previewEnable) {
                        onPhotoClickListener.onClick(view, pos, showCamera());
                    } else {
                        holder.appCompatCheckBox.performClick();
                    }
                }
            });
            holder.appCompatCheckBox.setOnClickListener(view -> {
                int pos = holder.getAdapterPosition();
                boolean isEnable = true;

                if (onItemCheckListener != null) {
                    isEnable = onItemCheckListener.onItemCheck(pos, photo,
                            getSelectedPhotos().size() + (isSelected(photo) ? -1 : 1));
                }
                if (isEnable) {
                    toggleSelection(photo);
                    if (mRecyclerView.isComputingLayout()) {
                        mRecyclerView.post(() -> notifyItemChanged(pos));
                    } else {
                        notifyItemChanged(pos);
                    }
                }
            });
        } else {
            holder.ivPhoto.setImageResource(R.drawable.picker_camera);
        }
    }


    @Override
    public int getItemCount() {
        int photosCount =
                photoDirectories.size() == 0 ? 0 : getCurrentPhotos().size();
        if (showCamera()) {
            return photosCount + 1;
        }
        return photosCount;
    }

    private String convertDuration(long duration) {
        StringBuilder durationString = new StringBuilder();
        int second = (int) (duration / 1000);
        int min = second / 60;
        int hour = min / 60;
        if (hour > 0) durationString.append(hour).append(":");
        durationString.append(min).append(":").append(new DecimalFormat("00").format(second));
        return durationString.toString();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        public SquareImageView ivPhoto;
        public AppCompatCheckBox appCompatCheckBox;
        public TextView tvVideoDuration;
        public ImageView ivVideoFlag;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            appCompatCheckBox = itemView.findViewById(R.id.cb_media);
            ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            tvVideoDuration = itemView.findViewById(R.id.tv_video_duration);
            ivVideoFlag = itemView.findViewById(R.id.iv_video_flag);
            ivPhoto.setShade(new ColorDrawable(Color.parseColor("#92000000")));
        }
    }


    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }


    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        this.onPhotoClickListener = onPhotoClickListener;
    }


    public void setOnCameraClickListener(View.OnClickListener onCameraClickListener) {
        this.onCameraClickListener = onCameraClickListener;
    }


    public ArrayList<Photo> getSelectedPhotos() {
        ArrayList<Photo> selectedPhotoPaths = new ArrayList<>(getSelectedItemCount());
        selectedPhotoPaths.addAll(selectedPhotos);
        return selectedPhotoPaths;
    }


    public void setShowCamera(boolean hasCamera) {
        this.hasCamera = hasCamera;
    }

    public void setPreviewEnable(boolean previewEnable) {
        this.previewEnable = previewEnable;
    }

    public boolean showCamera() {
        return (hasCamera && currentDirectoryIndex == MediaStoreHelper.INDEX_ALL_PHOTOS);
    }

    @Override
    public void onViewRecycled(PhotoViewHolder holder) {
        glide.clear(holder.ivPhoto);
        super.onViewRecycled(holder);
    }
}
