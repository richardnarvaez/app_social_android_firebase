package com.richardnarvaez.up.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.graphics.Matrix;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.richardnarvaez.up.Utility.FirebaseUtil;
import com.richardnarvaez.up.Utility.GlideUtil;
import com.richardnarvaez.up.Utility.UtilsEffects;
import com.richardnarvaez.up.ViewHolder.PostViewHolder;

import com.richardnarvaez.up.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import xyz.klinker.android.drag_dismiss.DragDismissIntentBuilder;

public class ImageActivity extends AppCompatActivity {

    public static String URL_THUM = "url_thum";
    public static String URL_FULL = "url_full";
    public static String TITLE = "_title";
    public static String DESCRIPTION = "_description";
    public static String CATEGORY = "_category";
    public static String DATE = "_date";
    public static String TYPE = "_type";
    public static String POST_KEY = "_post_key";
    public static String ID_USER = "_id_user";

    String TAG = "ImageActivity";

    private static final float MIN_ZOOM = 1.0f, MAX_ZOOM = 6.0f;

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;
    private Matrix matrix;
    private Matrix savedMatrix;

    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    String url_thum, type;
    String url_full;
    PhotoView image;
    TextView text;
    ProgressBar progress;

    private RelativeLayout image_detail;
    private TextView tvTitle, tvDescription, tvCategory, fecha;
    private String id_user;
    private boolean animFinish = true;
    private Button btMD, bt_more_reto;
    private String postKey;
    FrameLayout layoutDetail;
    private Button follow;
    private RelativeLayout content_profile;
    private String _description;
    long _date;

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent i = getIntent();
        if (i != null) {

            type = i.getStringExtra(TYPE);
            url_thum = i.getStringExtra(URL_THUM);
            url_full = i.getStringExtra(URL_FULL);
            id_user = i.getStringExtra(ID_USER);
            postKey = i.getStringExtra(POST_KEY);
            _description = i.getStringExtra(DESCRIPTION);
            _date = i.getLongExtra(DATE, 0);

            initLikes();

            image = findViewById(R.id.full_image);
            fecha = findViewById(R.id.fecha);
            text = findViewById(R.id.text);
            progress = findViewById(R.id.progress);
            image_detail = findViewById(R.id.image_detail);

            tvTitle = findViewById(R.id.tvTitle);
            tvDescription = findViewById(R.id.tvDescription);
            tvCategory = findViewById(R.id.tvCategory);

            SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM yyyy | HH:mm");//dd/MM/yyyy

            fecha.setText(sdfDate.format(_date));

            GlideUtil.loadImagewtProgress(url_thum, image, progress);
            final ImageView thumbUser = findViewById(R.id.thumbUser);


            AlphaAnimation anim = new AlphaAnimation(0f, 1.0f);
            anim.setDuration(1000);
            anim.setRepeatCount(0);
            anim.setRepeatMode(Animation.REVERSE);
            //image_detail.setVisibility(View.VISIBLE);
            image_detail.startAnimation(anim);

            FirebaseUtil.getPeopleRef().child(id_user).child("author").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GlideUtil.loadProfileIcon(dataSnapshot.child("profile_picture").getValue(String.class), thumbUser);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

            bt_more_reto = findViewById(R.id.bt_more_reto);
            bt_more_reto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(ImageActivity.this);
                    //builderSingle.setIcon(R.mipmap.ic_launcher);
                    builderSingle.setTitle("Retos");

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ImageActivity.this, android.R.layout.simple_list_item_1);
                    arrayAdapter.add("Versus");
                    arrayAdapter.add("Imitar foto");

