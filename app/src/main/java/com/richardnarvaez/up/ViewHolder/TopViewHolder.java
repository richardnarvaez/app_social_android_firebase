package com.richardnarvaez.up.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;
import com.richardnarvaez.up.Activity.ImageActivity;
import com.richardnarvaez.up.Model.Post;
import com.richardnarvaez.up.R;
import com.richardnarvaez.up.Utility.FirebaseUtil;
import com.richardnarvaez.up.Utility.GlideUtil;

public class TopViewHolder extends RecyclerView.ViewHolder {

    public final YouTubePlayerView post_type_youtube;
    private final LinearLayout btUp;
    public ImageView image, thumbUser;
    TextView nameUser, titlePost;
    TextView txtPosition, numUp;
    ImageView up, down;
    Context context;

    public TopViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        image = itemView.findViewById(R.id.imagePost);
        thumbUser = itemView.findViewById(R.id.thumbUser);
        nameUser = itemView.findViewById(R.id.nameUser);
        titlePost = itemView.findViewById(R.id.titlePost);
        txtPosition = itemView.findViewById(R.id.txtPosition);
        post_type_youtube = itemView.findViewById(R.id.youtube_player_view);

        /*BUTTON UP*/
        btUp = itemView.findViewById(R.id.btUp);
        numUp = itemView.findViewById(R.id.numUp);
        up = itemView.findViewById(R.id.up);
        down = itemView.findViewById(R.id.down);

    }

    public void setData(final Post post, final String key) {

        ValueEventListener likeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(FirebaseUtil.getCurrentUserId()).exists()) {
                    setLikeStatus(PostViewHolder.LikeStatus.LIKED, dataSnapshot.getChildrenCount());
                } else {
                    setLikeStatus(PostViewHolder.LikeStatus.NOT_LIKED, dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        FirebaseUtil.getLikesRef().child(key).addValueEventListener(likeListener);

        btUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtil.toggleLikes(key);
            }
        });

        GlideUtil.loadImage(post.getFull_url(), image);
        titlePost.setText(post.getName());
        FirebaseUtil.getPeopleRef().child(post.getUser_uid()).child("author").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GlideUtil.loadProfileIcon(dataSnapshot.child("profile_picture").getValue(String.class), thumbUser);
                nameUser.setText(dataSnapshot.child("name").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setLikeStatus(PostViewHolder.LikeStatus status, long likes) {
        int colorChange = ContextCompat.getColor(context, status == PostViewHolder.LikeStatus.LIKED ? R.color.red_d : R.color.blue_d);

        up.setColorFilter(colorChange);
        up.setVisibility(
                status == PostViewHolder.LikeStatus.LIKED ? View.GONE : View.VISIBLE);

        down.setColorFilter(colorChange);
        down.setVisibility(
                status == PostViewHolder.LikeStatus.LIKED ? View.VISIBLE : View.GONE);

        numUp.setText(status == PostViewHolder.LikeStatus.LIKED ? "" + likes : "Up");
        numUp.setTextColor(colorChange);
    }

    public void setPosition(int position) {
        txtPosition.setText("" + position);
    }
}
