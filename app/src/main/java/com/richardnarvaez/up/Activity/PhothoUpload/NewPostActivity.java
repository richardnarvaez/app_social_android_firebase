package com.richardnarvaez.up.Activity.PhothoUpload;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;
import com.richardnarvaez.up.Activity.BaseActivity;
import com.richardnarvaez.up.Activity.PhothoUpload.Fragment.FragmentAdvanceUpload;
import com.richardnarvaez.up.Activity.PhothoUpload.Fragment.FragmentCamera;
import com.richardnarvaez.up.Activity.PhothoUpload.Fragment.FragmentTextPost;
import com.richardnarvaez.up.Activity.PhothoUpload.Fragment.FragmentGallery;
import com.richardnarvaez.up.Fragment.NewPostUploadTaskFragment;
import com.richardnarvaez.up.Utility.FirebaseUtil;
import com.richardnarvaez.up.Utility.GlideUtil;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;


import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.richardnarvaez.up.R;
import com.yalantis.ucrop.view.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class NewPostActivity extends BaseActivity implements
        EasyPermissions.PermissionCallbacks,
        NewPostUploadTaskFragment.TaskCallbacks,
        FragmentGallery.GalleryCallbacks {

    public static final String TAG = "NewPostActivity";
    public static final String TAG_TASK_FRAGMENT = "newPostUploadTaskFragment";
    public static final String TAG_TASK_UPLOAD_FRAGMENT = "UploadTaskFragment";
    private static final int THUMBNAIL_MAX_DIMENSION = 50;
    private static final int FULL_SIZE_MAX_DIMENSION = 3000;
    TextInputEditText titleText;

    private Uri mFileUri;
    private Bitmap mResizedBitmap;
    private Bitmap mThumbnail;

    private NewPostUploadTaskFragment mTaskFragment;

    private static final int TC_PICK_IMAGE = 101;
    private static final int TC_PICK_VIDEO = 103;
    private static final int RC_CAMERA_PERMISSIONS = 102;

    private static final String[] cameraPerms = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private String CROPPED_IMAGE_NAME = "image_crope";
    private ProgressDialog progress;
    private String filemanagerstring;
    private String selectedImagePath;

    Toolbar toolbar;
    AppBarLayout app;
    ViewPager viewPager;
    private ImageView imageUpload;
    private FloatingActionButton fabsetText;
    private EditText descriptionText;
    private static final String YOUTUBE_TYPE = "youtube", LINK_TYPE = "link", TEXT_TYPE = "text", LOCATE_TYPE = "locate";
    private EditText linkText;
    private EditText ciudadText;
    private ProgressDialog pd;
    private LinearLayout layoutYoutube;
    EditText postTitle, postDescription, postHastag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        app = findViewById(R.id.appbar);
        app.setVisibility(View.GONE);

        /*FabMenu*/
        fabsetText = findViewById(R.id.fabsetText);
        fabsetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText("Texto", "Escribe un titulo y descripción para el POST", TEXT_TYPE);
            }
        });


        postTitle = findViewById(R.id.postTitle);
        postDescription = findViewById(R.id.postDescription);
        postHastag = findViewById(R.id.postHastag);

        imageUpload = findViewById(R.id.imageUpload);
        viewPager = findViewById(R.id.viewpager);

        Button fbUpload = findViewById(R.id.uploadImage);
        fbUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });

        layoutYoutube = findViewById(R.id.layoutYoutube);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent);
            } else if (type.startsWith("image/")) {
                handleSendImage(intent);
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            FragmentManager fm = getSupportFragmentManager();
            setupViewPager(viewPager, fm);
            viewPager.setCurrentItem(1);
            viewPager.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    return null;
                }
            });

            mTaskFragment = (NewPostUploadTaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

            if (mTaskFragment == null) {
                mTaskFragment = new NewPostUploadTaskFragment();
                fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
            }
        }

    }

    private void handleSendMultipleImages(Intent intent) {
        Toast.makeText(this, "No disponible.", Toast.LENGTH_SHORT).show();
//        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
//        if (imageUris != null) {
//            // Update UI to reflect multiple images being shared
//        }
    }

    private void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
        }
    }

    String titleYT, authorYT, author_url;
    String id;

    private void handleSendText(Intent intent) {
        final String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null && isYouTube(sharedText)) {
            layoutYoutube.setVisibility(View.VISIBLE);
            id = extractId(sharedText);
            new JsonTask().execute("https://noembed.com/embed?url=https://www.youtube.com/watch?v=" + id);

            YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube);
            youTubePlayerView.getPlayerUIController().showFullscreenButton(false);
            youTubePlayerView.getPlayerUIController().showYouTubeButton(false);
            getLifecycle().addObserver(youTubePlayerView);
            youTubePlayerView.initialize(new YouTubePlayerInitListener() {
                @Override
                public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
                    initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady() {
                            initializedYouTubePlayer.loadVideo(id, 0);
                        }
                    });
                }
            }, true);

        } else {
            Toast.makeText(this, "El url es erroneo.", Toast.LENGTH_SHORT).show();
        }

    }

    private String extractId(String url) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url); //url is youtube url for which you want to extract the id.
        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    private boolean isYouTube(String sharedText) {
        if (Patterns.WEB_URL.matcher(sharedText).matches() && sharedText.matches(".*(youtube|youtu.be).*")) {
            return true;
        } else {
            return false;
        }
    }

    String title = "";
    String description = "";
    String link = "";
    String youtube = "";
    String ciudad = "";
    String categoria = "";


    private void setText(String titleAlert, String subtitleAlert, String TYPE) {

        View view = getLayoutInflater().inflate(R.layout.item_set_text, null);

        ((TextView) view.findViewById(R.id.title)).setText(titleAlert);
        ((TextView) view.findViewById(R.id.subtitle)).setText(subtitleAlert);

        titleText = view.findViewById(R.id.et_text_1);
        descriptionText = view.findViewById(R.id.et_text_2);
        linkText = view.findViewById(R.id.et_text_3);
        ciudadText = view.findViewById(R.id.et_text_ciudad);


        titleText.setVisibility(View.GONE);
        descriptionText.setVisibility(View.GONE);
        linkText.setVisibility(View.GONE);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        switch (TYPE) {
            case TEXT_TYPE:

                titleText.setVisibility(View.VISIBLE);
                descriptionText.setVisibility(View.VISIBLE);
                ciudadText.setVisibility(View.GONE);
                linkText.setVisibility(View.GONE);

                if (title != "") {
                    titleText.setText(title);
                    postTitle.setText(title);
                }

                if (description != "") {
                    descriptionText.setText(description);
                }

                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        title = titleText.getText().toString();
                        description = descriptionText.getText().toString();
                    }
                });

                break;
            case LINK_TYPE:

                titleText.setVisibility(View.GONE);
                descriptionText.setVisibility(View.GONE);
                ciudadText.setVisibility(View.GONE);
                linkText.setVisibility(View.VISIBLE);

                if (link != "") {
                    linkText.setText(link);
                }

                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        link = linkText.getText().toString();
                    }
                });

                break;
            case YOUTUBE_TYPE:
                titleText.setVisibility(View.GONE);
                descriptionText.setVisibility(View.GONE);
                linkText.setVisibility(View.VISIBLE);

                if (youtube != "") {
                    linkText.setText(youtube);
                }

                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        youtube = linkText.getText().toString();
                    }
                });
                break;
            case LOCATE_TYPE:
                titleText.setVisibility(View.GONE);
                descriptionText.setVisibility(View.GONE);
                linkText.setVisibility(View.GONE);
                ciudadText.setVisibility(View.VISIBLE);


                if (ciudad != "") {
                    ciudadText.setText(ciudad);
                }

                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ciudad = ciudadText.getText().toString();
                    }
                });
                break;
        }


        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.setView(view);
        dialog.show();
    }

    private void upload() {

        if (resultUri == null) {
            Toast.makeText(NewPostActivity.this, "Select an image first.",
                    Toast.LENGTH_SHORT).show();
            return;
        }


//        if (title.isEmpty()) {
//            Toast.makeText(this, "El post debe tener Titulo como minimo.", Toast.LENGTH_SHORT).show();
//            setText("Texto", "Escribe un titulo y descripción para el POST", TEXT_TYPE);
//            titleText.setError(getString(R.string.error_required_field));
//            return;
//        }


        //showProgressDialog(getString(R.string.post_upload_progress_message));

        progress = new ProgressDialog(NewPostActivity.this);
        progress.setMessage("Subiendo archivo...");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        // progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();

        imageUpload.setEnabled(false);

        Long timestamp = System.currentTimeMillis();
        Log.e(TAG, "TIME: " + timestamp);
        Log.e(TAG, "SERVERTIME: " + ServerValue.TIMESTAMP);

        String bitmapPath = "/" + FirebaseUtil.getCurrentUserId() + "/full/" + timestamp.toString() + "/";
        String thumbnailPath = "/" + FirebaseUtil.getCurrentUserId() + "/thumb/" + timestamp.toString() + "/";

        title = postTitle.getText().toString();
        description = postDescription.getText().toString();
        categoria = postHastag.getText().toString().replace("#", "").replace(" ", "_");

        progress.setIndeterminate(false);
        try {
            mTaskFragment.uploadPost(
                    MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri),
                    bitmapPath,
                    mThumbnail,
                    thumbnailPath,
                    String.valueOf(timestamp),
                    title,
                    description,
                    categoria,
                    true,
                    ciudad,
                    "");//checkPromotion.isChecked());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setupViewPager(ViewPager viewPager, FragmentManager fm) {

        Adapter adapter = new Adapter(fm);
        //adapter.addFragment(new CameraFragment.newInstance(new Configuration.Builder().build()), null);
        adapter.addFragment(new FragmentCamera(), null);
        adapter.addFragment(new FragmentGallery(), null);
        adapter.addFragment(new FragmentTextPost(), null);
        viewPager.setAdapter(adapter);

    }

    @Override
    public void onSelectedImage(String path) {
        Log.e(TAG, "Uri: " + Uri.parse(path));
        cropImage(Uri.fromFile(new File(path)));
    }

    private void cropImage(Uri destination) {

        if (destination != null) {
            Log.e(TAG, "Recortando");
            mFileUri = destination;
            UCrop crop = UCrop.of(destination, Uri.fromFile(new File(getCacheDir(), CROPPED_IMAGE_NAME + ".jpg")))
                    //.withAspectRatio(1, 1)
                    .withMaxResultSize(FULL_SIZE_MAX_DIMENSION, FULL_SIZE_MAX_DIMENSION);

            UCrop.Options options = new UCrop.Options();

            options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary));
            options.setAspectRatioOptions(0,
                    new AspectRatio("9:16", 9, 16),
                    new AspectRatio("Original", CropImageView.DEFAULT_ASPECT_RATIO, CropImageView.DEFAULT_ASPECT_RATIO),
                    new AspectRatio("Square", 1, 1),
                    new AspectRatio("Large", 3, 4)
            );

            options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
            crop.withOptions(options);
            crop.start(NewPostActivity.this);
        }

    }

    public void onBackGetImage(View view) {
        imageUpload.setImageBitmap(null);
        viewPager.setVisibility(View.VISIBLE);
    }

    public void setLink(View view) {
        setText("Link", "Pega aqui el enlace del post.", LINK_TYPE);
    }

    public void setYoutube(View view) {
        setText("YouTube", "Comparte el link del video que quieras compartir.", YOUTUBE_TYPE);
    }

    public void setLocate(View view) {
        setText("Ubicación", "Selecciona donde fue tomada la foto", LOCATE_TYPE);
    }


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void setImage(String imgURL, ImageView image, String append) {
        Log.d(TAG, "setImage: setting image");
        Log.d(TAG, "IMAGE: " + imgURL);
        GlideUtil.loadImage(imgURL, image);
        //ImageLoader imageLoader = ImageLoader.getInstance();

        /*imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });*/
    }

    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }

    @Override
    public void onPostUploaded(final String error) {
        NewPostActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageUpload.setEnabled(true);
                progress.dismiss();
                //dismissProgressDialog();
                if (error == null) {
                    Toast.makeText(NewPostActivity.this, "Post created!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(NewPostActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onPostSuccesOne() {
        Log.e(TAG, "First Upload");
        //progress.setIndeterminate(true);
        progress.setMessage("Progess...");
        progress.setProgress(0);
    }

    @AfterPermissionGranted(RC_CAMERA_PERMISSIONS)
    private void showImagePicker() {
        if (!EasyPermissions.hasPermissions(this, cameraPerms)) {
            EasyPermissions.requestPermissions(this,
                    "This sample will upload a picture from your Camera",
                    RC_CAMERA_PERMISSIONS, cameraPerms);
            return;
        }

        // Choose file storage location
        File file = new File(getExternalCacheDir(), UUID.randomUUID().toString());
        mFileUri = Uri.fromFile(file);

        // Camera
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
            cameraIntents.add(intent);
        }

        // Image Picker
        Intent pickerIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Video Picker
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(Intent.createChooser(intent, "Select Video"), TC_PICK_VIDEO);


        Intent chooserIntent = Intent.createChooser(pickerIntent,
                getString(R.string.picture_chooser_title));

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new
                Parcelable[cameraIntents.size()]));

        startActivityForResult(pickerIntent, TC_PICK_IMAGE);
    }

    Uri resultUri = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TC_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                final boolean isCamera;
                if (data.getData() == null) {
                    isCamera = true;
                } else {
                    isCamera = MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());
                }
                if (!isCamera) {
                    mFileUri = data.getData();
                }

                Log.e(TAG, "Received file uri: " + mFileUri.getPath());
                //cropImage(data.getData());
            }
        }

        if (requestCode == TC_PICK_VIDEO) {
            Uri selectedImageUri = data.getData();

            // OI FILE Manager
            filemanagerstring = selectedImageUri.getPath();
            Log.e(TAG, "XXX: " + filemanagerstring);

            // MEDIA GALLERY
            selectedImagePath = getPath(selectedImageUri);
            if (selectedImagePath != null) {

                Log.e(TAG, "Video Recivido: " + selectedImagePath);
                /*Intent intent = new Intent(getBaseContext(),
                        VideoplayAvtivity.class);
                intent.putExtra("path", selectedImagePath);
                startActivity(intent);*/

            } else {
                Log.e(TAG, "Error al recivir");
            }
        }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                Log.e(TAG, "IMAGEN CORTADA");
                mTaskFragment.resizeBitmap(resultUri, THUMBNAIL_MAX_DIMENSION);
                //mTaskFragment.resizeBitmap(resultUri, FULL_SIZE_MAX_DIMENSION);
            } else {
                Toast.makeText(this, "Error, try again please", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Log.e(TAG, cropError.getMessage());
        }

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    @Override
    public void onDestroy() {
        if (mResizedBitmap != null) {
            mTaskFragment.setSelectedBitmap(mResizedBitmap);
        }
        if (mThumbnail != null) {
            mTaskFragment.setThumbnail(mThumbnail);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getVisibility() == View.GONE) {
            viewPager.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBitmapResized(Bitmap resizedBitmap, int mMaxDimension) {
        if (resizedBitmap == null) {
            Log.e(TAG, "Couldn't resize bitmap in background task.");
            Toast.makeText(getApplicationContext(), "Couldn't resize bitmap.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (mMaxDimension == THUMBNAIL_MAX_DIMENSION) {
            mThumbnail = resizedBitmap;
            try {
                imageUpload.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri));
                viewPager.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (mMaxDimension == FULL_SIZE_MAX_DIMENSION) {
            mResizedBitmap = resizedBitmap;
        }

        if (mThumbnail != null && mResizedBitmap != null) {

            Log.e(TAG, "LISTO PARA SUBUR");
            viewPager.setVisibility(View.GONE);

        }
    }

    @Override
    public void onPostProgress(int per) {
        progress.setProgress(per);
        Log.e(TAG, getString(R.string.post_upload_progress_message) + " " + per + "%");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @SuppressLint("StaticFieldLeak")
    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(NewPostActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }

            try {
                JSONObject jObject = new JSONObject(result);

                authorYT = jObject.getString("author_name");
                titleYT = jObject.getString("title");
                author_url = jObject.getString("author_url");

                TextInputEditText editTitle = findViewById(R.id.etTitle);
                editTitle.setText(titleYT);

                Button uploadVideo = findViewById(R.id.uploadVideo);
                uploadVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference mDatabase = FirebaseUtil.getPostsRef();

                        String uid = FirebaseUtil.getCurrentUserId();
                        String key = mDatabase.push().getKey();


                        Map<String, Object> result = new HashMap<>();
                        result.put("type", "_type_youtube");
                        result.put("user_uid", uid);
                        result.put("credits", authorYT);
                        result.put("credits_url", author_url);
                        result.put("name", titleYT);
                        result.put("video", id);


                        HashMap<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(key, result);

                        mDatabase.child(key).setValue(result);
                        FirebaseUtil.getCurrentUserRef().child("post").child(key).setValue(System.currentTimeMillis());

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

}