package com.richardnarvaez.up.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import com.richardnarvaez.up.R;
import com.richardnarvaez.up.Utility.FirebaseUtil;
import com.richardnarvaez.up.Utility.GlideUtil;

import static android.content.ContentValues.TAG;

/**
 * Created by RICHARD on 28/04/2017.
 */

public class SwipeStackAdapter extends BaseAdapter {

    private List<String> mData;
    private Activity context;

    public SwipeStackAdapter(Activity context, List<String> data) {
        this.mData = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView image;
        TextView text;

        ViewHolder(View v) {
            image = v.findViewById(R.id.image_swap_product);
            //text = v.findViewById(R.id.text);
        }
    }

    private ViewHolder viewHolder;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DatabaseReference databaseReference = FirebaseUtil.getPeopleRef().child(mData.get(position));
        Query search = databaseReference.child("products");
        search.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "PRODUCTO: " + dataSnapshot.getChildren().iterator().next().getKey());
                String uidp = dataSnapshot.getChildren().iterator().next().getKey();

                FirebaseUtil.getPostsRef().child(uidp).child("thumbnail").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "URL: " + dataSnapshot.getValue(String.class));
                        final String url = dataSnapshot.getValue(String.class);
                        GlideUtil.loadImage(url, viewHolder.image);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return convertView;
    }

}