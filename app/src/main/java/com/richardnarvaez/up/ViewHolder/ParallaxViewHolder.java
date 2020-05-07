package com.richardnarvaez.up.ViewHolder;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;

import com.bumptech.glide.Glide;

import com.richardnarvaez.up.Activity.DetailMinActivity;

import com.richardnarvaez.up.R;

import com.richardnarvaez.up.View.ParallaxImageView;

/**
 * Created by richardnarvaez on 7/4/17.
 */

public class ParallaxViewHolder extends RecyclerView.ViewHolder implements ParallaxImageView.ParallaxImageListener {

    private ParallaxImageView backgroundImage;
    private CardView cardImage;


    public ParallaxViewHolder(View itemView) {
        super(itemView);
        backgroundImage = (ParallaxImageView) itemView.findViewById(R.id.post_photo);
        cardImage = (CardView) itemView.findViewById(R.id.cardProduct);
        backgroundImage.setListener(this);
    }


    public void setImagePost(Context context, String url) {
        Glide.with(context).load(url).into(backgroundImage);
    }

    public void onClick(final Activity context, final String urlPhoto, final String id) {

        backgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                MainActivity.blurContent.setVisibility(View.VISIBLE);

                Intent i = new Intent(context, DetailMinActivity.class);
                i.putExtra(DetailMinActivity.URL_PHOTO, urlPhoto);
                i.putExtra(DetailMinActivity.ID_USER, id);
                //i.putExtra(DetailMinActivity.URL_TITLE, mPostTextView.getText());
                //i.putExtra(DetailMinActivity.URL_PRICE, mPriceTextView.getText());

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                        context,
                        Pair.create((View) backgroundImage, "img_product"),
                        Pair.create((View) cardImage, "card_product")
                        //Pair.create((View) mPostTextView, "txt_title"),
                        //Pair.create((View) mPriceTextView, "txt_price")
                );
                context.startActivity(i, options.toBundle());
            }
        });

    }

    @Override
    public int[] requireValuesForTranslate() {
        if (itemView.getParent() == null) {
            // Not added to parent yet!
            return null;
        } else {
            int[] itemPosition = new int[2];
            itemView.getLocationOnScreen(itemPosition);

            int[] recyclerPosition = new int[2];
            ((RecyclerView) itemView.getParent()).getLocationOnScreen(recyclerPosition);

            return new int[]{itemPosition[1], ((RecyclerView) itemView.getParent()).getMeasuredHeight(), recyclerPosition[1]};
        }
    }

    public void animateImage() {
        getBackgroundImage().doTranslate();
    }

    public ParallaxImageView getBackgroundImage() {
        return backgroundImage;
    }
}