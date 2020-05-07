package com.richardnarvaez.up.Fragment.Home;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.richardnarvaez.up.Adapter.Firebase.FirebasePostQueryAdapter;
import com.richardnarvaez.up.Adapter.Firebase.FirebaseRecyclerAdapter;
import com.richardnarvaez.up.Adapter.Firebase.FirebaseUserQueryAdapter;
import com.richardnarvaez.up.Adapter.Firebase.HeaderViewHolder;
import com.richardnarvaez.up.Adapter.Firebase.InfiniteFireArray;
import com.richardnarvaez.up.Adapter.Firebase.UserViewHolder;
import com.richardnarvaez.up.Constants;
import com.richardnarvaez.up.Interface.OnFeedPostListener;
import com.richardnarvaez.up.Live.LiveActivity;
import com.richardnarvaez.up.Model.Author;
import com.richardnarvaez.up.Model.Post;
import com.richardnarvaez.up.Model.PostYouTube;
import com.richardnarvaez.up.Utility.FirebaseUtil;
import com.richardnarvaez.up.Utility.GlideUtil;
import com.richardnarvaez.up.View.EqualSpacingItemDecoration;
import com.richardnarvaez.up.ViewHolder.PostViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.richardnarvaez.up.R;

/**
 * Created by RICHARD on 01/04/2017.
 */

public class FragmentHome extends Fragment {

