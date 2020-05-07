package com.richardnarvaez.up.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.richardnarvaez.up.R;

/**
 * Created by macbookpro on 2/27/18.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {
    public final ImageView commentPhoto;
    public final TextView commentText;
    public final TextView commentAuthor;
    public final TextView commentTime;
    public String authorRef;
    public View view;

    public CommentViewHolder(View itemView) {
        super(itemView);
        this.view = itemView;
        commentPhoto = (ImageView) itemView.findViewById(R.id.comment_author_icon);
        commentText = (TextView) itemView.findViewById(R.id.comment_text);
        commentAuthor = (TextView) itemView.findViewById(R.id.comment_name);
        commentTime = (TextView) itemView.findViewById(R.id.comment_time);

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(v.getContext(), "Long click -> Coming soon", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authorRef != null) {
                    //Toast.makeText(v.getContext(), "Usuario real", Toast.LENGTH_SHORT).show();
                    /*Context context = v.getContext();
                    Intent userDetailIntent = new Intent(context, UserDetailActivity.class);
                    userDetailIntent.putExtra(UserDetailActivity.USER_ID_EXTRA_NAME,
                            authorRef);
                    context.startActivity(userDetailIntent);*/
                }
            }
        });
    }
}
