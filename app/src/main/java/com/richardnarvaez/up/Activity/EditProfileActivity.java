package com.richardnarvaez.up.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.richardnarvaez.up.Utility.GlideUtil;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Objects;

import com.richardnarvaez.up.R;
import com.richardnarvaez.up.Utility.FirebaseUtil;

public class EditProfileActivity extends BaseActivity {

    private static int GALLERY_PICK = 101;
    private static final String CROPPED_IMAGE_NAME = "image_profile";
    private String TAG = "EditProfileActivity";

    private ImageView imageViewProfile;
    private EditText etName, etUserName, etDescription;
    DatabaseReference authorRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        imageViewProfile = findViewById(R.id.imageViewProfile);
        etName = findViewById(R.id.etName);
        etUserName = findViewById(R.id.etUserName);
        etDescription = findViewById(R.id.etDescription);


        authorRef = FirebaseUtil.getCurrentUserAuthorRef();
        if (authorRef == null) {
            Toast.makeText(this, "Hubo un error de conexion", Toast.LENGTH_SHORT).show();
            finish();
        }

        authorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GlideUtil.loadProfileIcon(dataSnapshot.child("profile_picture").getValue(String.class), imageViewProfile);
                etName.setText(dataSnapshot.child("name").getValue(String.class));
                etUserName.setText(dataSnapshot.child("user_name").getValue(String.class));
                etDescription.setText(dataSnapshot.child("description").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button btSave = findViewById(R.id.btSave);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authorRef.child("name").setValue(etName.getText().toString());
                authorRef.child("user_name").setValue(etUserName.getText().toString());
                authorRef.child("description").setValue(etDescription.getText().toString());
                finish();

            }
        });
        Button btNewImage = findViewById(R.id.btNewImage);
        btNewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(i, "Get Image"), GALLERY_PICK);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Log.e(TAG, data.getDataString());
            Uri destination = data.getData();
            if (destination != null) {
                UCrop.Options options = new UCrop.Options();
                options.setCircleDimmedLayer(true);
                options.setMaxBitmapSize(1024 / 2);
                UCrop crop = UCrop.of(destination, Uri.fromFile(new File(getCacheDir(), CROPPED_IMAGE_NAME + ".jpg")))
                        .withAspectRatio(1, 1)
                        //.setCompressionFormat(Bitmap.CompressFormat.JPEG)
                        .withMaxResultSize(512, 512);
                crop.withOptions(options);
                crop.start(EditProfileActivity.this);
            }
        }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            showProgressDialog("Estamos procesando la peticion...\n" +
                    "Espera un momento");
            if (resultUri != null) {

                final StorageReference filePath = FirebaseUtil.getCurrentUserStorageRef().child("profile_picture").child("profile_image.jpg");

                filePath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            final String fullSizeUrl = task.getResult().toString();

                            FirebaseUtil.getCurrentUserAuthorRef().child("profile_picture").setValue(fullSizeUrl);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(etName.getText().toString())
                                    .setPhotoUri(Uri.parse(fullSizeUrl))
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                dismissProgressDialog();
                                            }
                                        }
                                    });

                            //saveData();
                        }
                    }
                });
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Log.e(TAG, cropError.getMessage());
        }
    }

    public void saveData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName("Jane Q. User")
                    .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User profile updated.");
                            }
                        }
                    });
        }
    }
}
