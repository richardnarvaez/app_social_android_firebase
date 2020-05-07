package com.richardnarvaez.up.Activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;
import com.richardnarvaez.up.Utility.FirebaseUtil;
import com.richardnarvaez.up.Utility.GlideUtil;

import com.richardnarvaez.up.R;

public class DetailProductMaxActivity extends AppCompatActivity {

    public static String USER_NAME_PRODUCT = "user_name_product";
    public static String USER_NAME = "user_name";
    public static String ID_VIDEO = "id_video";
    public static String ID_PRODUCT_POST = "key_product_post";
    String TAG = "DetailProdutMaxAtivity";
    public static String PHOTO = "photo";
    public static String ID_PRODUCT = "id_product";
    public static String PRICE = "price";
    YouTubePlayerView youTubePlayerView;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product_max);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        id = getIntent().getStringExtra(ID_PRODUCT);

        final ImageView image = findViewById(R.id.image_main_product);
        final ImageView image_small = findViewById(R.id.image_small);
        final TextView textprice = findViewById(R.id.text_price);
        final TextView textname = findViewById(R.id.text_name);
        final TextView textnameUser = findViewById(R.id.nameUser);
        final TextView company = findViewById(R.id.text_company);
        final TextView textuser_name_product = findViewById(R.id.text_name_product_user);

        /*final Button comprar = (Button) findViewById(R.id.comprar);
        comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtil.getCurrentUserRef().child("buy")
                        .child(getIntent().getStringExtra(ID_PRODUCT_POST))
                        .setValue(System.currentTimeMillis());
                Toast.makeText(DetailProductMaxActivity.this, "Producto añadido", Toast.LENGTH_SHORT).show();
            }
        });*/

        final ImageView bt_play = (ImageView) findViewById(R.id.bt_play);
        final ImageView bt_view_des = (ImageView) findViewById(R.id.bt_desc_view);
//        final TextView text_des = (TextView) findViewById(R.id.description);
//
//        bt_view_des.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (text_des.getVisibility() == View.GONE) {
//                    text_des.setVisibility(View.VISIBLE);
//                    bt_view_des.setRotation(180);
//                } else {
//                    text_des.setVisibility(View.GONE);
//                    bt_view_des.setRotation(0);
//                }
//            }
//        });

//        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player_view);
//        youTubePlayerView.initialize(new AbstractYouTubeListener() {
//            @Override
//            public void onReady() {
//                if (getIntent().getStringExtra(ID_VIDEO) != null) {
//                    bt_play.setVisibility(View.VISIBLE);
//                } else {
//                    bt_play.setVisibility(View.INVISIBLE);
//                }
//            }
//
//            @Override
//            public void onError(int error) {
//                youTubePlayerView.setVisibility(View.INVISIBLE);
//                bt_play.setVisibility(View.INVISIBLE);
//            }
//
//        }, true);

        Log.e(TAG, "PHOT: " + getIntent().getStringExtra(PHOTO));
        GlideUtil.loadImage(
                getIntent().getStringExtra(PHOTO), image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getIntent().getStringExtra(ID_VIDEO) != null) {
                    //youTubePlayerView.setVisibility(View.VISIBLE);
                    //youTubePlayerView.loadVideo(getIntent().getStringExtra(ID_VIDEO), 0);
                } else {
                    //youTubePlayerView.setVisibility(View.INVISIBLE);
                    Intent i = new Intent(DetailProductMaxActivity.this, ImageActivity.class);
                    i.putExtra(ImageActivity.URL_THUM, getIntent().getStringExtra(PHOTO));
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                            DetailProductMaxActivity.this,
                            Pair.create((View) image, "full_image")
                    );
                    startActivity(i, options.toBundle());
                }

            }
        });


        textnameUser.setText("By: " + getIntent().getStringExtra(USER_NAME));
        textprice.setText("$" + getIntent().getStringExtra(PRICE));
        textuser_name_product.setText(getIntent().getStringExtra(USER_NAME_PRODUCT));


        Log.e(TAG, "ID: " + id);
        if (id != null) {
            FirebaseUtil.getBaseRef().child("partner_products").child(id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                //GlideUtil.loadMax(dataSnapshot.child("thumbnail").getValue(String.class), image_small);

                                image_small.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(DetailProductMaxActivity.this, ImageActivity.class);
                                        i.putExtra(ImageActivity.URL_THUM, dataSnapshot.child("thumbnail").getValue(String.class));
                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                                                DetailProductMaxActivity.this,
                                                Pair.create((View) image_small, "full_image")

                                        );
                                        startActivity(i, options.toBundle());
                                    }
                                });


                                String nameP = dataSnapshot.child("name").getValue(String.class);
                                textname.setText(nameP);
                                CollapsTitle(nameP);

                                FirebaseUtil.getBaseRef().child("partner").child(dataSnapshot.child("author").child("uid").getValue(String.class))
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    company.setText(dataSnapshot.child("company").child("name").getValue(String.class));
                                                } else {
                                                    company.setText("No exist name");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError firebaseError) {

                                            }
                                        });


                            } else {
                                Log.e(TAG, "No exist");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {

                        }
                    });
        }


        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }

    private void CollapsTitle(final String name) {
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_product_info);
        collapsingToolbarLayout.setTitle(" ");

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_product_info);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    assert name != null;
                    collapsingToolbarLayout.setTitle(name);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        youTubePlayerView.release();
        super.onDestroy();
    }
}
