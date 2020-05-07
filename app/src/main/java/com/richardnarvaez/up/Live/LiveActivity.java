package com.richardnarvaez.up.Live;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;
import com.richardnarvaez.up.R;


public class LiveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        YouTubePlayerView video = findViewById(R.id.ytVideo);

        video.getPlayerUIController().showFullscreenButton(false);
        video.getPlayerUIController().showYouTubeButton(false);
        video.getPlayerUIController().enableLiveVideoUI(true);
        video.enterFullScreen();
        video.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        initializedYouTubePlayer.cueVideo("0_YgglGYOJ4", 0);
                    }
                });
            }
        }, true);

    }
}
