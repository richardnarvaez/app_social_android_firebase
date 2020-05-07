package com.richardnarvaez.up.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.richardnarvaez.up.Model.Post;
import com.richardnarvaez.up.R;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.storage.UploadTask;

import com.richardnarvaez.up.Utility.FirebaseUtil;

/**
 * Created by RICHARD on 01/05/2017.
 */


public class NewPostUploadTaskFragment extends Fragment {

    private static final String TAG = "NewPostTaskFragment";

    public interface TaskCallbacks {
        void onBitmapResized(Bitmap resizedBitmap, int mMaxDimension);

        void onPostProgress(int progress);

        void onPostUploaded(String error);

        void onPostSuccesOne();
    }

    private Context mApplicationContext;
    private TaskCallbacks mCallbacks;
    private Bitmap selectedBitmap;
    private Bitmap thumbnail;

    public NewPostUploadTaskFragment() {
        // Required empty public constructor
    }

    public static NewPostUploadTaskFragment newInstance() {
        return new NewPostUploadTaskFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across config changes.
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_upload, container, false);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TaskCallbacks) {
            mCallbacks = (TaskCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TaskCallbacks");
        }
        mApplicationContext = context.getApplicationContext();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public void setSelectedBitmap(Bitmap bitmap) {
        this.selectedBitmap = bitmap;
    }

    public Bitmap getSelectedBitmap() {
        return selectedBitmap;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void resizeBitmap(Uri uri, int maxDimension) {
        LoadResizedBitmapTask task = new LoadResizedBitmapTask(maxDimension);
        task.execute(uri);
    }

    public void uploadPost(Bitmap bitmap, String inBitmapPath, Bitmap thumbnail, String inThumbnailPath,
                           String inFileName, String inPostText, String inDescription,
                           String inCategory, boolean inchekedPromotion, String ciudad, String pais) {

        UploadPostTask uploadTask = new UploadPostTask(bitmap, inBitmapPath, thumbnail, inThumbnailPath,
                inFileName, inPostText,
                inDescription, inCategory, inchekedPromotion, ciudad, pais);

        uploadTask.execute();
    }

    private class UploadPostTask extends AsyncTask<Void, Void, Void> {
        private final String pais;
        private final String ciuad;
        private WeakReference<Bitmap> bitmapReference;
        private WeakReference<Bitmap> thumbnailReference;
        private String postText;
        private String description;
        private String fileName;
        private String bitmapPath;
        private String thumbnailPath;
        private boolean isPromotion;
        private String category;
        private String product_select;
        public Long price;
        private String video;

        private UploadPostTask(Bitmap bitmap, String inBitmapPath, Bitmap thumbnail, String inThumbnailPath,
                               String inFileName, String inPostText, String _description, String inCategory,
                               boolean inchekedPromotion, String ciudad, String pais) {
            this.bitmapReference = new WeakReference<Bitmap>(bitmap);
            this.thumbnailReference = new WeakReference<Bitmap>(thumbnail);
            this.postText = inPostText;
            this.description = _description;
            this.fileName = inFileName;
            this.bitmapPath = inBitmapPath;
            this.thumbnailPath = inThumbnailPath;
            this.category = inCategory;
            this.isPromotion = inchekedPromotion;
            this.pais = pais;
            this.ciuad = ciudad;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            final Bitmap fullSize = bitmapReference.get();
            final Bitmap thumbnail = thumbnailReference.get();
            if (fullSize == null || thumbnail == null) {
                return null;
            }

            FirebaseStorage storageRef = FirebaseStorage.getInstance();
            final StorageReference productRef = storageRef.getReferenceFromUrl("gs://" + getString(R.string.google_storage_bucket));

            Long timestamp = System.currentTimeMillis();
            final StorageReference fullSizeRef = productRef.child("posts").child(FirebaseUtil.getCurrentUserId()).child("full").child(timestamp.toString()).child(fileName + ".jpg");
            final StorageReference thumbnailRef = productRef.child("posts").child(FirebaseUtil.getCurrentUserId()).child("thumb").child(timestamp.toString()).child(fileName + ".jpg");

            ByteArrayOutputStream fullSizeStream = new ByteArrayOutputStream();
            fullSize.compress(Bitmap.CompressFormat.JPEG, 90, fullSizeStream);
            byte[] bytes = fullSizeStream.toByteArray();

            fullSizeRef.putBytes(bytes).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return fullSizeRef.getDownloadUrl();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mCallbacks.onPostUploaded(mApplicationContext.getString(
                            R.string.error_upload_task_create));
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull final Task<Uri> task) {
                    if (task.isSuccessful()) {
                        final Uri fullSizeUrl = task.getResult();
                        //Uri.parse(taskSnapshot.getStorage().getDownloadUrl().toString());

                        mCallbacks.onPostSuccesOne();
                        ByteArrayOutputStream thumbnailStream = new ByteArrayOutputStream();
                        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, thumbnailStream);
                        thumbnailRef.putBytes(thumbnailStream.toByteArray()).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw Objects.requireNonNull(task.getException());
                                }
                                return thumbnailRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(Task<Uri> taskSnapshot) {
                                DatabaseReference ref = FirebaseUtil.getBaseRef();
                                DatabaseReference postsRef = FirebaseUtil.getPostsRef();

                                final String newPostKey = postsRef.push().getKey();
                                final Uri thumbnailUrl = taskSnapshot.getResult(); //Uri.parse(taskSnapshot.getStorage().getDownloadUrl().toString());

                                String user_uid = FirebaseUtil.getCurrentUserId();

                                if (user_uid == null || user_uid.isEmpty()) {
                                    Log.e(TAG, "Couldn't upload post: Couldn't get signed in user.");
                                    mCallbacks.onPostUploaded(mApplicationContext.getString(
                                            R.string.error_user_not_signed_in));
                                    return;
                                }

                                product_select = "";
                                video = "";
                                price = 0L;
                                /*Data post*/
                                assert fullSizeUrl != null;
                                assert thumbnailUrl != null;
                                Post newPost = new Post(user_uid,
                                        fullSizeUrl.toString(),
                                        fullSizeRef.toString(),
                                        thumbnailUrl.toString(),
                                        price,
                                        thumbnailRef.toString(),
                                        postText,
                                        description,
                                        ServerValue.TIMESTAMP,
                                        product_select,
                                        video,
                                        category,
                                        isPromotion,
                                        ciuad,
                                        pais
                                );
                                /**/

                                Map<String, Object> updatedUserData = new HashMap<>();

                                updatedUserData.put(FirebaseUtil.getPeoplePath() + user_uid + "/post/"
                                        + newPostKey, ServerValue.TIMESTAMP);
                                updatedUserData.put(FirebaseUtil.getPostsPath() + newPostKey,
                                        new ObjectMapper().convertValue(newPost, Map.class));

                                ref.updateChildren(updatedUserData, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError firebaseError, DatabaseReference databaseReference) {
                                        if (firebaseError == null) {
                                            mCallbacks.onPostUploaded(null);
                                        } else {
                                            Log.e(TAG, "Unable to create new post: " + firebaseError.getMessage());
                                            mCallbacks.onPostUploaded(mApplicationContext.getString(
                                                    R.string.error_upload_task_create));
                                        }
                                    }
                                });
                            }
                        })

                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mCallbacks.onPostUploaded(mApplicationContext.getString(
                                                R.string.error_upload_task_create));
                                    }
                                });
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

