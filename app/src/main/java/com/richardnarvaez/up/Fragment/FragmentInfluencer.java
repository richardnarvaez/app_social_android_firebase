package com.richardnarvaez.up.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.richardnarvaez.up.Adapter.Firebase.FirebaseRecyclerAdapter;
import com.richardnarvaez.up.Adapter.Firebase.HeaderViewHolder;
import com.richardnarvaez.up.Adapter.Firebase.InfiniteFireArray;
import com.richardnarvaez.up.Adapter.FirebaseTopPostAdapter;
import com.richardnarvaez.up.Fragment.Home.InfiniteFireRecyclerViewAdapter;
import com.richardnarvaez.up.Model.Post;
import com.richardnarvaez.up.R;
import com.richardnarvaez.up.Utility.FirebaseUtil;
import com.richardnarvaez.up.Utility.Utils;
import com.richardnarvaez.up.ViewHolder.PostViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by RICHARD on 01/04/2017.
 */

public class FragmentInfluencer extends Fragment {

    String TAG = "DiscoverFragment";
    public static RecyclerView recycler;
    public static RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;
    private int lastVisibleItem;
    FirebaseTopPostAdapter mAdapterF = null;
    String oldestKey;
    private int currentPage = 1;
    private InfiniteFireArray<String> array;
    private FrameLayout frame_no_connection;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_influencer, container, false);

//        recycler = rootView.findViewById(R.id.recyclerView);
//        frame_no_connection = rootView.findViewById(R.id.no_connection_container);
//        frame_no_connection.setVisibility(View.GONE);
//
//        mAdapterF = ((FirebaseTopPostAdapter) mAdapter);
//        recycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        recycler.addItemDecoration(new EqualSpacingItemDecoration(16));
//        LoadNewPage();

        return rootView;
    }

    DatabaseReference refFeed;

    public void LoadNewPage() {

        refFeed = Objects.requireNonNull(FirebaseUtil.getCurrentUserRef()).child("save");

        array = new InfiniteFireArray<>(
                String.class,
                refFeed,
                15,
                15,
                false,
                false
        );

        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(array, new RecyclerViewAdapter.OnSetupViewListener() {
            @Override
            public void onSetupView(PostViewHolder holder, Post post, int position, String postKey, int itemViewType) {
                setupPost(holder, post, position, postKey, itemViewType);
            }

            @Override
            public void onSetupHeaderView(final HeaderViewHolder holder, Post post, int position, String postKey) {
                //setupHeader(holder);
            }
        });

        array.addOnLoadingStatusListener(new InfiniteFireArray.OnLoadingStatusListener() {
            @Override
            public void onChanged(EventType type) {
                switch (type) {
                    case LoadingContent:
                        adapter.setInitiallyLoading(false);
                        adapter.setLoadingMore(true);
                        break;
                    case LoadingNoContent:
                        adapter.setInitiallyLoading(true);
                        adapter.setLoadingMore(false);
                        break;
                    case Done:
                        adapter.setInitiallyLoading(false);
                        adapter.setLoadingMore(false);
                        break;
                }
            }
        });

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastItem = 0;
                int firstItem = 0;
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                if (layoutManager instanceof GridLayoutManager) {
                    GridLayoutManager gridLayoutManager = ((GridLayoutManager) layoutManager);
                    firstItem = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
                    //Position to find the final item of the current LayoutManager
                    lastItem = gridLayoutManager.findLastCompletelyVisibleItemPosition();
                    if (lastItem == -1) lastItem = gridLayoutManager.findLastVisibleItemPosition();
                } else if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) layoutManager);
                    firstItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                    lastItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    if (lastItem == -1)
                        lastItem = linearLayoutManager.findLastVisibleItemPosition();
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager staggeredGridLayoutManager = ((StaggeredGridLayoutManager) layoutManager);
                    // since may lead to the final item has more than one StaggeredGridLayoutManager the particularity of the so here that is an array
                    // this array into an array of position and then take the maximum value that is the last show the position value
                    int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                    staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(lastPositions);
                    lastItem = Utils.findMax(lastPositions);
                    firstItem = staggeredGridLayoutManager.findFirstVisibleItemPositions(lastPositions)[0];
                }

                if (lastItem <= array.getCount()) {
                    return;
                }

                array.more();

            }
        });


        recycler.setAdapter(adapter);

        //Find my Feed
