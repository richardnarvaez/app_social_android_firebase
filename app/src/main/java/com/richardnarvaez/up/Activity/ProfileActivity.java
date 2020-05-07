package com.richardnarvaez.up.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.richardnarvaez.up.Adapter.Firebase.HeaderViewHolder;
import com.richardnarvaez.up.Adapter.Firebase.InfiniteFireArray;
import com.richardnarvaez.up.Constants;
import com.richardnarvaez.up.Fragment.Home.EndRecylerViewListener;
import com.richardnarvaez.up.Fragment.Home.FragmentHome;
import com.richardnarvaez.up.Fragment.Home.InfiniteFireRecyclerViewAdapter;
import com.richardnarvaez.up.Model.Author;
import com.richardnarvaez.up.Model.Post;
import com.richardnarvaez.up.Model.PostYouTube;
import com.richardnarvaez.up.R;
import com.richardnarvaez.up.Utility.FirebaseUtil;
import com.richardnarvaez.up.Utility.GlideUtil;
import com.richardnarvaez.up.View.SquareImageView;
import com.richardnarvaez.up.ViewHolder.PostViewHolder;

public class ProfileActivity extends AppCompatActivity {

    String TAG = "ProfileActivity";

    public static final String NAME = "name";
    public static final String ID_USER = "id_user";
    public static final String USER_NAME = "user_name";

    private int GRID_NUM_COLUMNS = 3;
    CollapsingToolbarLayout collapsingToolbarLayout;

    private ValueEventListener mFollowingListener;
    private DatabaseReference mPeopleRef;
    public static String mUserId;
    private String currentUserId;
    private TextView text_followers;
    private TextView text_des_followers;
    TextView name, textuserName;
    private DatabaseReference refFeed;
    private InfiniteFireArray<String> array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        name = findViewById(R.id.textName);
        textuserName = findViewById(R.id.textuserName);
        name.setText(this.getIntent().getStringExtra(NAME));
        textuserName.setText(this.getIntent().getStringExtra(USER_NAME));


        collapsingToolbarLayout = findViewById(R.id.coll_profile);
        collapsingToolbarLayout.setTitle(" ");

        final ImageView profile = findViewById(R.id.profileUser);
        final TextView post_count = findViewById(R.id.text_post_count);

        text_followers = findViewById(R.id.textview_followers);
        text_des_followers = findViewById(R.id.text_des_follow);

        mUserId = getIntent().getStringExtra(ID_USER);

