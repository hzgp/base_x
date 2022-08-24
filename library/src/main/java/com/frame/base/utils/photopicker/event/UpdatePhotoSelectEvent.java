package com.frame.base.utils.photopicker.event;


import com.frame.base.utils.photopicker.entity.Photo;

import java.util.ArrayList;

/**
 * Desc:
 * Author:zhujb
 * Date:2020/5/8
 */
public class UpdatePhotoSelectEvent {
    private ArrayList<Photo> selectedPhotos;

    public UpdatePhotoSelectEvent() {

    }

    public UpdatePhotoSelectEvent(ArrayList<Photo> selectedPhotos) {
        this.selectedPhotos = selectedPhotos;
    }

    public ArrayList<Photo> getSelectedPhotos() {
        return selectedPhotos==null?new ArrayList<>():selectedPhotos;
    }

    public void setSelectedPhotos(ArrayList<Photo> selectedPhotos) {
        this.selectedPhotos = selectedPhotos;
    }
}
