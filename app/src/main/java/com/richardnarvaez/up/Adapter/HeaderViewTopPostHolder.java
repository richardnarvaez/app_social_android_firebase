package com.richardnarvaez.up.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.richardnarvaez.up.R;

/**
 * Created by macbookpro on 3/28/18.
 */

public class HeaderViewTopPostHolder extends RecyclerView.ViewHolder {
    public View view;
    public RecyclerView recyclerView;
    public HeaderViewTopPostHolder(View v) {
        super(v);
        view = v;
        recyclerView = v.findViewById(R.id.rv_header_discover);
    }
}
