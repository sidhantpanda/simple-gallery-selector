# Simple Gallery Selector
A very simple gallery selector in development

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

Reach me at sidhantpanda@hashexclude.com
