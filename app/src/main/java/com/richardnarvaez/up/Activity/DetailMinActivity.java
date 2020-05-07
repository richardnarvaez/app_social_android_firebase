package com.richardnarvaez.up.Activity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.richardnarvaez.up.Constants;
import com.richardnarvaez.up.Utility.FirebaseUtil;
import com.richardnarvaez.up.Utility.GlideUtil;
import com.richardnarvaez.up.Utility.NeftyUtil;

import com.richardnarvaez.up.R;

import java.util.Objects;

import xyz.klinker.android.drag_dismiss.activity.DragDismissActivity;

public class DetailMinActivity extends AppCompatActivity {

    public static final String URL_TITLE = "title";
    public static final String URL_PRICE = "price";
    public static final String ID_USER = "id_user";

    public static String BACKGOUND = "background";
    public static String URL_PHOTO = "urlPhoto";
    public static String ID_PRODUCT = "id_product";
    public static String POSITION = "position";
    public static String URL_FULL_PHOTO = "urlFullPhoto";
    String TAG = "DetailProductAvtivity";

    String _idProduct;
    int _position;
    private ProgressDialog progress;
    private String _uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {


        //Esto me sirve para eliminar la parte superior e inferior
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);

        final FloatingActionButton fb = findViewById(R.id.trash);
        Intent data = getIntent();

        //Datos
        _idProduct = data.getStringExtra(ID_PRODUCT);
        _position = data.getIntExtra(POSITION, 0);
        _uid = data.getStringExtra(ID_USER);

        if (_uid.equals(FirebaseUtil.getCurrentUserId())) {
            fb.show();
        } else {
            fb.setVisibility(View.GONE);
        }

        Transition transition = getWindow().getSharedElementEnterTransition();
        transition.addTarget(android.R.transition.slide_top);
        transition.addListener(new Transition.TransitionListener() {

            @Override
            public void onTransitionStart(Transition transition) {
                Log.e("transition ", "onTransitionStart");
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                Log.e("transition ", "onTransitionEnd");

            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });


        CardView card = findViewById(R.id.card_info);
        RelativeLayout container = findViewById(R.id.container);

        Button bt_full_detal = findViewById(R.id.bt_full_detail);
        bt_full_detal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailMinActivity.this, ProfileActivity.class);
                i.putExtra(ProfileActivity.ID_USER, getIntent().getStringExtra(ID_USER));
                startActivity(i);
            }
        });


        final ImageView image = findViewById(R.id.image_product);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailMinActivity.this, ImageActivity.class);
                i.putExtra(ImageActivity.URL_THUM, getIntent().getStringExtra(URL_FULL_PHOTO));
                i.putExtra(ImageActivity.ID_USER, getIntent().getStringExtra(ID_USER));
                i.putExtra(ImageActivity.POST_KEY, _idProduct);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                        DetailMinActivity.this,
                        Pair.create((View) image, "full_image")

                );
                startActivity(i, options.toBundle());
                finish();

            }
        });

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Log.e(TAG, "Click in container Only");
            }
        });


        GlideUtil.loadImage(this.getIntent().getStringExtra(URL_FULL_PHOTO), image);

        TextView title = (TextView) findViewById(R.id.textTitle);
        title.setText("Gorro de lana");

        TextView price = (TextView) findViewById(R.id.textPrice);
        price.setText("$30");


        getWindow().setAllowEnterTransitionOverlap(false);
    }

    @Override
    public void onBackPressed() {
        //MainActivity.blurContent.setVisibility(View.GONE);
        super.onBackPressed();
    }

    public void onClickTrash(View view) {

        DialogInterface.OnClickListener yes = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                progress = ProgressDialog.show(DetailMinActivity.this, "Borrando",
                        "Espera un momento.", true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // do the thing that takes a long time
                        final DatabaseReference user_produts = FirebaseUtil.getCurrentUserRef().child("post").child(_idProduct);
                        final DatabaseReference produts = FirebaseUtil.getPostsRef().child(_idProduct);

                        produts.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (!dataSnapshot.child("type").exists() || Objects.requireNonNull(dataSnapshot.child("type").getValue(String.class)).equals(Constants.TYPE_PHOTO)) {

                                    String full = dataSnapshot.child("full_storage_uri").getValue(String.class);
                                    String thum = dataSnapshot.child("thumb_storage_uri").getValue(String.class);

                                    if (full != null && thum != null) {
                                        StorageReference fullSt = FirebaseStorage.getInstance().getReferenceFromUrl(full);

                                        final StorageReference thumSt = FirebaseStorage.getInstance().getReferenceFromUrl(thum);

                                        fullSt.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                user_produts.setValue(null);
                                                produts.setValue(null);
                                                thumSt.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        progress.dismiss();
                                                        //FirebaseUtil.getPeopleRef().child(FirebaseUtil.getCurrentUserId()).child("post").child(_idProduct).setValue(null);
                                                        finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        progress.dismiss();
                                                        Log.d(TAG, "onFailure: did not delete file");
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                progress.dismiss();
                                                Log.d(TAG, "onFailure: did not delete file");
                                            }
                                        });
                                    } else {
                                        Toast.makeText(DetailMinActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    if (Objects.requireNonNull(dataSnapshot.child("type").getValue(String.class)).equals(Constants.TYPE_YOUTUBE)) {
                                        produts.setValue(null);
                                        user_produts.setValue(null);
                                        //FirebaseUtil.getPeopleRef().child(FirebaseUtil.getCurrentUserId()).child("post").child(_idProduct).setValue(null);
                                        progress.dismiss();
                                        finish();
                                        //Toast.makeText(DetailMinActivity.this, "", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run()
//                            {
//
//                            }
//                        });
                    }
                }).start();


            }
        };

        DialogInterface.OnClickListener no = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
            }
        };

        NeftyUtil.AlertDialogAction(this, "Remove Post", "Este post se eliminara definitivamente.", yes, no);

    }

}