//        refFeed.orderByKey().limitToLast(10)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Log.e(TAG, "Ref: " + dataSnapshot.getRef());
//                        if (dataSnapshot.exists()) {
//                            //no_connection_container.setVisibility(View.GONE);
//                            final List<String> postPaths = new ArrayList<>();
//                            postPaths.clear();
//                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                Log.e(TAG, "Post: " + snapshot.getKey());
//                                postPaths.add(snapshot.getKey());
//                            }
//
//                            //Add Header ITEM
//                            //postPaths.add(null);
//                            Log.e(TAG, "SIZE: " + postPaths.size());
//
//                            Collections.reverse(postPaths);
//
//                            if (postPaths.size() >= 11) {
//                                oldestKey = postPaths.get(10);
//                            } else {
//                                oldestKey = postPaths.get(postPaths.size() - 1);
//                            }
//
//                            mAdapterF = new FirebaseTopPostAdapter(postPaths,
//                                    new FirebaseTopPostAdapter.OnSetupViewListener() {
//                                        @Override
//                                        public void onSetupView(PostViewHolder holder, Post post, int position, String postKey, int itemViewType) {
//                                            setupPost(holder, post, position, postKey, itemViewType);
//                                        }
//
//                                        @Override
//                                        public void onSetupViewHeader(HeaderViewTopPostHolder holder, int adapterPosition) {
//                                            StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(
//                                                    ViewGroup.LayoutParams.MATCH_PARENT,
//                                                    ViewGroup.LayoutParams.WRAP_CONTENT);
//                                            //Utils.dpToPx(getActivity(),156));
//                                            layoutParams.setFullSpan(true);
//                                            holder.view.setLayoutParams(layoutParams);
//                                        }
//                                    });
//
//                            recycler.setAdapter(mAdapterF);
//
//                        } else {
//                            //no_connection_container.setVisibility(View.VISIBLE);
//                            //text_info.setText("No esta siguendo a nadie, busca perfiles que te gusten.");
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError firebaseError) {
//                        Log.e(TAG, "DatabaseError: " + firebaseError);
//                    }
//                });
    }

    private void setupPost(final PostViewHolder postViewHolder, final Post post, final int position, final String inPostKey, int itemViewType) {
        //postViewHolder.setPhoto(post.getThumbnail(), post.getFull_url());
        Log.e(TAG, "LEGADADADADADADADA");
        postViewHolder.setPhotoOther(post.getFull_url());
        postViewHolder.onClickImage(getActivity(), post, inPostKey);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

    public void onLoadMore(final int lastItem) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refFeed.orderByKey()
                        .endAt(oldestKey)
                        .limitToLast(11)//currentPage * visibleThreshold)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.e(TAG, "Item of Load: " + lastItem);
                                Log.e(TAG, "Total items: " + dataSnapshot.getChildrenCount());
                                //
                                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() != 1) {

                                    currentPage++;
                                    lastVisibleItem = lastVisibleItem + 10;
                                    Log.e(TAG, "PAGE: " + currentPage);

                                    //mAdapterF.removeLoadItem();
                                    final List<String> post = new ArrayList<>();

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        post.add(snapshot.getKey());
                                    }

                                    Collections.reverse(post);

                                    oldestKey = post.get(post.size() - 1);
                                    for (int a = 1; a <= post.size() - 1; a++) {
                                        mAdapterF.addItem(post.get(a));
                                        Log.e(TAG, "Insert at: " + a);
                                    }

                                    Log.e(TAG, "SIZE: " + mAdapterF.getItemCount());
                                    mAdapterF.notifyDataSetChanged();
                                    //recycler.setPullLoadMoreCompleted();

                                } else {
                                    Log.e(TAG, "No more");
                                    //recycler.setPullLoadMoreCompleted();
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //recycler.setPullLoadMoreCompleted();
                                Toast.makeText(getContext(), "Hubo un error para obtener mas POST", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }, 500);

    }

    public static class RecyclerViewAdapter extends InfiniteFireRecyclerViewAdapter<String> {

        public static final int VIEW_TYPE_HEADER = 0;
        public static final int VIEW_TYPE_CONTENT = 1;
        public static final int VIEW_TYPE_FOOTER = 2;
        public static final int VIEW_TYPE_LOADING = 3;

        private RecyclerViewAdapter.OnSetupViewListener mOnSetupViewListener;

        public interface OnSetupViewListener {
            void onSetupView(PostViewHolder holder, Post post, int position, String postKey, int itemViewType);

            void onSetupHeaderView(HeaderViewHolder holder, Post post, int position, String postKey);
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
                    view = inflater.inflate(R.layout.item_save, parent, false);
                    viewHolder = new PostViewHolder(view);
                    break;
                case VIEW_TYPE_HEADER:
                    view = inflater.inflate(R.layout.item_head_saved, parent, false);
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
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            switch (viewType) {
                case VIEW_TYPE_CONTENT:
                    final PostViewHolder contentHolder = (PostViewHolder) holder;

                    String value = snapshots.getItem(position - indexOffset).getValue();
                    final String key = snapshots.getItem(position - indexOffset).getKey();
                    Log.e("TAG", "KEY: >>>>>>>>" + key);

                    DatabaseReference ref = FirebaseUtil.getPostsRef().child(key);

                    ValueEventListener postListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Post post = dataSnapshot.getValue(Post.class);
                                mOnSetupViewListener.onSetupView(contentHolder, post, contentHolder.getAdapterPosition(),
                                        key,
                                        2);
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

        private final static int FADE_DURATION = 1000;

        private void setAnimation(View view) {
            // If the bound view wasn't previously displayed on screen, it's animated
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(FADE_DURATION);
            view.startAnimation(anim);
        }

        @Override
        public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder) {
            recycler.clearAnimation();
        }
//
//        public void clearAnimation()
//        {
//            mRootLayout.clearAnimation();
//        }
    }

}
