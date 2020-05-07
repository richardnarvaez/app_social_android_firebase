package com.richardnarvaez.up.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.richardnarvaez.up.Interface.OnLoadMoreListener;
import com.richardnarvaez.up.ViewHolder.PostViewHolder;

import java.util.ArrayList;
import java.util.List;

import com.richardnarvaez.up.Model.Post;
import com.richardnarvaez.up.R;
import com.richardnarvaez.up.Utility.FirebaseUtil;
import com.richardnarvaez.up.ViewHolder.LoadingViewHolder;


/**
 * Created by RICHARD on 09/04/2017.
 */


public class FirebaseTopPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = "PostQueryAdapter";
    private List<String> mPostPaths;
    private FirebaseTopPostAdapter.OnSetupViewListener mOnSetupViewListener;
    private OnLoadMoreListener mOnLoadMoreListener;

    private static final int HEADER_ITEM = 0;
    private static final int GRID_ITEM = 2;
    private static final int LOADING_ITEM = 3;


    public FirebaseTopPostAdapter(List<String> paths, FirebaseTopPostAdapter.OnSetupViewListener onSetupViewListener) {
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

        switch (viewType) {
            case 0:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_rv_top_category, parent, false);
                return new HeaderViewTopPostHolder(v);
            case 1:
//                v = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.item_home_image_h, parent, false);
//                return new ImageViewHolder(v);
            case 2:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_save, parent, false);
                return new PostViewHolder(v);
            case 3:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_loading_view, parent, false);
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

    public void clearData() {
        mPostPaths.clear();
        //notifyItemInserted(mPostPaths.size());
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
                HeaderViewTopPostHolder vhHeader = (HeaderViewTopPostHolder) holder;
                //vhHeader.setImage(isSwitchView);
                mOnSetupViewListener.onSetupViewHeader(vhHeader, vhHeader.getAdapterPosition());
                break;

            case 1:
            case 2:
                //Debido al Header la posicion debe disminuir en 1 para poder
                //ver todos los post correctamente.
                final PostViewHolder vhItem = (PostViewHolder) holder;
                //vhItem.card_Image2.setLayoutParams(new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dpToPx(vhItem.itemView.getContext(), 256)));
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

                ref.addListenerForSingleValueEvent(postListener);
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

    @Override
    public int getItemViewType(int position) {
        if (mPostPaths.get(position) == null) {

            if (position == 0) {
                return HEADER_ITEM;
            } else {
                return LOADING_ITEM;
            }

        } else {
            return GRID_ITEM;
//            if (position == 1) {
//                return 1;
//            } else {
//                return GRID_ITEM;
//            }

        }

        //return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mPostPaths.size();
    }

    public void setFinishLoaded() {
        //isLoading = false;
    }

    public interface OnSetupViewListener {
        void onSetupView(PostViewHolder holder, Post post, int position, String postKey, int itemViewType);

        void onSetupViewHeader(HeaderViewTopPostHolder vhHeader, int adapterPosition);
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageViewHolder(View v) {
            super(v);
        }
    }
}