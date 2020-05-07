package com.richardnarvaez.up.Fragment.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import com.richardnarvaez.up.Adapter.Firebase.FirebaseMegaPostQueryAdapter;
import com.richardnarvaez.up.Adapter.Firebase.UserViewHolder;
import com.richardnarvaez.up.Adapter.ProductosAdapter;
import com.richardnarvaez.up.Model.Author;
import com.richardnarvaez.up.Model.ItemCesta;
import com.richardnarvaez.up.Model.Post;
import com.richardnarvaez.up.R;
import com.richardnarvaez.up.Utility.FirebaseUtil;
import com.richardnarvaez.up.ViewHolder.PostViewHolder;

/**
 * Created by RICHARD on 02/04/2017.
 */

public class FragmentBuy extends Fragment {

    private RecyclerView rvBuy;
    private ProductosAdapter videoListAdapter;
    private ArrayList<ItemCesta> itemCestas;
    private RecyclerView rvSave;
    private String TAG = "FragmentBuy";
    private FirebaseMegaPostQueryAdapter mAdapter, mAdapterBuy;
    LinearLayout no_items;

    public FragmentBuy() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_buy, container, false);

        itemCestas = new ArrayList<>();
        //conf = new NetworkConf(getActivity());

        no_items = (LinearLayout) rootView.findViewById(R.id.no_items_info);
        rvBuy = (RecyclerView) rootView.findViewById(R.id.recyclerPay);
        rootView.findViewById(R.id.button_buy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(getContext(), PayActivity.class));
            }
        });

        rvSave = (RecyclerView) rootView.findViewById(R.id.recyclerSaveFavorite);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvSave.setLayoutManager(linearLayoutManager);
        adapterSave();

        rvBuy.setHasFixedSize(true);
        //videoListAdapter = new ProductosAdapter(getActivity(), itemCestas, R.layout.item_small);
        rvBuy.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvBuy.setItemAnimator(new DefaultItemAnimator());
        //videoListAdapter.setOnItemEventsListener(this);
        //setLayoutManager(new GridLayoutManager(this,3));
        //rvProductos.setAdapter(videoListAdapter);
        //disable swipe to refresh for this tab
        //rvBuy.setAdapter(videoListAdapter);


        return rootView;
    }

    private void adapterSave() {
        //Query allPostsQuery = FirebaseUtil.getCurrentUserRef().child("save");
        /*mAdapter = getRecyclerAdapter(allPostsQuery);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
            }
        });*/

        //rvSave.setAdapter(mAdapter);

        FirebaseUtil.getCurrentUserRef().child("save")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final List<String> postPaths = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Log.d(TAG, "Add Save: " + snapshot.getKey());
                                postPaths.add(snapshot.getKey());
                            }

                            mAdapter = new FirebaseMegaPostQueryAdapter(postPaths, 0,
                                    new FirebaseMegaPostQueryAdapter.OnSetupViewListener() {
                                        @Override
                                        public void onSetupView(PostViewHolder holder, Post post, int position, String postKey) {
                                            setupPost(holder, post, position, postKey);
                                        }

                                        @Override
                                        public void onSetupUserView(UserViewHolder holder, Author post, int adapterPosition, String key) {

                                        }
                                    });

                            rvSave.setAdapter(mAdapter);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                });

        FirebaseUtil.getCurrentUserRef().child("buy")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final List<String> postPaths = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Log.d(TAG, "Add Buy: " + snapshot.getKey());
                                postPaths.add(snapshot.getKey());
                            }

                            mAdapterBuy = new FirebaseMegaPostQueryAdapter(postPaths, 1,
                                    new FirebaseMegaPostQueryAdapter.OnSetupViewListener() {
                                        @Override
                                        public void onSetupView(PostViewHolder holder, Post post, int position, String postKey) {
                                            setupPostBuy(holder, post, position, postKey);
                                        }

                                        @Override
                                        public void onSetupUserView(UserViewHolder holder, Author post, int adapterPosition, String key) {

                                        }
                                    });

                            rvBuy.setAdapter(mAdapterBuy);
                            if (mAdapterBuy.getItemCount() == 0) {
                                no_items.setVisibility(View.VISIBLE);
                            } else {
                                no_items.setVisibility(View.GONE);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                });

    }

    private void setupPostBuy(PostViewHolder holder, Post post, int position, String postKey) {
        holder.getLayoutPosition();
        holder.setPhotoSave(post.getThumbnail());
        holder.setPrice(post.getPrice());
        holder.setText(post.getName());
        holder.setOnClick(postKey);
    }


    /*private FirebaseRecyclerAdapter<Post, PostViewHolder> getRecyclerAdapter(Query query) {
        return new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class, R.layout.item_save, PostViewHolder.class, query) {
            @Override
            public void populateViewHolder(final PostViewHolder postViewHolder,
                                           final Post post, final int position) {
                setupPost(postViewHolder, post, position, null);
            }

            @Override
            public void onViewRecycled(PostViewHolder holder) {
                super.onViewRecycled(holder);
            }
        };
    }
*/

    private void setupPost(PostViewHolder holder, Post post, int position, String postKey) {
        holder.setPhotoSave(post.getThumbnail());
    }

    @Override
    public void onResume() {
        super.onResume();
        //itemCestas.clear();
        //itemCestas.addAll(StoreSqlDb.getInstance().productos(StoreSqlDb.ITEM_TYPE.FAVORITE).readAll());
        //videoListAdapter.notifyDataSetChanged();
    }

}
