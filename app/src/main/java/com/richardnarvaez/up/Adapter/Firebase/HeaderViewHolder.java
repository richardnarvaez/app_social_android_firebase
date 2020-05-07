package com.richardnarvaez.up.Adapter.Firebase;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.richardnarvaez.up.R;
import com.richardnarvaez.up.Utility.Utils;
import com.richardnarvaez.up.View.EqualSpacingItemDecoration;

/**
 * Created by macbookpro on 2/24/18.
 */

public class HeaderViewHolder extends RecyclerView.ViewHolder {

    public ImageView switchView;
    public ImageView bubbleView;
    public RecyclerView recyclerView;
    public View v;
    public RelativeLayout initView;
    public LinearLayout finishView;

    public HeaderViewHolder(View view) {
        super(view);
        v = view;
        recyclerView = v.findViewById(R.id.rv_top_friends);

        switchView = v.findViewById(R.id.switchView);
        bubbleView = v.findViewById(R.id.bubbleView);
        initView = v.findViewById(R.id.init);
        finishView = v.findViewById(R.id.finish);
    }

    public void setImage(boolean isSwitchView) {
        if (isSwitchView) {
            switchView.setImageResource(R.drawable.vector_grid);
        } else {
            switchView.setImageResource(R.drawable.vector_list);
        }
    }

}
