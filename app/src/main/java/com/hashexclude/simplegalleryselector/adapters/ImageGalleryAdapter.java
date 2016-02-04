package com.hashexclude.simplegalleryselector.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hashexclude.simplegalleryselector.R;
import com.hashexclude.simplegalleryselector.models.PhotoItem;
import com.manuelpeinado.multichoiceadapter.MultiChoiceArrayAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by sidhantpanda on 05/02/16.
 */
public class ImageGalleryAdapter extends MultiChoiceArrayAdapter<PhotoItem> {

    private Context context;
    private int resourceId;

    private List<PhotoItem> items = new ArrayList<>();

    private ArrayList<PhotoItem> returnDetails = new ArrayList<>();

    private ImageGalleryAdapterResponse imageGalleryAdapterResponse;

    public void setImageGalleryAdapterResponse(ImageGalleryAdapter.ImageGalleryAdapterResponse imageGalleryAdapterResponse) {
        this.imageGalleryAdapterResponse = imageGalleryAdapterResponse;
    }

    public interface ImageGalleryAdapterResponse {
        void tickClicked(ArrayList<PhotoItem> returnDetails);
    }

    public ImageGalleryAdapter(Bundle savedInstanceState, Context context, int resourceId, List<PhotoItem> items) {
        super(savedInstanceState,context,resourceId,items);
        this.resourceId = resourceId;
        this.context    = context;
        this.items      = items;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_mul, menu);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.action_go) {
            Set<Long> checkedItems = this.getCheckedItems();
            for(Long currItem : checkedItems){
                //Log.d("Hello", currItem.toString());
                getItem((int) (long) currItem);
                returnDetails.add(getItem((int) (long) currItem));
            }

            if (imageGalleryAdapterResponse != null) {
                imageGalleryAdapterResponse.tickClicked(returnDetails);
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public PhotoItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * The "ViewHolder" pattern is used for speed.
     *
     * Reference: http://www.javacodegeeks.com/2013/09/android-viewholder-pattern-example.html
     */
    private class ViewHolder {
        ImageView photoImageView;
    }

    /**
     * Populate the view holder with data.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getViewImpl(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        PhotoItem photoItem = getItem(position);
        View viewToUse = null;

        // This block exists to inflate the photo list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            holder = new ViewHolder();
            viewToUse = mInflater.inflate(resourceId, null);
            holder.photoImageView = (ImageView) viewToUse.findViewById(R.id.img_galley_thumb);
            viewToUse.setTag(holder);
        } else {
            viewToUse = convertView;
            holder = (ViewHolder) viewToUse.getTag();
        }

        Glide.with(context)
                .load(photoItem.getImageUri().getPath())
                .centerCrop()
                .crossFade()
                .into(holder.photoImageView);

        return viewToUse;
    }
}