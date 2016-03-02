package com.hashexclude.simplegalleryselector.activities;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
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

public class ImageGalleryActivity extends Activity implements LoaderManager.LoaderCallbacks<List<PhotoItem>> {

    private static final String TAG = "ImageGalleryActivity";

    private final String[] PermissionsStorage = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private final int RequestStorageId = 0;

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

                ClipData clip = null;
                for (PhotoItem item : returnDetails) {
                    if (clip == null) {
                        clip = new ClipData("Paths", new String[]{},
                                new ClipData.Item(item.getImageUri()));
                    } else {
                        clip.addItem(new ClipData.Item(item.getImageUri()));
                    }
                }

                Intent returnIntent = new Intent();
                returnIntent.setClipData(clip);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PermissionsStorage, RequestStorageId);
        } else {
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RequestStorageId:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    getLoaderManager().initLoader(0, null, this);
                } else {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Permission Denied");
                    builder.setMessage("We don't have permission to access your photos. Please provide permission in order to select files from your gallery.");
                    builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            ImageGalleryActivity.this.startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                break;
        }
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

