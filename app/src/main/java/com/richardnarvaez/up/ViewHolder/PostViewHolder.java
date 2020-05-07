package com.richardnarvaez.up.ViewHolder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;
import com.richardnarvaez.up.Activity.DetailActivity;
import com.richardnarvaez.up.Activity.ProfileActivity;
import com.richardnarvaez.up.Activity.VideoActivity;
import com.richardnarvaez.up.Model.PostYouTube;
import com.richardnarvaez.up.Utility.GlideUtil;
import com.richardnarvaez.up.Utility.Utils;

import com.richardnarvaez.up.Activity.ImageActivity;
import com.richardnarvaez.up.Activity.ProgressTarget;
import com.richardnarvaez.up.Model.Post;
import com.richardnarvaez.up.R;
import com.richardnarvaez.up.Utility.FirebaseUtil;

import java.io.Serializable;

import xyz.klinker.android.drag_dismiss.DragDismissIntentBuilder;


/**
 * Created by RICHARD on 09/04/2017.
 */

public class PostViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    private final RelativeLayout image_verified;
    private final TextView post_place;
    public final FrameLayout post_type_youtube, post_type_text, post_type_photo, post_type_load;
    private final TextView post_type_youtube_credits;
    private final RelativeLayout share_button;
    private PostClickListener mListener;
    public DatabaseReference mPostRef;
    public ValueEventListener mPostListener;

    private ImageView ex;

    int cantidad;

    public void onClickComment(FragmentActivity activity, Post post, String inPostKey) {
        Intent i = new Intent(activity, DetailActivity.class);
        new DragDismissIntentBuilder(activity)
                .setTheme(DragDismissIntentBuilder.Theme.LIGHT)
                .setPrimaryColorResource(android.R.color.transparent)
                .setShowToolbar(false)
                .setFullscreenOnTablets(true)
                .setDrawUnderStatusBar(true)
                .setDragElasticity(DragDismissIntentBuilder.DragElasticity.XXLARGE)
                .build(i);
        i.putExtra(DetailActivity.URL_THUM, post.getThumbnail());
        i.putExtra(DetailActivity.URL_FULL, post.getThumbnail());
        i.putExtra(DetailActivity.POST_KEY, inPostKey);
        i.putExtra(DetailActivity.ID_USER, post.getUser_uid());
        i.putExtra(DetailActivity.TYPE, "comment");
        activity.startActivity(i);
    }

    public void setPhotoOther(String thumbnail) {
        GlideUtil.loadImage(thumbnail, mPhotoView);
    }

    @SuppressLint("SetTextI18n")
    public void setLocate(String ciudad) {
        if (ciudad != null) {
            post_place.setText(ciudad);
        }
    }

    public void initYouTube(final String video) {
        if (!video.isEmpty()) {
            YouTubePlayerView youTubePlayerView = mView.findViewById(R.id.youtube_player_view);
            //context.getLifecycle().addObserver(youTubePlayerView);
            youTubePlayerView.getPlayerUIController().showFullscreenButton(false);
            youTubePlayerView.getPlayerUIController().showYouTubeButton(false);
            youTubePlayerView.initialize(new YouTubePlayerInitListener() {
                @Override
                public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
                    initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady() {
                            initializedYouTubePlayer.cueVideo(video, 0);
                        }
                    });
                }
            }, true);
        }
    }

    public void hideItems() {
        post_type_load.setVisibility(View.VISIBLE);
        post_type_photo.setVisibility(View.GONE);
        post_type_youtube.setVisibility(View.GONE);
        post_type_text.setVisibility(View.GONE);
    }

    public void endLoadPost() {
        post_type_load.setVisibility(View.GONE);
    }

    public void setData(PostYouTube post) {
        post_type_youtube_credits.setText(post.getCredits());
    }

    public void onClickImageVideo(final Activity context, final PostYouTube post, final String inPostKey) {
        videoImage.setVisibility(View.VISIBLE);

        post_type_photo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 680));

        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, VideoActivity.class);
                i.putExtra(VideoActivity.POST, post.getVideo());
                i.putExtra(VideoActivity.SUBTITLE, post.getCredits());
                i.putExtra(VideoActivity.TITLE, post.getName());
                context.startActivity(i);
            }
        });
    }


    public enum LikeStatus {LIKED, NOT_LIKED}

    private ImageView dotsMenu;
    private RelativeLayout mLikeIconR;
    private ImageView mLikeIcon;
    private RelativeLayout comment;
    private static final int POST_TEXT_MAX_LINES = 6;
    public ImageView mPhotoView;
    private ImageView mIconView;
    private TextView mAuthorView;
    private TextView mPostTextView;
    private TextView mPriceTextView;
    private TextView mTimestampView;
    private TextView mNumLikesView;
    private ImageView shader;
    public String mPostKey;
    LinearLayout bt_Profile;
    public CardView card_Image, card_Image2;
    public ValueEventListener mLikeListener;
    public ValueEventListener mSaveListener;
    public ValueEventListener mCommentListener;
    ImageView like;
    RelativeLayout backLikeButton;
    ImageView up, down;
    TextView text_item;
    TextView txt_vote;

    TextView username, name;
    ProgressBar progress;

    private TextView description;
    ImageView videoImage;

    public PostViewHolder(View itemView) {
        super(itemView);
        mView = itemView;

        post_type_load = itemView.findViewById(R.id.post_type_load);
        post_type_youtube = itemView.findViewById(R.id.post_type_youtube);
        post_type_text = itemView.findViewById(R.id.post_type_text);
        post_type_photo = itemView.findViewById(R.id.post_type_photo);
        post_type_youtube_credits = itemView.findViewById(R.id.credits);

        videoImage = itemView.findViewById(R.id.videoImage);
        //backShadown = itemView.findViewById(R.id.backShadown);
        ex = (ImageView) itemView.findViewById(R.id.delete);

        dotsMenu = itemView.findViewById(R.id.dots_menu);
        up = (ImageView) itemView.findViewById(R.id.up);
        down = (ImageView) itemView.findViewById(R.id.down);
        text_item = (TextView) itemView.findViewById(R.id.numero_item);
        txt_vote = itemView.findViewById(R.id.text_vote);
        description = itemView.findViewById(R.id.text_description);
        post_place = itemView.findViewById(R.id.post_place);

        backLikeButton = (RelativeLayout) itemView.findViewById(R.id.like_button);

        name = (TextView) itemView.findViewById(R.id.text_profile_name);
        username = (TextView) itemView.findViewById(R.id.text_profile_user_name);

        card_Image = (CardView) itemView.findViewById(R.id.cardProduct);
        card_Image2 = (CardView) itemView.findViewById(R.id.cardProduct2);
        //shader = (ImageView) itemView.findViewById(R.id.shader);
        bt_Profile = (LinearLayout) itemView.findViewById(R.id.button_profile);

        /*Profile info*/
        mPhotoView = itemView.findViewById(R.id.post_photo);
        mIconView = mView.findViewById(R.id.post_author_icon);
        mAuthorView = mView.findViewById(R.id.post_author_name);

        mPostTextView = (TextView) itemView.findViewById(R.id.post_text_title);
        mPriceTextView = (TextView) itemView.findViewById(R.id.tvCategory);
        mTimestampView = (TextView) itemView.findViewById(R.id.post_timestamp);
        mNumLikesView = (TextView) itemView.findViewById(R.id.post_num_likes);
        image_verified = itemView.findViewById(R.id.image_verified);
        share_button = itemView.findViewById(R.id.share_button);

        progress = mView.findViewById(R.id.progress);

    }

    public void getVerified(boolean verified) {
        if (verified) {
            image_verified.setVisibility(View.VISIBLE);
        } else {
            image_verified.setVisibility(View.GONE);
        }
    }

    public void getLikeButtons(final FragmentActivity activity) {
        like = itemView.findViewById(R.id.like);

        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //share_button.startAnimation(AnimationUtils.loadAnimation(backLikeButton.getContext(), R.anim.imagen_click));
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
                //builderSingle.setIcon(R.mipmap.ic_launcher);
                builderSingle.setTitle("Share");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1);
                arrayAdapter.add("New Story");
                arrayAdapter.add("Facebook");
                arrayAdapter.add("Instagram");
                arrayAdapter.add("Whatsapp");
                arrayAdapter.add("Otro");

                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(activity);
                        builderInner.setMessage("Por el momento no esta disponible, si te deseas apoyar al proyecto para poder usar estas funcionalidades puedes donar en el menu inicial 'Donaciones'.");
                        builderInner.setTitle(strName);
                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builderInner.show();
                    }
                });
                builderSingle.show();
            }
        });

        backLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backLikeButton.startAnimation(AnimationUtils.loadAnimation(backLikeButton.getContext(), R.anim.imagen_click));
                mListener.toggleLike();
            }
        });

        mLikeIconR = itemView.findViewById(R.id.save);
        mLikeIcon = itemView.findViewById(R.id.saveImage);
        mLikeIconR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLikeIconR.startAnimation(AnimationUtils.loadAnimation(mLikeIconR.getContext(), R.anim.imagen_click));
                mListener.toggleSave();
            }
        });

        comment = itemView.findViewById(R.id.comment);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.showComments();
            }
        });
    }

    public void onClickImage(final FragmentActivity context, final Post post, final String inPostKey) {
        videoImage.setVisibility(View.GONE);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ImageActivity.class);
                Pair<View, String> pairImage = Pair.create(mView.findViewById(R.id.post_photo), "imageThumb");
                //Pair<View, String> pairTitle = Pair.create(mView.findViewById(R.id.post_text_title), "txtTitle");
                //Pair<View, String> pairCategory = Pair.create(mView.findViewById(R.id.tvCategory), "txtCategory");
                //Pair<View, String> pairProfileImage = Pair.create(mView.findViewById(R.id.post_author_icon), "profileImage");
                //Pair<View, String> pairCard = Pair.create(mView.findViewById(R.id.cardProduct), "cardContent");

                i.putExtra(ImageActivity.TYPE, "image");
                i.putExtra(ImageActivity.TITLE, post.getName());
                i.putExtra(ImageActivity.CATEGORY, post.getCategory());
                i.putExtra(ImageActivity.URL_THUM, post.getFull_url());
                i.putExtra(ImageActivity.ID_USER, post.getUser_uid());
                i.putExtra(ImageActivity.DATE, (long) post.getDate());
                i.putExtra(ImageActivity.POST_KEY, inPostKey);

                if (mView.findViewById(R.id.post_text_title) != null) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context, pairImage);
                    context.startActivity(i, options.toBundle());
                } else {
                    context.startActivity(i);
                }
            }
        });
    }


    public void onClick(final Activity context, final String url, final String id, final Long price, final String video, final String mPostKey) {

//        mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                //easyFlipView.flipTheView();
//                Vibrator vb = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
//                vb.vibrate(50);
//                return true;
//            }
//        });

        /*Flip View*/
        //easyFlipView.flipTheView();

        /*VER DetailProductMaxActivity*/
                /*Intent i = new Intent(context, DetailProductMaxActivity.class);
                i.putExtra(DetailProductMaxActivity.PHOTO, url);
                i.putExtra(DetailProductMaxActivity.ID_PRODUCT, id);
                if (price != null) {
                    i.putExtra(DetailProductMaxActivity.PRICE, price.toString());
                } else {
                    i.putExtra(DetailProductMaxActivity.PRICE, "25");
                }
                i.putExtra(DetailProductMaxActivity.USER_NAME_PRODUCT, mPostTextView.getText());
                i.putExtra(DetailProductMaxActivity.USER_NAME, mAuthorView.getText());
                i.putExtra(DetailProductMaxActivity.ID_VIDEO, video);
                i.putExtra(DetailProductMaxActivity.ID_PRODUCT_POST, mPostKey);
                context.startActivity(i);*/



                /*AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                View view = context.getLayoutInflater().inflate(R.layout.layout_dialog, null);
                mBuilder.setView(view);
                Blurry.with(context)
                        .radius(25)
                        .sampling(2)
                        //.color(Color.argb(66, 255, 255, 0))
                        .async()
                        .animate(200)
                        .onto(blur);*/

                /*AlertDialog dialog = mBuilder.create();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Blurry.delete(blur);
                    }
                });

                dialog.show();*/

    }

    private ProgressTarget<String, Bitmap> target;

    public void setPhoto(String url, String full_url) {
        //target = new ImageActivity.MyProgressTarget<>(new BitmapImageViewTarget(mPhotoView), progress, mPhotoView, null);
        //image.setOnTouchListener(ImageActivity.this);
        GlideUtil.loadImagewtProgress(full_url, mPhotoView, progress);
        //GlideUtil.loadBlurImage(url, backShadown,6);
        //GlideUtil.loadImage(url, mPhotoView);
    }

    public void setPhoto(String url) {
        //GlideUtil.load2Parts(url, full_url, mPhotoView);
    }

    public void setPhotoSave(String url) {
        // GlideUtil.loadMax(url, mPhotoView);
    }

    public void setIcon(String url, final String authorId) {
        GlideUtil.loadProfileIcon(url, mIconView);
        mIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDetail(authorId);
            }
        });
    }

    public void setAuthor(String author, final String authorId) {
        if (author == null || author.isEmpty()) {
            author = mView.getResources().getString(R.string.user_info_no_name);
        }
        mAuthorView.setText(author);
        mAuthorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDetail(authorId);
            }
        });
    }

    private void showUserDetail(String authorId) {
        Activity context = (Activity) mView.getContext();
        Intent userDetailIntent = new Intent(context, ProfileActivity.class);
//        Pair<View, String> pairProfile = Pair.create((View) mIconView, "tran_img_profile");
//        Pair<View, String> pairProfileName = Pair.create((View) mAuthorView, "tran_tv_profile");
//        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context, pairProfile, pairProfileName);
        userDetailIntent.putExtra(ProfileActivity.ID_USER, authorId);
        context.startActivity(userDetailIntent);
    }

    public void setPrice(Long price) {
        if (price != null) {
            mPriceTextView.setText("$" + price.toString());
        }
    }

    public void setText(final String text) {
        if (text == null || text.isEmpty() || text.equals(" ")) {
            mPostTextView.setVisibility(View.GONE);
        } else {
            mPostTextView.setVisibility(View.VISIBLE);
            mPostTextView.setText(text);
            mPostTextView.setMaxLines(POST_TEXT_MAX_LINES);
            mPostTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPostTextView.getMaxLines() == POST_TEXT_MAX_LINES) {
                        mPostTextView.setMaxLines(Integer.MAX_VALUE);
                    } else {
                        mPostTextView.setMaxLines(POST_TEXT_MAX_LINES);
                    }
                }
            });
        }
    }

    public void setTimestamp(String timestamp) {
        if (!post_place.getText().toString().isEmpty()) {
            mTimestampView.setText(timestamp + "  •  ");
        } else {
            mTimestampView.setText(timestamp);
        }
    }

    @SuppressLint("SetTextI18n")
    public void setNumLikes(long numLikes) {
        String suffix = numLikes == 1 ? " vote" : " votes";
        mNumLikesView.setText(numLikes + suffix + " • " + "comments");
    }

    public void setPostClickListener(PostClickListener listener) {
        mListener = listener;
    }

    public void setSaveStatus(LikeStatus status, Activity context) {

        mLikeIcon.setImageResource(
                status == LikeStatus.LIKED ? R.drawable.vector_check : R.drawable.vector_marker);
        mLikeIcon.setColorFilter(ContextCompat.getColor(mLikeIcon.getContext(), status == LikeStatus.LIKED ? R.color.green_d : R.color.colorNoSelect));
        /*txt_save.setTextColor(
                ContextCompat.getColor(context,
                        status == LikeStatus.LIKED ? R.color.green_d : R.color.colorNoSelect));*/
    }

    public void setLikeStatus(LikeStatus status, Activity context) {
        //Color in ICON LIKE
        /*like.setColorFilter(
                ContextCompat.getColor(context,
                        status == LikeStatus.LIKED ? R.color.colorWhite : R.color.colorTextDisabledLight)
        );*/

        if (status != LikeStatus.LIKED) {
            //if no LIKED
            //mNumLikesView.setText(" Up");
        }

        //Drawable down = ContextCompat.getDrawable(context, R.drawable.vector_up);
        int colorChange = ContextCompat.getColor(like.getContext(), status == LikeStatus.LIKED ? R.color.red_d : R.color.blue_d);
        //int colorChange = ContextCompat.getColor(context, status == LikeStatus.LIKED ? R.color.red_d : R.color.blue_d);


        like.setColorFilter(colorChange);
        like.setImageResource(
                status == LikeStatus.LIKED ? R.drawable.vector_down : R.drawable.vector_up);

        txt_vote.setText(status == LikeStatus.LIKED ? " Down" : "Up");
        txt_vote.setTextColor(colorChange);

        /*backLikeButton.setBackground(
                mView.getResources()
                        .getDrawable(status == LikeStatus.LIKED ? R.drawable.chip_like : R.drawable.chip)
        );*/
    }

    public void setOnClick(final String mPostKey) {
        cantidad = 1;
        setCount();

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cantidad += 1;
                setCount();
            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cantidad -= 1;
                setCount();
            }
        });

        ex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtil.getCurrentUserRef().child("buy").child(mPostKey).setValue(null);

            }
        });
    }

    void setCount() {
        if (cantidad <= 0) {
            cantidad = 0;
        }
        text_item.setText(cantidad <= 9 ? "0" + cantidad : "" + cantidad);
    }

    public void setProfile(String t_name, String t_user_name) {
        name.setText(t_name);
        username.setText(t_user_name);
    }

    @SuppressLint("SetTextI18n")
    public void setCategory(String categoria) {
        //mPriceTextView.setText("#" + (mPriceTextView.getContext().getResources().getStringArray(R.array.normal_arrays)[pos]).toLowerCase());
        if (categoria.isEmpty()) {
            mPriceTextView.setVisibility(View.GONE);
        } else {
            mPriceTextView.setText('#' + categoria);
        }
    }

    public void setDotsMenu() {
        dotsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.inflate(R.menu.menu_dots_home_item);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int i = item.getItemId();
                        if (i == R.id.menu_save) {//handle menu1 click

                        } else if (i == R.id.menu_report) {//handle menu2 click

                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

    }

    public void setDescription(String description) {
        if (!empty(description)) {
            this.description.setVisibility(View.VISIBLE);
            Utils.setTags(this.description, description);
        } else {
            this.description.setVisibility(View.GONE);
        }
    }


    static boolean empty(final String s) {
        return s == null || s.trim().isEmpty();
    }

    public interface PostClickListener {
        void showComments();

        void toggleSave();

        void toggleLike();
    }
}