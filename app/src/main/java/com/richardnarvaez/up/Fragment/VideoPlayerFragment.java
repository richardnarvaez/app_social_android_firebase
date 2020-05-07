package com.richardnarvaez.up.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;
import com.richardnarvaez.up.R;

public class VideoPlayerFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ID = "id";

    private String mParam1;
    private String mID;


    public static VideoPlayerFragment newInstance(String id) {
        VideoPlayerFragment fragment = new VideoPlayerFragment();
        Bundle args = new Bundle();
        args.putString(ID, id);
        fragment.setArguments(args);
        return fragment;
    }


    public VideoPlayerFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static VideoPlayerFragment newInstance(String param1, String _id) {
        VideoPlayerFragment fragment = new VideoPlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ID, _id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mID = getArguments().getString(ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_youtube, container, false);
        // Inflate the layout for this fragment
//        final YouTubePlayerView youTubePlayerView = mView.findViewById(R.id.youtube_player_view);
//        getLifecycle().addObserver(youTubePlayerView);
//
////        youTubePlayerView.getPlayerUIController().showFullscreenButton(false);
////        youTubePlayerView.getPlayerUIController().showYouTubeButton(false);
////        youTubePlayerView.getPlayerUIController().showCurrentTime(false);
////        youTubePlayerView.getPlayerUIController().showPlayPauseButton(false);
////        youTubePlayerView.getPlayerUIController().showVideoTitle(true);
//
//        //youTubePlayerView.getPlayerUIController().
//
//        youTubePlayerView.initialize(new YouTubePlayerInitListener() {
//            @Override
//            public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
////                CustomPlayerUIController customPlayerUIController = new CustomPlayerUIController(this, customPlayerUI, youTubePlayer, youTubePlayerView);
////                youTubePlayer.addListener(customPlayerUIController);
//                //youTubePlayerView.addFullScreenListener(customPlayerUIController);
//
//                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
//                    @Override
//                    public void onReady() {
//                        initializedYouTubePlayer.loadVideo(mID, 0);
//                    }
//                });
//            }
//        }, true);

        return mView;
    }


}
