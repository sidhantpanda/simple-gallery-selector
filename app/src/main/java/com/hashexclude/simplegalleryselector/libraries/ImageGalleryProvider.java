package com.hashexclude.simplegalleryselector.libraries;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.hashexclude.simplegalleryselector.models.PhotoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sidhantpanda on 05/02/16.
 */
public class ImageGalleryProvider {
    /**
     * Fetch full sized images
     * Returns all images
     * @param context
     * @return
     */
    public static List<PhotoItem> getPhotoItemsList(Context context){

        final String[] projection = {MediaStore.Images.Media.DATA};

        Cursor imagesCursor = context.getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, // Which columns to return
                null,       // Return all rows
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC");

        // Extract the proper column thumbnails
        int dataColumnIndex = imagesCursor.getColumnIndex(MediaStore.Images.Media.DATA);
        ArrayList<PhotoItem> result = new ArrayList<>(imagesCursor.getCount());

        if (imagesCursor.moveToFirst()) {
            do {
                int imageId = imagesCursor.getInt(dataColumnIndex);
                String imagePath = imagesCursor.getString(imageId);
                Uri imageUri = Uri.parse(imagePath);

                // Create the list item.
                PhotoItem newItem = new PhotoItem(imageUri);
                result.add(newItem);
            } while (imagesCursor.moveToNext());
        }
        imagesCursor.close();
        return result;
    }
}