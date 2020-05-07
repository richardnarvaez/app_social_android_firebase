package com.richardnarvaez.up.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.richardnarvaez.up.Model.ItemCesta;
import com.richardnarvaez.up.R;
import com.richardnarvaez.up.Utility.NetworkConf;

/**
 * Created by RICHARD on 24/04/2017.
 */

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ViewHolder> {
    private ArrayList<ItemCesta> items;
    private int itemLayout;
    private Context context;
    private NetworkConf networkConf;
    //private ItemEventsListener itemEventsListener;

    public ProductosAdapter(Activity context, ArrayList<ItemCesta> items, int itemLayout) {
        this.context = context;
        this.items = items;
        this.itemLayout = itemLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ItemCesta item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.price.setText("$ " + item.getPrice());
        networkConf = new NetworkConf((Activity) context);

        Glide.with(holder.image.getContext()).load(item.getThumbnailURL()).into(holder.image);
        holder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /*public void setOnItemEventsListener(ItemEventsListener listener) {
        itemEventsListener = listener;
    }*/

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public TextView description;
        public TextView price;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image_product_pay);
            /*title = (TextView) itemView.findViewById(R.id.text_title);
            price = (TextView) itemView.findViewById(R.id.text_price);
            description = (TextView) itemView.findViewById(R.id.mi_description);*/
        }
    }
}
