package com.richardnarvaez.up.Adapter.Firebase;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.richardnarvaez.up.Model.Post;
import com.richardnarvaez.up.Utility.FirebaseUtil;
import com.richardnarvaez.up.ViewHolder.PostViewHolder;

import java.util.ArrayList;
import java.util.List;

import com.richardnarvaez.up.Model.Author;

import com.richardnarvaez.up.R;

/**
 * Created by RICHARD on 09/04/2017.
 */


public class FirebaseUserQueryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = "PostQueryAdapter";
    private List<String> mPostPaths;
    private OnSetupViewListener mOnSetupViewListener;
    private int position;


    public FirebaseUserQueryAdapter(List<String> paths, int item_home, OnSetupViewListener onSetupViewListener) {
        this.position = item_home;

        if (paths == null || paths.isEmpty()) {
            mPostPaths = new ArrayList<>();
        } else {
            mPostPaths = paths;
        }
        mOnSetupViewListener = onSetupViewListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;

        switch (position) {
            case 0:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_save, parent, false);
                return new PostViewHolder(v);
            case 1:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_story, parent, false);
                return new UserViewHolder(v);
            case 2:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_top_photo, parent, false);
                return new UserViewHolder(v);
        }
        return null;
    }

    public void setPaths(List<String> postPaths) {
        mPostPaths = postPaths;
        notifyDataSetChanged();
    }

    public void addItem(String path) {
        mPostPaths.add(path);
        notifyItemInserted(mPostPaths.size());
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        DatabaseReference ref = FirebaseUtil.getPeopleRef().child(mPostPaths.get(position));
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Author user = dataSnapshot.child("author").getValue(Author.class);
                    mOnSetupViewListener.onSetupUserView(((UserViewHolder) holder),
                            user,
                            position,
                            dataSnapshot.getKey());
                } else {
                    //FirebaseUtil.getCurrentUserRef().child("save").child(dataSnapshot.getKey()).child(null);
                    //FirebaseUtil.getCurrentUserRef().child("buy").child(dataSnapshot.getKey()).child(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e(TAG, "Error occurred: " + firebaseError.getMessage());
            }
        };

        ref.addValueEventListener(postListener);
        /*holder.mPostRef = ref;
        holder.mPostListener = postListener;*/
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        //((UserViewHolder) holder).mPostRef.removeEventListener(((UserViewHolder) holder).mPostListener);
    }

    @Override
    public int getItemCount() {
        return mPostPaths.size();
    }

    public interface OnSetupViewListener {
        void onSetupView(PostViewHolder holder, Post post, int position, String postKey);

        void onSetupUserView(UserViewHolder holder, Author post, int adapterPosition, String key);
    }
}