package com.jxkj.base.utils.photopicker.event;


import com.jxkj.base.utils.photopicker.entity.Photo;

import java.util.List;

/**
 * Desc:
 * Author:zhujb
 * Date:2020/5/8
 */
public class UpdatePhotoSelectEvent {
    private List<Photo> selectedPhotos;

    public UpdatePhotoSelectEvent() {

    }

    public UpdatePhotoSelectEvent(List<Photo> selectedPhotos) {
        this.selectedPhotos = selectedPhotos;
    }

    public List<Photo> getSelectedPhotos() {
        return selectedPhotos;
    }

    public void setSelectedPhotos(List<Photo> selectedPhotos) {
        this.selectedPhotos = selectedPhotos;
    }
}
