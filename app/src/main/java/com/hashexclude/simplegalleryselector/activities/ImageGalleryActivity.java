package com.hashexclude.simplegalleryselector.activities;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hashexclude.simplegalleryselector.R;
import com.hashexclude.simplegalleryselector.adapters.ImageGalleryAdapter;
import com.hashexclude.simplegalleryselector.models.PhotoItem;
import com.hashexclude.simplegalleryselector.tasks.ImageGalleryAsyncLoader;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageGalleryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<PhotoItem>> {

    private static final String TAG = "ImageGalleryActivity";

    private boolean isInPreviewMode = false;

    protected AbsListView mListView;

    protected ImageGalleryAdapter mAdapter;

    protected ArrayList<PhotoItem> mPhotoListItem;

    private RelativeLayout layoutImageDetails;

    private ProgressWheel progressBarLoading;

    private GridView layoutImageList;

    private ImageView imgPosterPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);

        progressBarLoading = (ProgressWheel) findViewById(R.id.progress_bar_img_details_loading);
        layoutImageList    = (GridView) findViewById(R.id.gridView);
        layoutImageDetails = (RelativeLayout) findViewById(R.id.container_image_details);
        imgPosterPreview   = (ImageView) findViewById(R.id.img_details_preview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        setTitle("Gallery");

        mListView = (AbsListView) findViewById(R.id.gridView);
        mPhotoListItem = new ArrayList<>() ;

        mAdapter = new ImageGalleryAdapter(savedInstanceState, ImageGalleryActivity.this, R.layout.view_image_gallery_thumb, mPhotoListItem);
        mAdapter.setAdapterView(mListView);

        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, final View view, int position, long id) {
                isInPreviewMode = true;
                layoutImageList.setVisibility(View.GONE);
                layoutImageDetails.setVisibility(View.VISIBLE);
                progressBarLoading.setVisibility(View.VISIBLE);
                Log.d(TAG, mAdapter.getItem(position).getImageUri().toString());
                Glide.with(ImageGalleryActivity.this)
                        .load(new File(mAdapter.getItem(position).getImageUri().toString()))
                        .listener(new RequestListener<File, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressBarLoading.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(imgPosterPreview);
                layoutImageDetails.setVisibility(View.VISIBLE);

                TextView btnSelectImage;
                btnSelectImage = (TextView) findViewById(R.id.btn_select_image);
                btnSelectImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.performLongClick();
                        isInPreviewMode = false;
                        layoutImageList.setVisibility(View.VISIBLE);
                        layoutImageDetails.setVisibility(View.GONE);
                    }
                });
            }
        });

        mAdapter.setImageGalleryAdapterResponse(new ImageGalleryAdapter.ImageGalleryAdapterResponse() {
            @Override
            public void tickClicked(ArrayList<PhotoItem> returnDetails) {
                Uri[] selectedUris = new Uri[returnDetails.size()];
                int i = 0;
                for(PhotoItem item: returnDetails) {
                    selectedUris[i] = returnDetails.get(i).getImageUri();
                    i++;
                }

                Intent returnIntent = new Intent();
                returnIntent.putExtra("selectedImages", selectedUris);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<PhotoItem>> onCreateLoader(int id, Bundle args) {
        return new ImageGalleryAsyncLoader(ImageGalleryActivity.this);
    }

    @Override
    public void onLoadFinished(Loader<List<PhotoItem>> loader, List<PhotoItem> data) {
        mPhotoListItem.clear();

        for(int i = 0; i < data.size();i++){
            PhotoItem item = data.get(i);
            mPhotoListItem.add(item);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<PhotoItem>> loader) {
        mPhotoListItem.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (isInPreviewMode) {
            layoutImageDetails.setVisibility(View.GONE);
            layoutImageList.setVisibility(View.VISIBLE);
            isInPreviewMode = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

