package com.richardnarvaez.up.Adapter.Firebase;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.richardnarvaez.up.Activity.ProfileActivity;
import com.richardnarvaez.up.Model.Author;
import com.richardnarvaez.up.R;
import com.richardnarvaez.up.Utility.GlideUtil;
import com.richardnarvaez.up.ViewHolder.PostViewHolder;

/**
 * Created by macbookpro on 2/26/18.
 */

public class UserViewHolder extends PostViewHolder {

    public ImageView backImage;
    private View view;
    private TextView tvName, tvUserName, tvPosition;
    private ImageView imUser;
    RelativeLayout userContent;

    public UserViewHolder(View v) {
        super(v);
        this.view = v;
        this.userContent = v.findViewById(R.id.userContent);
        this.tvName = v.findViewById(R.id.tvName);
        this.imUser = v.findViewById(R.id.thumbUser);
        this.tvUserName = v.findViewById(R.id.tvUserName);
        this.backImage = v.findViewById(R.id.backImage);
        this.tvPosition = v.findViewById(R.id.txtPosition);
        //this.btFollow = v.findViewById(R.id.btFollow);
    }

    public void setOnClick(final Activity context, final Author user) {
        userContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra(ProfileActivity.ID_USER, user.getUid());
                context.startActivity(i);
                //context.finish();
            }
        });
    }

    public void setUser(String name) {
        tvName.setText(name);
    }

    public void setUserName(String username) {
        tvUserName.setText(username);
    }

    public void setImageUser(String thumb) {
        GlideUtil.loadProfileIcon(thumb, imUser);
    }

    public void setPosition(String position) {
        tvPosition.setText(position);
    }

}