        final Button bt_follow = findViewById(R.id.bt_follow);
        bt_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follow();
            }
        });


        Button bt_edit_profile = findViewById(R.id.bt_edit_profile);
        Button bt_recommend = findViewById(R.id.bt_recommend);

        if (Objects.equals(FirebaseUtil.getCurrentUserId(), mUserId)) {
            bt_edit_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
                }
            });
        } else {
            bt_edit_profile.setVisibility(View.GONE);
            bt_recommend.setVisibility(View.VISIBLE);
        }

        FirebaseUtil.getPeopleRef().child(mUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            DataSnapshot data = dataSnapshot.child("author");
                            DataSnapshot products = dataSnapshot.child("post");

                            Author author = data.getValue(Author.class);
                            post_count.setText("" + products.getChildrenCount());
                            assert author != null;
                            String nameUSER = author.getName();
                            String usernameUSER = author.getUser_name();
                            String description = data.child("description").getValue(String.class);

                            name.setText(nameUSER);
                            textuserName.setText("@" + usernameUSER);

                            CollapsTitle(nameUSER);

                            LoadImageProfile(author, profile);

                        } else {
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                });

        RecyclerView recycler = findViewById(R.id.recyclerProfile);
        GridLayoutManager gridLayout = new GridLayoutManager(this, GRID_NUM_COLUMNS);
        gridLayout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return GRID_NUM_COLUMNS;
                }
                return 1;
            }
        });

        recycler.setLayoutManager(gridLayout);
        //recycler.addItemDecoration(new EqualSpacingItemDecoration(16));

        refFeed = FirebaseUtil.getPeopleRef().child(mUserId).child("post");

        array = new InfiniteFireArray<>(
                String.class,
                refFeed,
                15,
                30,
                false,
                false
        );

        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(array, new FragmentHome.RecyclerViewAdapter.OnSetupViewListener() {
            @Override
            public void onSetupView(PostViewHolder holder, Post post, int position, String postKey, int itemViewType) {
                //setupPost(holder, post, position, postKey, itemViewType);
            }

            @Override
            public void onSetupHeaderView(final HeaderViewHolder holder, Post post, int position, String postKey) {
                //setupHeader(holder);
            }

            @Override
            public void onSetupYouTubeView(final PostViewHolder postViewHolder, final PostYouTube post, final int position, final String inPostKey, int itemViewType) {

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

        mPeopleRef = FirebaseUtil.getPeopleRef();
        currentUserId = FirebaseUtil.getCurrentUserId();

        mFollowingListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                bt_follow.setText(dataSnapshot.exists() ? "UnFollow" : "Follow");
                final Drawable drawableFollow = getResources().getDrawable(R.drawable.vector_following);
                final Drawable drawableNoFollow = getResources().getDrawable(R.drawable.vector_no_following);
                bt_follow.setCompoundDrawablesWithIntrinsicBounds(dataSnapshot.exists() ? drawableNoFollow : drawableFollow, null, null, null);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        };

        if (currentUserId != null) {
            mPeopleRef.child(currentUserId).child("following").child(mUserId)
                    .addValueEventListener(mFollowingListener);
        }

    }//End Create

    public static class RecyclerViewAdapter extends InfiniteFireRecyclerViewAdapter<String> {

        public static final int VIEW_TYPE_HEADER = 0;
        public static final int VIEW_TYPE_CONTENT = 1;
        public static final int VIEW_TYPE_FOOTER = 2;
        public static final int VIEW_TYPE_LOADING = 3;

        private FragmentHome.RecyclerViewAdapter.OnSetupViewListener mOnSetupViewListener;

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

        public RecyclerViewAdapter(InfiniteFireArray snapshots, FragmentHome.RecyclerViewAdapter.OnSetupViewListener _mOnSetupViewListener) {
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
                    //SquareImageView imageView = new SquareImageView(parent.getContext());
                    //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    view = inflater.inflate(R.layout.item_square_post, parent, false);
                    viewHolder = new SquareImageHolder(view);
                    break;
                case VIEW_TYPE_HEADER:
                    view = inflater.inflate(R.layout.layout_header_profile, parent, false);
                    viewHolder = new HeaderProfileViewHolder(view);
                    break;
                case VIEW_TYPE_FOOTER:
                    view = inflater.inflate(R.layout.item_null, parent, false);
                    viewHolder = new FragmentHome.RecyclerViewAdapter.LoadingHolder(view);
                    break;
                case VIEW_TYPE_LOADING:
                    view = inflater.inflate(R.layout.item_loading_view, parent, false);
                    viewHolder = new FragmentHome.RecyclerViewAdapter.LoadingHolder(view);
                    break;

                default:
                    throw new IllegalArgumentException("Unknown type");
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof SquareImageHolder) {
                final SquareImageHolder hol = (SquareImageHolder) holder;

                final String key = snapshots.getItem(position - indexOffset).getKey();
                DatabaseReference ref = FirebaseUtil.getPostsRef().child(key);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            if (dataSnapshot.child("type").exists()) {
                                if (dataSnapshot.child("type").getValue(String.class).equals(Constants.TYPE_YOUTUBE)) {
                                    hol.playVideo.setVisibility(View.VISIBLE);
                                } else {
                                    hol.playVideo.setVisibility(View.GONE);
                                }
                            } else {
                                hol.playVideo.setVisibility(View.GONE);
                            }


                            GlideUtil.loadImage(dataSnapshot.child("full_url").getValue(String.class), hol.imageView);
                            hol.imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(hol.imageView.getContext(), DetailMinActivity.class);
                                    i.putExtra(DetailMinActivity.ID_USER, mUserId);
                                    i.putExtra(DetailMinActivity.POSITION, position);
                                    i.putExtra(DetailMinActivity.URL_PHOTO, dataSnapshot.child("full_url").getValue(String.class));
                                    i.putExtra(DetailMinActivity.URL_FULL_PHOTO, dataSnapshot.child("full_url").getValue(String.class));
                                    i.putExtra(DetailMinActivity.ID_PRODUCT, dataSnapshot.getKey());
                                    hol.imageView.getContext().startActivity(i);
                                }
                            });
                        } else {
                            FirebaseUtil.getPeopleRef().child(FirebaseUtil.getCurrentUserId()).child("post").child(key).setValue(null);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else if (holder instanceof HeaderProfileViewHolder) {
                final HeaderProfileViewHolder h = (HeaderProfileViewHolder) holder;
                FirebaseUtil.getPeopleRef().child(mUserId).child("author")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String description = dataSnapshot.child("description").getValue(String.class);

                                    if (description != null && !description.isEmpty()) {
                                        h.tvDescription.setVisibility(View.VISIBLE);
                                        h.tvDescription.setText(description);
                                    } else {
                                        h.tvDescription.setVisibility(View.GONE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError firebaseError) {

                            }
                        });

            }
        }

        private class SquareImageHolder extends RecyclerView.ViewHolder {
            public ImageView imageView, playVideo;

            public SquareImageHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.image);
                playVideo = view.findViewById(R.id.playVideo);
            }
        }

        private class HeaderProfileViewHolder extends RecyclerView.ViewHolder {
            public TextView tvDescription;

            public HeaderProfileViewHolder(View view) {
                super(view);
                tvDescription = view.findViewById(R.id.tvDescription);
            }
        }
    }

    private void AddData(final GridAdapter mGridAdapter) {
        FirebaseUtil.getPeopleRef().child(mUserId).child("post").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final List<String> paths = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "Add PRODUCT key: " + snapshot.getKey());
                    paths.add(snapshot.getKey());
                }

                /*Person person = dataSnapshot.getValue(Person.class);
                Log.d(TAG, "person" + dataSnapshot.getRef());
                List<String> paths = new ArrayList<String>(person.getProducts().keySet());*/

                Collections.reverse(paths);
                mGridAdapter.addPaths(paths);
                //String firstPostKey = paths.get(0);

                FirebaseUtil.getFollowersRef().child(mUserId).addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        int followers = (int) dataSnapshot.getChildrenCount();
                        text_followers.setText(followers + "");
                        text_des_followers.setText(followers == 1 ? "Follower" : "Followers");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError firebaseError) {

                    }

                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError firebaseError) {

            }
        });

    }

    private void follow() {

        if (currentUserId == null) {
            Toast.makeText(ProfileActivity.this, "You need to sign in to follow someone.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mPeopleRef.child(currentUserId).child("following").child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> updatedUserData = new HashMap<>();
                if (dataSnapshot.exists()) {
                    updatedUserData.put("users/" + currentUserId + "/following/" + mUserId, null);
                    updatedUserData.put("followers/" + mUserId + "/" + currentUserId, null);
                    FirebaseUtil.getFeedRef().child(FirebaseUtil.getCurrentUserId()).setValue(null);
                } else {
                    updatedUserData.put("followers/" + mUserId + "/" + currentUserId, true);
                    updatedUserData.put("users/" + currentUserId + "/following/" + mUserId, true);
                }
                FirebaseUtil.getBaseRef().updateChildren(updatedUserData, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            Toast.makeText(ProfileActivity.this, R.string.follow_user_error, Toast.LENGTH_LONG).show();
                            Log.d(TAG, getString(R.string.follow_user_error) + "\n" +
                                    firebaseError.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    Palette.PaletteAsyncListener paletteListener = new Palette.PaletteAsyncListener() {
        public void onGenerated(Palette palette) {
            Palette.Swatch vibrant =
                    palette.getVibrantSwatch();
            assert vibrant != null;
            try {
                int mutedColor = palette.getVibrantSwatch().getRgb();
                collapsingToolbarLayout.setBackgroundColor(mutedColor);
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(mutedColor));
                collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(mutedColor));
            } catch (Exception e) {
                Log.e(TAG, "No se pudo optener el color del Toolbar");
            }
        }
    };

    private void LoadImageProfile(Author data, final ImageView profile) {

        String image = data.getProfile_picture();

        GlideUtil.loadProfileIcon(image, profile);

        if (!data.getCover().isEmpty()) {
            GlideUtil.loadImage(data.getCover(), (ImageView) findViewById(R.id.backdrop));
        } else {
            GlideUtil.loadBlurImage(image, (ImageView) findViewById(R.id.backdrop), 25);
        }
    }

    private void CollapsTitle(final String name) {

        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    assert name != null;
                    collapsingToolbarLayout.setTitle(name);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void adapterHome() {

        DatabaseReference users = FirebaseUtil.getPeopleRef().child(getIntent().getStringExtra(ID_USER));
        assert users != null;
        users.child("products").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot followedUserSnapshot, String s) {

                String followedUserId = followedUserSnapshot.getKey();
                String lastKey = "";

                if (followedUserSnapshot.getValue() instanceof String) {
                    lastKey = followedUserSnapshot.getValue().toString();
                }

                Log.d(TAG, "Followed user id: " + followedUserId);
                Log.d(TAG, "Last key: " + lastKey);

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

    }

    private class GridAdapter extends RecyclerView.Adapter<GridImageHolder> {
        private List<String> mPostPaths;

        public GridAdapter() {
            mPostPaths = new ArrayList<String>();
        }

        @Override
        public GridImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SquareImageView imageView = new SquareImageView(ProfileActivity.this);
            //int tileDimPx = getPixelsFromDps(100);
            //imageView.setLayoutParams(new GridView.LayoutParams(tileDimPx, tileDimPx));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imageView.setPadding(8, 8, 8, 8);

            return new GridImageHolder(imageView);
        }

        @Override
        public void onBindViewHolder(final GridImageHolder holder, final int position) {

            DatabaseReference ref = FirebaseUtil.getPostsRef().child(mPostPaths.get(position));
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        FirebaseUtil.getPeopleRef().child(mUserId).child("post").child(dataSnapshot.getKey()).setValue(null);
                    } else {
                        final Post post = dataSnapshot.getValue(Post.class);
                        if (post != null && post.getFull_url() != null) {

                            GlideUtil.loadImage(post.getFull_url(), holder.imageView);

//                            holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
//                                @Override
//                                public boolean onLongClick(View v) {
//                                    Intent i = new Intent(ProfileActivity.this, DetailMinActivity.class);
//                                    i.putExtra(DetailMinActivity.POSITION, position);
//                                    i.putExtra(DetailMinActivity.URL_PHOTO, post.getThumbnail());
//                                    i.putExtra(DetailMinActivity.URL_FULL_PHOTO, post.getFull_url());
//                                    i.putExtra(DetailMinActivity.ID_PRODUCT, dataSnapshot.getKey());
//                                    startActivity(i);
//                                    return true;
//                                }
//                            });


                        }
                    }
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
            final float scale = ProfileActivity.this.getResources().getDisplayMetrics().density;
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
