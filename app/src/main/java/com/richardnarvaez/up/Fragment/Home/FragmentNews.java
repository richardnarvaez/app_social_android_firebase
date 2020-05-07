package com.richardnarvaez.up.Fragment.Home;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.richardnarvaez.up.Adapter.Firebase.FirebaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.richardnarvaez.up.Model.Post;
import com.richardnarvaez.up.Model.Product;
import com.richardnarvaez.up.R;
import com.richardnarvaez.up.Utility.FirebaseUtil;
import com.richardnarvaez.up.Utility.GlideUtil;
import com.richardnarvaez.up.View.ParallaxRecyclerView;
import com.richardnarvaez.up.View.SquareImageView;
import com.richardnarvaez.up.ViewHolder.ParallaxViewHolder;

/**
 * Created by RICHARD on 01/04/2017.
 */

public class FragmentNews extends Fragment {

    private static final int GRID_NUM_COLUMNS = 3;
    String TAG = "FragmentNews";
    private static final String KEY_LAYOUT_POSITION = "layoutPosition";
    private ParallaxRecyclerView mRecyclerView;
    private FirebaseRecyclerAdapter<Post, ParallaxViewHolder> mAdapter;
    private int mRecyclerViewPosition = 0;

    private ValueEventListener mPersonInfoListener;
    private OnPostSelectedListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_news, container, false);

        mRecyclerView = (ParallaxRecyclerView) rootView.findViewById(R.id.recyclerViewNews);
        /*SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(mRecyclerView);*/
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        
        /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);*/

        /*mRecyclerView.addItemDecoration(new SpacesItemDecoration(32));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());*/
        mRecyclerView.setLayoutManager(linearLayoutManager);

        /*if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mRecyclerViewPosition = (int) savedInstanceState
                    .getSerializable(KEY_LAYOUT_POSITION);
            mRecyclerView.scrollToPosition(mRecyclerViewPosition);
        }*/

        Query allPostsQuery = FirebaseUtil.getPostsRef().orderByChild("likes");
        mAdapter = getFirebaseRecyclerAdapter(allPostsQuery);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        //final GridAdapter mGridAdapter = new GridAdapter();
        //mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), GRID_NUM_COLUMNS));
        //AddData(mGridAdapter);
        //mRecyclerView.setAdapter(mGridAdapter);

        return rootView;
    }


    private FirebaseRecyclerAdapter<Post, ParallaxViewHolder> getFirebaseRecyclerAdapter(Query query) {
        return new FirebaseRecyclerAdapter<Post, ParallaxViewHolder>(
                Post.class, R.layout.item_post_parallax, ParallaxViewHolder.class, query) {
            @Override
            public void populateViewHolder(final ParallaxViewHolder postViewHolder,
                                           final Post post, final int position) {
                setupPost(postViewHolder, post, position, null);
            }

            @Override
            public void onViewRecycled(ParallaxViewHolder holder) {
                super.onViewRecycled(holder);
//                FirebaseUtil.getLikesRef().child(holder.mPostKey).removeEventListener(holder.mLikeListener);
            }
        };
    }

    private void setupPost(final ParallaxViewHolder postViewHolder, final Post post, final int position, final String inPostKey) {
        postViewHolder.setImagePost(getActivity(), post.getThumbnail());
        postViewHolder.onClick(getActivity(), post.getThumbnail(), post.getUser_uid());
    }

    public interface OnPostSelectedListener {
        void onPostComment(String postKey);

        void onPostLike(String postKey);
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

    private void AddData(final GridAdapter mGridAdapter) {
        FirebaseUtil.getBaseRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Product person = dataSnapshot.getValue(Product.class);
                Log.d(TAG, "person: " + dataSnapshot.getRef());
                List<String> paths = new ArrayList<String>(person.getUsers_products().keySet());
                Collections.reverse(paths);
                mGridAdapter.addPaths(paths);
                //String firstPostKey = paths.get(0);

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

    }

    private class GridAdapter extends RecyclerView.Adapter<GridImageHolder> {
        private List<String> mPostPaths;

        public GridAdapter() {
            mPostPaths = new ArrayList<String>();
        }

        @Override
        public GridImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SquareImageView imageView = new SquareImageView(getContext());
            //int tileDimPx = getPixelsFromDps(100);
            //imageView.setLayoutParams(new GridView.LayoutParams(tileDimPx, tileDimPx));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(4, 4, 4, 4);

            return new GridImageHolder(imageView);
        }

        @Override
        public void onBindViewHolder(final GridImageHolder holder, int position) {
            DatabaseReference ref = FirebaseUtil.getPostsRef().child(mPostPaths.get(position));
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Post post = dataSnapshot.getValue(Post.class);
                    GlideUtil.loadImage(post.getThumbnail(), holder.imageView);
                    holder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(),
                                    "Selected: " + holder
                                            .getAdapterPosition(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    Log.e(TAG, "Unable to load grid image: " + firebaseError.getMessage());
                }
            });
        }

        public void addPath(String path) {
            mPostPaths.add(path);
            notifyItemInserted(mPostPaths.size());
        }

        public void addPaths(List<String> paths) {
            int startIndex = mPostPaths.size();
            mPostPaths.addAll(paths);
            notifyItemRangeInserted(startIndex, mPostPaths.size());
        }

        @Override
        public int getItemCount() {
            return mPostPaths.size();
        }

        private int getPixelsFromDps(int dps) {
            final float scale = getContext().getResources().getDisplayMetrics().density;
            return (int) (dps * scale + 0.5f);
        }
    }

    private class GridImageHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public GridImageHolder(ImageView itemView) {
            super(itemView);
            imageView = itemView;
        }
    }
}
