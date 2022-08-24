package com.frame.base.utils.photopicker.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by donglua on 15/6/30.
 */
public class Photo implements Parcelable {

  private int id;
  private String path;
  private long duration;
  private long adddate;
  private String mimetype;
  private boolean fullImage;//是否使用原图，默认图片传送需要进过压缩。
  public Photo(int id, String path) {
    this.id = id;
    this.path = path;
  }

  public Photo() {
  }

  protected Photo(Parcel in) {
    id = in.readInt();
    path = in.readString();
    duration = in.readLong();
    adddate = in.readLong();
    fullImage = in.readByte() != 0;
    mimetype=in.readString();
  }

  public static final Creator<Photo> CREATOR = new Creator<Photo>() {
    @Override
    public Photo createFromParcel(Parcel in) {
      return new Photo(in);
    }

    @Override
    public Photo[] newArray(int size) {
      return new Photo[size];
    }
  };

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Photo)) return false;

    Photo photo = (Photo) o;

    return id == photo.id;
  }

  @Override public int hashCode() {
    return id;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setAdddate(long adddate) {
    this.adddate = adddate;
  }

  public long getAdddate() {
    return adddate;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public long getDuration() {
    return duration;
  }

  public void setFullImage(boolean fullImage) {
    this.fullImage = fullImage;
  }

  public boolean isFullImage() {
    return fullImage;
  }

  public boolean isImgType(){
    return TextUtils.isEmpty(mimetype)||mimetype.contains("image");
  }

  public String getMimetype() {
    return mimetype;
  }

  public void setMimetype(String mimetype) {
    this.mimetype = mimetype;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(id);
    dest.writeString(path);
    dest.writeLong(duration);
    dest.writeLong(adddate);
    dest.writeByte((byte) (fullImage ? 1 : 0));
    dest.writeString(mimetype);
  }


}
