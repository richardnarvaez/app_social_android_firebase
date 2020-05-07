package com.richardnarvaez.up.Adapter.Firebase;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.richardnarvaez.up.Interface.OnLoadMoreListener;
import com.richardnarvaez.up.Model.Post;
import com.richardnarvaez.up.Utility.FirebaseUtil;
import com.richardnarvaez.up.ViewHolder.LoadingViewHolder;
import com.richardnarvaez.up.ViewHolder.PostViewHolder;

import java.util.ArrayList;

import com.richardnarvaez.up.R;

/**
 * Created by RICHARD on 09/04/2017.
 */


public class FirebasePostQueryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = "PostQueryAdapter";
    private List<String> mPostPaths;
    private OnSetupViewListener mOnSetupViewListener;
    private OnHeaderViewListener mOnHeaderViewListener;
    private OnLoadMoreListener mOnLoadMoreListener;

    private static final int HEADER_ITEM = 0;
    private static final int LIST_ITEM = 1;
    private static final int GRID_ITEM = 2;
    private static final int LOADING_ITEM = 3;
    private static final int MORE_ITEM = 4;


    public FirebasePostQueryAdapter(List<String> paths, OnSetupViewListener onSetupViewListener, OnHeaderViewListener onHeaderViewListener) {
        if (paths == null || paths.isEmpty()) {
            mPostPaths = new ArrayList<>();
        } else {
            mPostPaths = paths;
        }

        mOnSetupViewListener = onSetupViewListener;
        mOnHeaderViewListener = onHeaderViewListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;

        switch (viewType) {
            case 0:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_head_feed, parent, false);
                return new HeaderViewHolder(v);
            case 1:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_home_image, parent, false);
                return new PostViewHolder(v);
            case 2:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_post_home, parent, false);
                return new PostViewHolder(v);
            case 3:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_loading_view, parent, false);
                return new LoadingViewHolder(v);

            case 4:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_home, parent, false);
                return new LoadingViewHolder(v);

            default:
                break;
        }

        return new PostViewHolder(v);
    }

    public void setPaths(List<String> postPaths) {
        mPostPaths = postPaths;
        notifyDataSetChanged();
    }

    public void addItem(String path) {
        mPostPaths.add(mPostPaths.size(), path);
        notifyItemInserted(mPostPaths.size());
    }

    public String getItem(int position) {
        return mPostPaths.get(position);
    }

    public void removeLoadItem() {
        mPostPaths.remove(mPostPaths.size() - 1);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case 0:
                HeaderViewHolder vhHeader = (HeaderViewHolder) holder;
                vhHeader.setImage(isSwitchView);
                mOnHeaderViewListener.onSetupView(vhHeader, null, vhHeader.getAdapterPosition(),
                        null);
                break;

            case 1:
            case 2:
                //Debido al Header la posicion debe disminuir en 1 para poder
                //ver todos los post correctamente.
                final PostViewHolder vhItem = (PostViewHolder) holder;
                DatabaseReference ref = FirebaseUtil.getPostsRef().child(mPostPaths.get(position));

                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            mOnSetupViewListener.onSetupView(vhItem, post, vhItem.getAdapterPosition(),
                                    dataSnapshot.getKey(),
                                    holder.getItemViewType());
                        } else {
                            FirebaseUtil.getFeedRef().child(FirebaseUtil.getCurrentUserId()).child(dataSnapshot.getKey()).setValue(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.e(TAG, "Error occurred: " + firebaseError.getMessage());
                    }
                };

                ref.addValueEventListener(postListener);
                vhItem.mPostRef = ref;
                vhItem.mPostListener = postListener;
                break;
            case 3:
                if (holder instanceof LoadingViewHolder) {
                    LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                    loadingViewHolder.progressBar.setIndeterminate(true);
                }
                break;
        }

    }


    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public OnLoadMoreListener getLoadMoreListener() {
        return mOnLoadMoreListener;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        switch (holder.getItemViewType()) {
            case 2:
                PostViewHolder vhPost = (PostViewHolder) holder;
                vhPost.mPostRef.removeEventListener(vhPost.mPostListener);
                break;
        }
    }

    private boolean isSwitchView = false;

    public boolean toggleItemViewType() {
        isSwitchView = !isSwitchView;
        return isSwitchView;
    }

    public boolean getTypeList() {
        return isSwitchView;
    }


    @Override
    public int getItemViewType(int position) {

        /*if (position == 3 && mPostPaths.get(4) != null) {
            //mPostPaths.add(3, null);
            //notifyItemInserted(3);
            return MORE_ITEM;
        }*/

        if (mPostPaths.get(position) == null) {
            if (position == 0) {
                return HEADER_ITEM;
            }
//            } else {
//                return LOADING_ITEM;
//            }

        } else {
            if (isSwitchView) {
                return LIST_ITEM;
            } else {
                return GRID_ITEM;
            }
        }

        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mPostPaths.size();
    }

    public void setFinishLoaded() {
        //isLoading = false;
    }

    public void clearData() {
        mPostPaths.clear();
        //notifyItemInserted(mPostPaths.size());
    }

    public interface OnSetupViewListener {
        void onSetupView(PostViewHolder holder, Post post, int position, String postKey, int itemViewType);
    }

    public interface OnHeaderViewListener {
        void onSetupView(HeaderViewHolder holder, Post post, int position, String postKey);
    }

}