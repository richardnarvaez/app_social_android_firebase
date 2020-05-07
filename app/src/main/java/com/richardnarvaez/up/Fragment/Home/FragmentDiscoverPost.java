package com.richardnarvaez.up.Fragment.Home;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.richardnarvaez.up.Adapter.Firebase.FirebaseRecyclerAdapter;
import com.richardnarvaez.up.Utility.Utils;
import com.richardnarvaez.up.View.RecyclerViewLoadMore;
import com.richardnarvaez.up.ViewHolder.PostViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.richardnarvaez.up.Adapter.FirebaseTopPostAdapter;
import com.richardnarvaez.up.Adapter.HeaderViewTopPostHolder;
import com.richardnarvaez.up.Model.Post;
import com.richardnarvaez.up.R;
import com.richardnarvaez.up.Utility.FirebaseUtil;

/**
 * Created by RICHARD on 01/04/2017.
 */

public class FragmentDiscoverPost extends Fragment implements RecyclerViewLoadMore.PullLoadMoreListener {

    String TAG = "DiscoverFragment";
    public static RecyclerViewLoadMore recycler;
    public static RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;
    private int totalItemCount;
    private int lastVisibleItem;
    private boolean isLoading;
    private int visibleThreshold = 10;
    FirebaseTopPostAdapter mAdapterF = null;
    String oldestKey;
    private int currentPage = 1;
    private boolean nomore = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_complete_rv, container, false);

        recycler = rootView.findViewById(R.id.recyclerView);


        mAdapterF = ((FirebaseTopPostAdapter) mAdapter);
        recycler.setRefreshing(false);
        recycler.setPullRefreshEnable(false);
        recycler.setOnPullLoadMoreListener(this);
        recycler.setStaggeredGridLayout(2);

        LoadNewPage();

        return rootView;
    }

    DatabaseReference refFeed;

    public void LoadNewPage() {
        refFeed = FirebaseUtil.getPostsRef();
        refFeed.limitToLast(10)
                .addListenerForSingleValueEvent(getFirstData());
    }

    ValueEventListener getFirstData() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final List<String> postPaths = new ArrayList<>();
                    postPaths.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        postPaths.add(snapshot.getKey());
                    }

                    //Add Header ITEM
                    postPaths.add(null);
                    Collections.reverse(postPaths);

                    if (postPaths.size() >= 11) {
                        oldestKey = postPaths.get(10);
                    } else {
                        oldestKey = postPaths.get(postPaths.size() - 1);
                    }

                    mAdapterF = new FirebaseTopPostAdapter(postPaths,
                            new FirebaseTopPostAdapter.OnSetupViewListener() {
                                @Override
                                public void onSetupView(PostViewHolder holder, Post post, int position, String postKey, int itemViewType) {
                                    setupPost(holder, post, position, postKey, itemViewType);
//                                    if (holder.mPhotoView.getLayoutParams().height >= 300) {
//
//                                    } else {
//                                        holder.mPhotoView.getLayoutParams().height = Utils.getRandomIntInRange(475, 300);
//                                    }
                                }

                                @Override
                                public void onSetupViewHeader(HeaderViewTopPostHolder holder, int adapterPosition) {
                                    StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                                    layoutParams.setFullSpan(true);
                                    holder.view.setLayoutParams(layoutParams);
                                }

                            });

                    recycler.setAdapter(mAdapterF);

                } else {
                    //no_connection_container.setVisibility(View.VISIBLE);
                    //text_info.setText("No esta siguendo a nadie, busca perfiles que te gusten.");
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e(TAG, "DatabaseError: " + firebaseError);
            }
        };
    }

    private void setupPost(final PostViewHolder postViewHolder, final Post post, final int position, final String inPostKey, int itemViewType) {

        postViewHolder.onClickImage(getActivity(), post, inPostKey);
        postViewHolder.setPhotoOther(post.getFull_url());

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

    /*Load more*/
    @Override
    public void onRefresh() {
        mAdapterF.clearData();
        recycler.setPullLoadMoreCompleted();
        //mCount = 1;
    }

    @Override
    public void onLoadMore(final int lastItem) {
        if (!nomore) {
            refFeed.orderByKey()
                    .endAt(oldestKey)
                    .limitToLast(11)//currentPage * visibleThreshold)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                Log.e(TAG, "Item of Load: " + lastItem);
//                                Log.e(TAG, "Total items: " + dataSnapshot.getChildrenCount());
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
                                //mAdapterF.notifyDataSetChanged();
                                recycler.setPullLoadMoreCompleted();

                            } else {
                                Log.e(TAG, "No more");
                                nomore = true;
                                recycler.setPullLoadMoreCompleted();
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            recycler.setPullLoadMoreCompleted();
                            Toast.makeText(getContext(), "Hubo un error para obtener mas POST", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            recycler.setPullLoadMoreCompleted();
        }

    }


}