//            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                    long uploadBytes = taskSnapshot.getBytesTransferred();
//                    long fileSize = taskSnapshot.getTotalByteCount();
//                    Log.e(TAG, (uploadBytes / (2 * 1024)) + "/" + (fileSize / (2 * 1024)));
//                    if (fileSize != 0) {
//                        long progress = (100 * uploadBytes) / fileSize;
//                        System.out.println("Upload is " + progress + "% done");
//                        int currentprogress = (int) progress;
//                        mCallbacks.onPostProgress(currentprogress);
//                    }
//                }
//            })

//                    full
//            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                    long uploadBytes = taskSnapshot.getBytesTransferred();
//                    long fileSize = taskSnapshot.getTotalByteCount();
//                    Log.e(TAG, (uploadBytes / (1024)) + "/" + (fileSize / (1024)));
//                    if (fileSize != 0) {
//                        long progress = (100 * uploadBytes) / fileSize;
//                        int currentprogress = (int) progress;
//                        mCallbacks.onPostProgress(currentprogress);
//                    }
//                }
//            })
            return null;
        }
    }

    private class LoadResizedBitmapTask extends AsyncTask<Uri, Void, Bitmap> {
        private int mMaxDimension;

        public LoadResizedBitmapTask(int maxDimension) {
            mMaxDimension = maxDimension;
        }

        @Override
        protected Bitmap doInBackground(Uri... params) {
            Uri uri = params[0];
            if (uri != null) {
                Log.e(TAG, "Rescalado de Imagen");
                Bitmap bitmap = null;
                try {
                    Bitmap bit = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    int outWidth;
                    int outHeight;
                    int inWidth = bit.getWidth();
                    int inHeight = bit.getHeight();

                    if (inWidth > inHeight) {
                        outWidth = mMaxDimension;
                        outHeight = (inHeight * mMaxDimension) / inWidth;
                    } else {
                        outHeight = mMaxDimension;
                        outWidth = (inWidth * mMaxDimension) / inHeight;
                    }

                    bitmap = Bitmap.createScaledBitmap(bit, outWidth, outHeight, false);
                    //bitmap = decodeSampledBitmapFromFile(uri, mMaxDimension, mMaxDimension);
                } catch (Exception e) {
                    Log.e(TAG, "Can't find file to resize: " + e.getMessage());
                }
                return bitmap;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mCallbacks.onBitmapResized(bitmap, mMaxDimension);
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}