                    builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String strName = arrayAdapter.getItem(which);
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(ImageActivity.this);
                            builderInner.setMessage("Por el momento no esta disponible, si te deseas apoyar al proyecto para poder usar estas funcionalidades puedes donar en el menu inicial 'Donaciones'.");
                            builderInner.setTitle(strName);
                            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builderInner.show();
                        }
                    });
                    builderSingle.show();
                }
            });

            if (id_user.equals(FirebaseUtil.getCurrentUserId())) {
                bt_more_reto.setVisibility(View.GONE);
            } else {
                bt_more_reto.setVisibility(View.VISIBLE);
            }


            content_profile = findViewById(R.id.content_profile);
            content_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ImageActivity.this, ProfileActivity.class);
                    i.putExtra(ProfileActivity.ID_USER, id_user);
                    startActivity(i);
                }
            });


            layoutDetail = findViewById(R.id.comments_fragment_image);
            layoutDetail.setVisibility(View.INVISIBLE);

            btMD = findViewById(R.id.bt_more_details);
            btMD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(ImageActivity.this, DetailActivity.class);

                    new DragDismissIntentBuilder(ImageActivity.this)
                            .setTheme(DragDismissIntentBuilder.Theme.LIGHT)
                            .setPrimaryColorResource(android.R.color.transparent)
                            .setShowToolbar(false)
                            .setFullscreenOnTablets(true)
                            .setDrawUnderStatusBar(true)
                            .setDragElasticity(DragDismissIntentBuilder.DragElasticity.XXLARGE)
                            .build(i);


                    //Intent i = new Intent(ImageActivity.this, DetailActivity.class);
                    i.putExtra(DetailActivity.URL_THUM, url_thum);
                    i.putExtra(DetailActivity.URL_FULL, url_full);
                    i.putExtra(DetailActivity.POST_KEY, postKey);
                    i.putExtra(DetailActivity.ID_USER, id_user);
                    i.putExtra(DetailActivity.TYPE, "details");
                    startActivity(i);

                }
            });

            tvTitle.setText(i.getStringExtra(TITLE));

            if (i.getStringExtra(CATEGORY) != null) {
                if (i.getStringExtra(CATEGORY).isEmpty()) {
                    tvCategory.setVisibility(View.GONE);
                } else {
                    tvCategory.setText("#" + i.getStringExtra(CATEGORY));
                }
            }

            tvDescription.setText(i.getStringExtra(DESCRIPTION));

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (image_detail.getVisibility() == View.INVISIBLE) {
                        UtilsEffects.enterCircularReveal(image_detail);
                        animFinish = true;
                    } else {
                        UtilsEffects.exitCircularReveal(image_detail);
                        animFinish = false;
                    }
                }
            });


        } else {
            Toast.makeText(this, "Hubo un error con la imagen.", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

    }

    private void setNumLikes(long childrenCount) {

    }

    private void setSaveStatus(PostViewHolder.LikeStatus status) {
        int colorChange = ContextCompat.getColor(ImageActivity.this, status == PostViewHolder.LikeStatus.LIKED ? R.color.green_d : R.color.colorNoSelect);
        save.setImageResource(
                status == PostViewHolder.LikeStatus.LIKED ? R.drawable.vector_check : R.drawable.vector_marker);
        save.setColorFilter(colorChange);
        text_save.setText(status == PostViewHolder.LikeStatus.LIKED ? "Guardado" : "Guardar");
        text_save.setTextColor(colorChange);
    }

    public void setLikeStatus(PostViewHolder.LikeStatus status, long likes) {
        int colorChange = ContextCompat.getColor(ImageActivity.this, status == PostViewHolder.LikeStatus.LIKED ? R.color.red_d : R.color.colorNoSelect);

        like.setColorFilter(colorChange);
        like.setImageResource(
                status == PostViewHolder.LikeStatus.LIKED ? R.drawable.vector_down : R.drawable.vector_up);
        text_like.setText(status == PostViewHolder.LikeStatus.LIKED ? "" + likes : "Up");
        text_like.setTextColor(colorChange);

    }

    RelativeLayout bt_like, bt_share, bt_save;
    ImageView like, share, save;
    TextView text_like, text_save;

    private void initLikes() {

        bt_like = findViewById(R.id.bt_like);
        like = findViewById(R.id.like);
        text_like = findViewById(R.id.text_like);

        bt_share = findViewById(R.id.bt_share);

        bt_save = findViewById(R.id.bt_save);
        save = findViewById(R.id.icon_save);
        text_save = findViewById(R.id.text_save);

        ValueEventListener likeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(FirebaseUtil.getCurrentUserId())) {
                    setLikeStatus(PostViewHolder.LikeStatus.LIKED, dataSnapshot.getChildrenCount());
                } else {
                    setLikeStatus(PostViewHolder.LikeStatus.NOT_LIKED, dataSnapshot.getChildrenCount());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        FirebaseUtil.getLikesRef().child(postKey).addValueEventListener(likeListener);

        ValueEventListener saveListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    setSaveStatus(PostViewHolder.LikeStatus.LIKED);
                } else {
                    setSaveStatus(PostViewHolder.LikeStatus.NOT_LIKED);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        FirebaseUtil.getCurrentUserRef().child("save").child(postKey).addValueEventListener(saveListener);

        bt_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtil.toggleLikes(postKey);
            }
        });

        bt_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(ImageActivity.this);
                //builderSingle.setIcon(R.mipmap.ic_launcher);
                builderSingle.setTitle("Share");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ImageActivity.this, android.R.layout.simple_list_item_1);
                arrayAdapter.add("New Story");
                arrayAdapter.add("Facebook");
                arrayAdapter.add("Instagram");
                arrayAdapter.add("Whatsapp");
                arrayAdapter.add("Otro");

                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(ImageActivity.this);
                        builderInner.setMessage("Por el momento no esta disponible, si te deseas apoyar al proyecto para poder usar estas funcionalidades puedes donar en el menu inicial 'Donaciones'.");
                        builderInner.setTitle(strName);
                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builderInner.show();
                    }
                });
                builderSingle.show();
            }
        });

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtil.toggleSave(postKey);
            }
        });

    }

    private void comprobarFollow() {
        FirebaseUtil.getPeopleRef().child(FirebaseUtil.getCurrentUserId()).child("following").child(id_user)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> updatedUserData = new HashMap<>();

                        if (dataSnapshot.exists()) {
                            follow.setVisibility(View.GONE);
                        } else {
                            follow.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onBackPressed() {

        if (animFinish) {
            if (image_detail.getVisibility() == View.VISIBLE) {
                UtilsEffects.exitCircularReveal(image_detail, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animFinish = false;
                        onBackPressed();
                    }
                });
            }
        } else {
            super.onBackPressed();
        }

    }

}
