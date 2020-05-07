package com.richardnarvaez.up.Fragment.Home.Top;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;
import com.richardnarvaez.up.Adapter.Firebase.FirebaseRecyclerAdapter;
import com.richardnarvaez.up.Adapter.Firebase.HeaderViewHolder;
import com.richardnarvaez.up.Adapter.Firebase.InfiniteFireArray;
import com.richardnarvaez.up.Adapter.Firebase.UserViewHolder;
import com.richardnarvaez.up.Constants;
import com.richardnarvaez.up.Effect.SpacesItemDecoration;
import com.richardnarvaez.up.Fragment.Home.FragmentHome;
import com.richardnarvaez.up.Fragment.Home.InfiniteFireRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.richardnarvaez.up.Adapter.Firebase.FirebaseMegaPostQueryAdapter;
import com.richardnarvaez.up.Fragment.Home.RecyclerViewOnScrollLoadMore;
import com.richardnarvaez.up.Model.Author;
import com.richardnarvaez.up.Model.Post;
import com.richardnarvaez.up.Model.PostYouTube;
import com.richardnarvaez.up.R;
import com.richardnarvaez.up.Utility.FirebaseUtil;
import com.richardnarvaez.up.Utility.Utils;
import com.richardnarvaez.up.View.RecyclerViewLoadMore;
import com.richardnarvaez.up.ViewHolder.PostViewHolder;
import com.richardnarvaez.up.ViewHolder.TopViewHolder;

/**
 * Created by RICHARD on 01/04/2017.
 */

public class FragmentTop extends Fragment {

    String TAG = "FragmentTop";
    public static RecyclerViewLoadMore recycler;
    public static RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_complete_rv, container, false);

//        Button btParticipation = rootView.findViewById(R.id.btParticipation);
//        btParticipation.setVisibility(View.GONE);
//        btParticipation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "Abrir panel", Toast.LENGTH_SHORT).show();
//            }
//        });

        recycler = rootView.findViewById(R.id.recyclerView);
        recycler.setGridLayout(2);

        //layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        recycler.setLayoutManager(layoutManager);
