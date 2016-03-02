# Simple Gallery Selector
A very simple gallery selector in development. Uses new Marshmallow permissions

#### Step 0
Download the library [from here](https://github.com/sidhantpanda/simple-gallery-selector/raw/master/simple-gallery-selector.aar). Add it to your libs folder.

Please add these dependencies as well
```java
compile 'com.github.manuelpeinado.multichoiceadapter:multichoiceadapter:3.0.0'
compile 'com.github.bumptech.glide:glide:3.7.0'
compile 'com.android.support:support-v4:23.1.1'
compile 'com.pnikosis:materialish-progress:1.7'
```

#### Step 1
Invoke Gallery Selector:
```java
btnTest.setOnClickListener(new View.OnClickListener() {
  @Override
  public void onClick(View v) {
    Intent i = new Intent(MainActivity.this, ImageGalleryActivity.class);
    startActivityForResult(i, IMAGE_GALLERY_RESULT);
  }
});
```

#### Step 2
Catch the result:
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  super.onActivityResult(requestCode, resultCode, data);
  if (requestCode == IMAGE_GALLERY_RESULT && resultCode == RESULT_OK) {
    ClipData clip = data.getClipData();
    if (clip != null) {
      for (int i = 0; i < clip.getItemCount(); i++) {
        Uri uri = clip.getItemAt(i).getUri();
        Log.d(TAG, "File: " + uri.getPath());
      }
    }
  }
}
```

##### Example
See [Demo App Implementation](https://github.com/sidhantpanda/demo-android-app/blob/master/app/src/main/java/com/hashexclude/testapp/MainActivity.java)

Reach me at sidhantpanda@hashexclude.com
