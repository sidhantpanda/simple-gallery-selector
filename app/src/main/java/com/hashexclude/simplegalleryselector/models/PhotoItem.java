package com.hashexclude.simplegalleryselector.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sidhantpanda on 05/02/16.
 */
public class PhotoItem implements Parcelable{
    private Uri imageUri;

    public PhotoItem(){};

    public PhotoItem(Uri imageUri) {
        this.imageUri = imageUri;
    }

    protected PhotoItem(Parcel in) {
        imageUri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<PhotoItem> CREATOR = new Creator<PhotoItem>() {
        @Override
        public PhotoItem createFromParcel(Parcel in) {
            return new PhotoItem(in);
        }

        @Override
        public PhotoItem[] newArray(int size) {
            return new PhotoItem[size];
        }
    };

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(imageUri, flags);
    }
}
