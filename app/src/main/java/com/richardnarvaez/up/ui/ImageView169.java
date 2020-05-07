package com.richardnarvaez.up.ui;

import android.content.Context;

public class ImageView169 extends android.support.v7.widget.AppCompatImageView {

    public ImageView169(Context context) {
        super(context);
    }

    // some other necessary things

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        //force a 16:9 aspect ratio
        int height = Math.round(width * .5625f);
        setMeasuredDimension(width, height);
    }

}
