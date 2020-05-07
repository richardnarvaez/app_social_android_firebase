package com.richardnarvaez.up.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;
import com.richardnarvaez.up.Model.Post;
import com.richardnarvaez.up.Model.PostYouTube;
import com.richardnarvaez.up.R;

public class VideoActivity extends AppCompatActivity {

    public static String TYPE = "_type";
    public static String POST = "post";
    public static String TITLE = "_title";
    public static String SUBTITLE = "_subtitle";
    String post;
    YouTubePlayerView youTubePlayerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent i = getIntent();
        post = i.getStringExtra(POST);

        toolbar.setTitle(i.getStringExtra(TITLE));
        toolbar.setSubtitle(i.getStringExtra(SUBTITLE));

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        initYouTube();

    }

    private void initYouTube() {
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.getPlayerUIController().showFullscreenButton(false);
        youTubePlayerView.getPlayerUIController().showYouTubeButton(false);
        youTubePlayerView.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        initializedYouTubePlayer.loadVideo(post, 0);
                    }
                });
            }
        }, true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