    String TAG = "FragmentHome";
    public static RecyclerView recycler;
    public static RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;
    private OnFeedPostListener mListener;
    private int lastVisibleItem;
    FirebasePostQueryAdapter mAdapterF = null;
    String oldestKey;
    private int currentPage = 1;
    private InfiniteFireArray<String> array;
    private FrameLayout frame_no_connection;
    private LocationManager locationManager;
    private Location location;
    private String currentLocation;
    private String current_locality;
    private FrameLayout no_post;
    private Button btTryConf;
    private Button discover;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        rootView.findViewById(R.id.background).setBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.background_main));

        frame_no_connection = rootView.findViewById(R.id.no_connection_container);
        no_post = rootView.findViewById(R.id.no_post);
        discover = rootView.findViewById(R.id.discover);
        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Proximamente...", Toast.LENGTH_SHORT).show();
            }
        });

        btTryConf = rootView.findViewById(R.id.btTryConf);
        btTryConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        Button btTry = rootView.findViewById(R.id.btTry);
        btTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frame_no_connection.setVisibility(View.GONE);
            }
        });

        frame_no_connection.setVisibility(View.GONE);

        recycler = rootView.findViewById(R.id.recyclerView);

        mAdapterF = ((FirebasePostQueryAdapter) mAdapter);

        recycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recycler.setHasFixedSize(true);

        adapterHome();

        return rootView;

    }

    private void adapterHome() {

        DatabaseReference users = FirebaseUtil.getCurrentUserRef();
        assert users != null;

        users.child("following").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot followedUserSnapshot, String s) {

                if (followedUserSnapshot.exists()) {
                    String followedUserId = followedUserSnapshot.getKey();
                    String lastKey = "";

                    if (followedUserSnapshot.getValue() instanceof String) {
                        lastKey = followedUserSnapshot.getValue().toString();
                    }

                    FirebaseUtil.getPeopleRef().child(followedUserId).child("post")
                            .orderByKey().startAt(lastKey).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(final DataSnapshot postSnapshot, String s) {

                            HashMap<String, Object> addedPost = new HashMap<String, Object>();
                            addedPost.put(postSnapshot.getKey(), true);

                            FirebaseUtil.getFeedRef().child(FirebaseUtil.getCurrentUserId())
                                    .updateChildren(addedPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseUtil.getCurrentUserRef().child("following")
                                            .child(followedUserSnapshot.getKey())
                                            .setValue(postSnapshot.getKey());
                                }
                            });

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    Toast.makeText(getContext(), "No sigues a nadie aun.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        refFeed = FirebaseUtil.getFeedRef().child(FirebaseUtil.getCurrentUserId());

        array = new InfiniteFireArray<>(
                String.class,
                refFeed,
                5,
                2,
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
                setupHeader(holder);
            }

            @Override
            public void onSetupYouTubeView(final PostViewHolder postViewHolder, final PostYouTube post, final int position, final String inPostKey, int itemViewType) {
                postViewHolder.onClickImageVideo(getActivity(), post, inPostKey);
                postViewHolder.setPhoto(post.getThumbnail(), post.getFull_url());
                //postViewHolder.initYouTube(post.getVideo());
                postViewHolder.setData(post);

                if (itemViewType == 2) {
                    postViewHolder.setLocate(post.getCiudad());
                    postViewHolder.setDescription(post.getDescription());

                    postViewHolder.onClick(getActivity(), post.getThumbnail(), post.getUid_product(), post.getPrice(), "post.getVideo()", inPostKey);

                    assert post.getUser_uid() != null;
                    FirebaseUtil.getOtherPeopleRef(post.getUser_uid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        DataSnapshot ref = dataSnapshot.child("author");
                                        postViewHolder.setIcon(ref.child("profile_picture").getValue(String.class),
                                                dataSnapshot.getKey());
                                        postViewHolder.setAuthor(ref.child("name").getValue(String.class), dataSnapshot.getKey());
                                        postViewHolder.getVerified(ref.child("verified").getValue(Boolean.class));
                                    } else {
                                        //
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError firebaseError) {

                                }
                            });

                    postViewHolder.getLikeButtons(getActivity());
                    postViewHolder.setText(post.getName());

                    //postViewHolder.setPrice(post.getPrice());
                    //assert post.getCategory() != null;

                    postViewHolder.setTimestamp(DateUtils.getRelativeTimeSpanString(
                            (long) post.getDate()).toString());

                    final String postKey;
                    if (mAdapter instanceof FirebaseRecyclerAdapter) {
                        postKey = ((FirebaseRecyclerAdapter) mAdapter).getRef(position).getKey();
                    } else {
                        postKey = inPostKey;
                    }

                    ValueEventListener likeListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            postViewHolder.setNumLikes(dataSnapshot.getChildrenCount());
                            if (dataSnapshot.hasChild(FirebaseUtil.getCurrentUserId())) {
                                postViewHolder.setLikeStatus(PostViewHolder.LikeStatus.LIKED, getActivity());
                            } else {
                                postViewHolder.setLikeStatus(PostViewHolder.LikeStatus.NOT_LIKED, getActivity());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };

                    ValueEventListener saveListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                postViewHolder.setSaveStatus(PostViewHolder.LikeStatus.LIKED, getActivity());
                            } else {
                                postViewHolder.setSaveStatus(PostViewHolder.LikeStatus.NOT_LIKED, getActivity());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };

                    ValueEventListener commentListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Toast.makeText(getContext(), "Comment", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };

                    FirebaseUtil.getLikesRef().child(postKey).addValueEventListener(likeListener);
                    postViewHolder.mLikeListener = likeListener;

                    FirebaseUtil.getCurrentUserRef().child("save").child(postKey).addValueEventListener(saveListener);
                    postViewHolder.mSaveListener = saveListener;
                    postViewHolder.mCommentListener = commentListener;

                    postViewHolder.setDotsMenu();

                    if (mListener != null) {
                        postViewHolder.setPostClickListener(new PostViewHolder.PostClickListener() {
                            @Override
                            public void showComments() {
                                postViewHolder.onClickComment(getActivity(), post, inPostKey);
                                Log.e(TAG, "KEY: " + inPostKey);
                                Log.d(TAG, "Comment position: " + position);
                                mListener.onPostComment(postKey);
                            }

                            @Override
                            public void toggleSave() {
                                Log.d(TAG, "Save position: " + position);
                                mListener.onPostSave(postKey);
                            }

                            @Override
                            public void toggleLike() {
                                Log.d(TAG, "Like position: " + position);
                                mListener.onPostLike(postKey);
                            }
                        });
                    }
                }

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
                        //frame_no_connection.setVisibility(View.VISIBLE);
                        adapter.setInitiallyLoading(true);
                        adapter.setLoadingMore(false);
                        break;
                    case Done:
                        adapter.setInitiallyLoading(false);
                        adapter.setLoadingMore(false);
                        if (array.getCount() == 0) {
                            no_post.setVisibility(View.VISIBLE);
                        } else {
                            no_post.setVisibility(View.GONE);
                        }
                        break;
                }


            }
        });

        recycler.addOnScrollListener(new EndRecylerViewListener() {
            @Override
            public void onEndScroll(int lastItem) {
                super.onEndScroll(lastItem);

                if (lastItem <= array.getCount()) {
                    return;
                }

                array.more();
            }
        });

        recycler.setAdapter(adapter);

    }


    DatabaseReference refFeed;

    private void setupHeader(final HeaderViewHolder holder) {

        LinearLayoutManager lH = new LinearLayoutManager(getActivity()
                , LinearLayoutManager.HORIZONTAL, false);
        holder.recyclerView.setLayoutManager(lH);
        holder.recyclerView.addItemDecoration(new EqualSpacingItemDecoration(8));

        FirebaseUtil.getPeopleRef().child(Objects.requireNonNull(FirebaseUtil.getCurrentUserId())).child("following").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //final List<String> profilePaths = new ArrayList<>();
                        final FirebaseUserQueryAdapter adapterUser = new FirebaseUserQueryAdapter(null, 1,
                                new FirebaseUserQueryAdapter.OnSetupViewListener() {
                                    @Override
                                    public void onSetupView(PostViewHolder holder, Post post, int position, String postKey) {
                                        //Nada
                                    }

                                    @Override
                                    public void onSetupUserView(final UserViewHolder holder, final Author post, final int adapterPosition, final String key) {
                                        FirebaseUtil.getPeopleRef().child(key).child("post").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    String key = null;

                                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                        Log.d("User key: ", child.getKey());
                                                        key = child.getKey();
                                                    }

                                                    FirebaseUtil.getPostsRef().child(key).child("full_url").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                            if (dataSnapshot.exists()) {
                                                                if (!Objects.requireNonNull(dataSnapshot.getValue(String.class)).isEmpty()) {
                                                                    holder.backImage.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            startActivity(new Intent(getContext(), LiveActivity.class));
                                                                        }
                                                                    });
                                                                    GlideUtil.loadImage(dataSnapshot.getValue(String.class), holder.backImage);
                                                                    holder.setImageUser(post.getProfile_picture());
                                                                }
                                                            } else {
                                                                //profilePaths.remove(finalKey);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                });

                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            FirebaseUtil.getPeopleRef().child(Objects.requireNonNull(snapshot.getKey())).child("post").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String key = null;

                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            key = child.getKey();
                                        }

                                        assert key != null;
                                        FirebaseUtil.getPostsRef().child(key).child("full_url").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.exists()) {
                                                    adapterUser.addItem(snapshot.getKey());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        holder.recyclerView.setAdapter(adapterUser);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );


        holder.bubbleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "FirstButton", Toast.LENGTH_SHORT).show();
            }
        });

        holder.switchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "SecondButton", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void setupPost(final PostViewHolder postViewHolder, final Post post, final int position, final String inPostKey, int itemViewType) {

        postViewHolder.setPhoto(post.getThumbnail(), post.getFull_url());
        postViewHolder.onClickImage(getActivity(), post, inPostKey);

        if (itemViewType == 2) {
            postViewHolder.setText(post.getName());
            postViewHolder.setDescription(post.getDescription());
            postViewHolder.setLocate(post.getCiudad());

            postViewHolder.onClick(getActivity(), post.getThumbnail(), post.getUid_product(), post.getPrice(), "post.getVideo()", inPostKey);

            assert post.getUser_uid() != null;
            FirebaseUtil.getOtherPeopleRef(post.getUser_uid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                DataSnapshot ref = dataSnapshot.child("author");
                                postViewHolder.setIcon(ref.child("profile_picture").getValue(String.class),
                                        dataSnapshot.getKey());
                                postViewHolder.setAuthor(ref.child("name").getValue(String.class), dataSnapshot.getKey());
                                postViewHolder.getVerified(ref.child("verified").getValue(Boolean.class));
                            } else {
                                //
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {

                        }
                    });

            postViewHolder.getLikeButtons(getActivity());


            //postViewHolder.setPrice(post.getPrice());
            //assert post.getCategory() != null;
            //postViewHolder.setCategory(post.getCategory());

            postViewHolder.setTimestamp(DateUtils.getRelativeTimeSpanString(
                    (long) post.getDate()).toString());

            final String postKey;
            if (mAdapter instanceof FirebaseRecyclerAdapter) {
                postKey = ((FirebaseRecyclerAdapter) mAdapter).getRef(position).getKey();
            } else {
                postKey = inPostKey;
            }

            ValueEventListener likeListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    postViewHolder.setNumLikes(dataSnapshot.getChildrenCount());
                    if (dataSnapshot.hasChild(FirebaseUtil.getCurrentUserId())) {
                        postViewHolder.setLikeStatus(PostViewHolder.LikeStatus.LIKED, getActivity());
                    } else {
                        postViewHolder.setLikeStatus(PostViewHolder.LikeStatus.NOT_LIKED, getActivity());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            ValueEventListener saveListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        postViewHolder.setSaveStatus(PostViewHolder.LikeStatus.LIKED, getActivity());
                    } else {
                        postViewHolder.setSaveStatus(PostViewHolder.LikeStatus.NOT_LIKED, getActivity());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            ValueEventListener commentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Toast.makeText(getContext(), "Comment", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            FirebaseUtil.getLikesRef().child(postKey).addValueEventListener(likeListener);
            postViewHolder.mLikeListener = likeListener;

            FirebaseUtil.getCurrentUserRef().child("save").child(postKey).addValueEventListener(saveListener);
            postViewHolder.mSaveListener = saveListener;
            postViewHolder.mCommentListener = commentListener;

            postViewHolder.setDotsMenu();

            if (mListener != null) {
                postViewHolder.setPostClickListener(new PostViewHolder.PostClickListener() {
                    @Override
                    public void showComments() {
                        postViewHolder.onClickComment(getActivity(), post, inPostKey);
                        Log.e(TAG, "KEY: " + inPostKey);
                        Log.d(TAG, "Comment position: " + position);
                        mListener.onPostComment(postKey);
                    }

                    @Override
                    public void toggleSave() {
                        Log.d(TAG, "Save position: " + position);
                        mListener.onPostSave(postKey);
                    }

                    @Override
                    public void toggleLike() {
                        Log.d(TAG, "Like position: " + position);
                        mListener.onPostLike(postKey);
                    }
                });
            }
        }
    }

    public static class RecyclerViewAdapter extends InfiniteFireRecyclerViewAdapter<String> {

        public static final int VIEW_TYPE_HEADER = 0;
        public static final int VIEW_TYPE_CONTENT = 1;
        public static final int VIEW_TYPE_FOOTER = 2;
        public static final int VIEW_TYPE_LOADING = 3;

        private OnSetupViewListener mOnSetupViewListener;

        public interface OnSetupViewListener {

            void onSetupView(PostViewHolder holder, Post post, int position, String postKey, int itemViewType);

            void onSetupHeaderView(HeaderViewHolder holder, Post post, int position, String postKey);

            void onSetupYouTubeView(PostViewHolder contentHolder, PostYouTube post, int adapterPosition, String key, int i);

        }

        public static class LoadingHolder extends RecyclerView.ViewHolder {
            public LinearLayout init;
            public RelativeLayout finish;

            public LoadingHolder(View view) {
                super(view);
                init = view.findViewById(R.id.init);
                finish = view.findViewById(R.id.finish);
            }
        }

        private boolean initiallyLoading = true;
        private boolean loadingMore = false;

        public RecyclerViewAdapter(InfiniteFireArray snapshots, OnSetupViewListener _mOnSetupViewListener) {
            super(snapshots, 1, 1);
            this.mOnSetupViewListener = _mOnSetupViewListener;
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

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder;
            View view;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            switch (viewType) {
                case VIEW_TYPE_CONTENT:
                    view = inflater.inflate(R.layout.item_post_home, parent, false);
                    viewHolder = new PostViewHolder(view);
                    break;
                case VIEW_TYPE_HEADER:
                    view = inflater.inflate(R.layout.item_head_feed, parent, false);
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
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            switch (viewType) {
                case VIEW_TYPE_CONTENT:

                    final PostViewHolder contentHolder = (PostViewHolder) holder;
                    //String value = snapshots.getItem(position - indexOffset).getValue();
                    final String key = snapshots.getItem(position - indexOffset).getKey();

                    DatabaseReference ref = FirebaseUtil.getPostsRef().child(key);

                    contentHolder.hideItems();

                    ValueEventListener postListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.e("TAG", dataSnapshot.getKey() + " " + dataSnapshot.child("type").exists());

                            if (dataSnapshot.exists()) {

                                contentHolder.endLoadPost();

                                if (dataSnapshot.child("type").exists()) {
                                    String type = dataSnapshot.child("type").getValue(String.class);
                                    assert type != null;
                                    switch (type) {
                                        case Constants.TYPE_VIDEO:
                                            //contentHolder.post_type_video.setVisibility(View.VISIBLE);
                                            break;

                                        case Constants.TYPE_PHOTO:
                                            contentHolder.post_type_photo.setVisibility(View.VISIBLE);
                                            contentHolder.post_type_photo.setVisibility(View.VISIBLE);
                                            Post post = dataSnapshot.getValue(Post.class);
                                            mOnSetupViewListener.onSetupView(contentHolder, post, contentHolder.getAdapterPosition(),
                                                    key,
                                                    2);
                                            break;
                                        case Constants.TYPE_YOUTUBE:
                                            contentHolder.post_type_photo.setVisibility(View.VISIBLE);
                                            contentHolder.post_type_youtube.setVisibility(View.VISIBLE);
                                            PostYouTube postYt = dataSnapshot.getValue(PostYouTube.class);
                                            mOnSetupViewListener.onSetupYouTubeView(contentHolder, postYt, contentHolder.getAdapterPosition(),
                                                    key,
                                                    2);
                                            break;
                                        case Constants.TYPE_TEXT:
                                            contentHolder.post_type_text.setVisibility(View.VISIBLE);
                                            break;
                                    }
                                } else {
                                    contentHolder.post_type_photo.setVisibility(View.VISIBLE);
                                    Post post = dataSnapshot.getValue(Post.class);
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
                    headerHolder.finishView.setVisibility((isInitiallyLoading()) ? View.GONE : View.VISIBLE);
                    headerHolder.initView.setVisibility((isInitiallyLoading()) ? View.VISIBLE : View.GONE);
                    break;
                case VIEW_TYPE_LOADING:
                    LoadingHolder loadingHolder = (LoadingHolder) holder;
                    //loadingHolder.progressBar.setVisibility((isLoadingMore()) ? View.VISIBLE : View.GONE);
                    break;
                case VIEW_TYPE_FOOTER:
                    LoadingHolder footerHolder = (LoadingHolder) holder;


//                    if (isInitiallyLoading()) {
//                        footerHolder.init.setVisibility(View.VISIBLE);
//                        footerHolder.finish.setVisibility(View.GONE);
//                    } else {
//                        footerHolder.init.setVisibility((isLoadingMore()) ? View.VISIBLE : View.GONE);
//                        footerHolder.finish.setVisibility((isLoadingMore()) ? View.GONE : View.VISIBLE);
//                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown type");
            }
        }

        private final static int FADE_DURATION = 1000;

        private void setAnimation(View view) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(FADE_DURATION);
            view.startAnimation(anim);
        }

        @Override
        public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder) {
            recycler.clearAnimation();
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFeedPostListener) {
            mListener = (OnFeedPostListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPostSelectedListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null && mAdapter instanceof FirebaseRecyclerAdapter) {
            ((FirebaseRecyclerAdapter) mAdapter).cleanup();
        }
    }

    private void loadNext(final int lastItem) {

        Log.e(TAG, "FIN DE LA LISTA>> ESTADO: " + mAdapterF.getTypeList());
        if (mAdapterF.getTypeList()) {
            Log.e(TAG, "ESTAMOS EN GRID");
        } else {
            refFeed.orderByKey()
                    .endAt(oldestKey)
                    .limitToLast(11)//currentPage * visibleThreshold)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.e(TAG, "Item of Load: " + lastItem);
                            Log.e(TAG, "Total items: " + dataSnapshot.getChildrenCount());

                            if (dataSnapshot.exists()) {
                                final List<String> post = new ArrayList<>();
                                post.clear();

                                if (mAdapterF.getItemCount() <= dataSnapshot.getChildrenCount() - 1) {
                                    currentPage++;
                                    lastVisibleItem = lastVisibleItem + 10;
                                    Log.e(TAG, "PAGE: " + currentPage);
                                }

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    post.add(snapshot.getKey());
                                }

                                Collections.reverse(post);

                                oldestKey = post.get(post.size() - 1);
                                for (int a = 1; a <= post.size() - 1; a++) {
                                    mAdapterF.addItem(post.get(a));
                                }

                                //recycler.setPullLoadMoreCompleted();
                                //mAdapterF.setFinishLoaded();
                                //isLoading = false;

                            } else {
                                Log.e(TAG, "No more");
                                //recycler.setPullLoadMoreCompleted();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Hubo un error para obtener mas POST", Toast.LENGTH_SHORT).show();
                        }
                    });
        }


    }

}