//        recycler.addItemDecoration(new SpacesItemDecoration(Utils.dpToPx(getContext(), 8)));
//        recycler.setHasFixedSize(true);

        adapterTop();

        return rootView;
    }

    Query refFeed;
    private InfiniteFireArray<String> array;

    private void adapterTop() {

//        refFeed = FirebaseUtil.getPostsRef().orderByChild("likes");
//
//        array = new InfiniteFireArray<>(
//                String.class,
//                refFeed,
//                20,
//                25,
//                false,
//                true
//        );
//
//        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(array, new RecyclerViewAdapter.OnSetupViewListener() {
//            @Override
//            public void onSetupView(TopViewHolder holder, Post post, int position, String postKey, int itemViewType) {
//                setupPost(holder, post, position, postKey, itemViewType);
//
//            }
//
//            @Override
//            public void onSetupHeaderView(final HeaderViewHolder holder, Post post, int position, String postKey) {
//                setupHeader(holder);
//            }
//
//            @Override
//            public void onSetupYouTubeView(TopViewHolder contentHolder, PostYouTube post, int adapterPosition, String key, int i) {
//
//            }
//        });
//
//        array.addOnLoadingStatusListener(new InfiniteFireArray.OnLoadingStatusListener() {
//            @Override
//            public void onChanged(EventType type) {
//                switch (type) {
//                    case LoadingContent:
//                        adapter.setInitiallyLoading(false);
//                        adapter.setLoadingMore(true);
//                        break;
//                    case LoadingNoContent:
//                        //frame_no_connection.setVisibility(View.VISIBLE);
//                        adapter.setInitiallyLoading(true);
//                        adapter.setLoadingMore(false);
//                        break;
//                    case Done:
//                        adapter.setInitiallyLoading(false);
//                        adapter.setLoadingMore(false);
//                        break;
//                }
//            }
//        });
//
//        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int lastItem = 0;
//                int firstItem = 0;
//                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//                int totalItemCount = layoutManager.getItemCount();
//                if (layoutManager instanceof GridLayoutManager) {
//                    GridLayoutManager gridLayoutManager = ((GridLayoutManager) layoutManager);
//                    firstItem = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
//                    //Position to find the final item of the current LayoutManager
//                    lastItem = gridLayoutManager.findLastCompletelyVisibleItemPosition();
//                    if (lastItem == -1) lastItem = gridLayoutManager.findLastVisibleItemPosition();
//                } else if (layoutManager instanceof LinearLayoutManager) {
//                    LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) layoutManager);
//                    firstItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
//                    lastItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
//                    if (lastItem == -1)
//                        lastItem = linearLayoutManager.findLastVisibleItemPosition();
//                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
//                    StaggeredGridLayoutManager staggeredGridLayoutManager = ((StaggeredGridLayoutManager) layoutManager);
//                    // since may lead to the final item has more than one StaggeredGridLayoutManager the particularity of the so here that is an array
//                    // this array into an array of position and then take the maximum value that is the last show the position value
//                    int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
//                    staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(lastPositions);
//                    lastItem = Utils.findMax(lastPositions);
//                    firstItem = staggeredGridLayoutManager.findFirstVisibleItemPositions(lastPositions)[0];
//                }
//
//                if (lastItem <= array.getCount()) {
//                    return;
//                }
//
//                Log.e(TAG, "MORE");
//                array.more();
//
//            }
//        });
//
//
//        recycler.setAdapter(adapter);

        //LoadNewPage();

    }

    private void setupHeader(HeaderViewHolder holder) {
    }


    private void setupPost(TopViewHolder holder, Post post, int position, String postKey, int itemViewType) {
        //holder.setData(post);
        holder.setPosition(position);
    }

    private void adapterHome() {

        DatabaseReference users = getOutDataReference();

        //Find my Feed
        FirebaseUtil.getBaseRef().child("users").orderByChild("votes")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            final List<String> postPaths = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Log.d(TAG, "Persona Encontrada: " + snapshot.getKey());
                                postPaths.add(snapshot.getKey());
                            }

                            //postPaths.add(null);

                            Collections.reverse(postPaths);

                            //Collections.reverse(postPaths);

                            mAdapter = new FirebaseMegaPostQueryAdapter(postPaths, 2,
                                    new FirebaseMegaPostQueryAdapter.OnSetupViewListener() {
                                        @Override
                                        public void onSetupView(PostViewHolder holder, Post post, int position, String postKey) {

                                        }

                                        @Override
                                        public void onSetupUserView(UserViewHolder holder, Author post, int adapterPosition, String key) {
                                            setupPost(holder, post, adapterPosition, key);
                                        }
                                    });

                            recycler.setAdapter(mAdapter);

                        } else {
//                            no_connection_container.setVisibility(View.VISIBLE);
//                            text_info.setText("No esta siguendo a nadie, busque vendedores de confianza.");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                });
    }

    private DatabaseReference getOutDataReference() {
        return FirebaseUtil.getBaseRef().child("top-24");
    }


    public static class RecyclerViewAdapter extends InfiniteFireRecyclerViewAdapter<String> {

        public static final int VIEW_TYPE_HEADER = 0;
        public static final int VIEW_TYPE_CONTENT = 1;
        public static final int VIEW_TYPE_FOOTER = 2;
        public static final int VIEW_TYPE_LOADING = 3;

        private RecyclerViewAdapter.OnSetupViewListener mOnSetupViewListener;

        public interface OnSetupViewListener {
            void onSetupView(TopViewHolder holder, Post post, int position, String postKey, int itemViewType);

            void onSetupHeaderView(HeaderViewHolder holder, Post post, int position, String postKey);

            void onSetupYouTubeView(TopViewHolder contentHolder, PostYouTube post, int adapterPosition, String key, int i);
        }

        public static class LoadingHolder extends RecyclerView.ViewHolder {
            public ProgressBar progressBar;

            public LoadingHolder(View view) {
                super(view);
                progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            }
        }

        private boolean initiallyLoading = true;
        private boolean loadingMore = false;

        public RecyclerViewAdapter(InfiniteFireArray snapshots, RecyclerViewAdapter.OnSetupViewListener _mOnSetupViewListener) {
            super(snapshots, 1, 1);
            mOnSetupViewListener = _mOnSetupViewListener;
        }

        public boolean isInitiallyLoading() {
            return initiallyLoading;
        }

        public void setInitiallyLoading(boolean initiallyLoading) {
            if (initiallyLoading == this.initiallyLoading) return;
            if (initiallyLoading && this.isLoadingMore()) {
                this.setLoadingMore(false);
            }
            this.initiallyLoading = initiallyLoading;
            notifyItemChanged(0);
        }

        public boolean isLoadingMore() {
            return loadingMore;
        }

        public void setLoadingMore(boolean loadingMore) {
            if (loadingMore == this.isLoadingMore()) return;
            if (loadingMore && this.isInitiallyLoading()) {
                this.setInitiallyLoading(false);
            }
            this.loadingMore = loadingMore;
            notifyItemChanged(getItemCount() - 1);
        }

        @Override
        public int getItemViewType(int position) {

            if (position == 0) {
                return VIEW_TYPE_HEADER;
            } else if (position == getItemCount() - 1) {
                if (isLoadingMore()) {
                    return VIEW_TYPE_LOADING;
                } else {
                    return VIEW_TYPE_FOOTER;
                }
            }

            return VIEW_TYPE_CONTENT;

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder;
            View view;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            switch (viewType) {
                case VIEW_TYPE_CONTENT:
                    view = inflater.inflate(R.layout.item_top_photo, parent, false);
                    viewHolder = new TopViewHolder(view);
                    break;
                case VIEW_TYPE_HEADER:
                    view = inflater.inflate(R.layout.item_null, parent, false);
                    viewHolder = new HeaderViewHolder(view);
                    break;
                case VIEW_TYPE_FOOTER:
                    view = inflater.inflate(R.layout.item_footer, parent, false);
                    viewHolder = new LoadingHolder(view);
                    break;
                case VIEW_TYPE_LOADING:
                    view = inflater.inflate(R.layout.item_loading_view, parent, false);
                    viewHolder = new LoadingHolder(view);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown type");
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            int viewType = getItemViewType(position);
            switch (viewType) {
                case VIEW_TYPE_CONTENT:
                    final TopViewHolder contentHolder = (TopViewHolder) holder;

                    //String value = snapshots.getItem(position - indexOffset).getValue();
                    final String key = snapshots.getItem(position - indexOffset).getKey();
                    DatabaseReference ref = FirebaseUtil.getPostsRef().child(key);

                    ValueEventListener postListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                Post post = dataSnapshot.getValue(Post.class);
                                contentHolder.setData(post, key);
                                contentHolder.setPosition(position - indexAppendix + 1);

                                if (dataSnapshot.child("type").exists()) {
                                    String type = dataSnapshot.child("type").getValue(String.class);
                                    assert type != null;
                                    switch (type) {

                                        case Constants.TYPE_YOUTUBE:
                                        case Constants.TYPE_PHOTO:
                                            contentHolder.image.setVisibility(View.VISIBLE);
                                            mOnSetupViewListener.onSetupView(contentHolder, post, contentHolder.getAdapterPosition(),
                                                    key,
                                                    2);
                                            break;
//                                            contentHolder.post_type_youtube.setVisibility(View.VISIBLE);
//
//                                            final PostYouTube postYT = dataSnapshot.getValue(PostYouTube.class);
//                                            //context.getLifecycle().addObserver(youTubePlayerView);
//                                            contentHolder.post_type_youtube.getPlayerUIController().showFullscreenButton(false);
//                                            contentHolder.post_type_youtube.getPlayerUIController().showYouTubeButton(false);
//                                            contentHolder.post_type_youtube.initialize(new YouTubePlayerInitListener() {
//                                                @Override
//                                                public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
//                                                    initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
//                                                        @Override
//                                                        public void onReady() {
//                                                            assert postYT != null;
//                                                            initializedYouTubePlayer.cueVideo(postYT.getVideo(), 0);
//                                                        }
//                                                    });
//                                                }
//                                            }, true);
//                                            PostYouTube postYouTube = dataSnapshot.getValue(PostYouTube.class);
//                                            mOnSetupViewListener.onSetupYouTubeView(contentHolder, postYouTube, contentHolder.getAdapterPosition(),
//                                                    key,
//                                                    2);
                                    }
                                } else {
                                    mOnSetupViewListener.onSetupView(contentHolder, post, contentHolder.getAdapterPosition(),
                                            key,
                                            2);
                                }


                            } else {
                                FirebaseUtil.getFeedRef().child(FirebaseUtil.getCurrentUserId()).child(dataSnapshot.getKey()).setValue(null);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            Log.e("TAG", "Error occurred: " + firebaseError.getMessage());
                        }
                    };

                    ref.addValueEventListener(postListener);

                    //setAnimation(holder.itemView);

                    break;
                case VIEW_TYPE_HEADER:
                    HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
                    mOnSetupViewListener.onSetupHeaderView(headerHolder, null, headerHolder.getAdapterPosition(),
                            null);
                    //headerHolder.progressBar.setVisibility((isInitiallyLoading()) ? View.VISIBLE : View.GONE);
                    break;
                case VIEW_TYPE_LOADING:
                    LoadingHolder loadingHolder = (LoadingHolder) holder;
                    //footerHolder.progressBar.setVisibility((isLoadingMore()) ? View.VISIBLE : View.GONE);
                    break;
                case VIEW_TYPE_FOOTER:
                    LoadingHolder footerHolder = (LoadingHolder) holder;
                    //footerHolder.progressBar.setVisibility((isLoadingMore()) ? View.VISIBLE : View.GONE);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown type");
            }
        }

    }


    private void setupPost(final UserViewHolder userViewHolder, final Author user, final int position, final String inPostKey) {
        userViewHolder.setUser(user.getName());
        userViewHolder.setImageUser(user.getProfile_picture());
        userViewHolder.setPosition(String.valueOf(position + 1));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        /*if (context instanceof OnPostSelectedListener) {
            mListener = (OnPostSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPostSelectedListener");
        }*/

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null && mAdapter instanceof FirebaseRecyclerAdapter) {
            ((FirebaseRecyclerAdapter) mAdapter).cleanup();
        }
    }

}
