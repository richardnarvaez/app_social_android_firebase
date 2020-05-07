package com.richardnarvaez.up.Utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

/**
 * Created by macbookpro on 4/7/18.
 */

public class UtilsEffects {

    public static void exitCircularReveal(final ViewGroup rootLayout) {
        // get the center for the clipping circle
        int cx = rootLayout.getMeasuredWidth() / 2;
        int cy = rootLayout.getMeasuredHeight();

        // get the initial radius for the clipping circle
        int initialRadius = rootLayout.getHeight();

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, initialRadius, 0);

        anim.setDuration(400);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rootLayout.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }

    public static void exitCircularReveal(final ViewGroup rootLayout, AnimatorListenerAdapter animAdp) {
        // get the center for the clipping circle
        int cx = rootLayout.getMeasuredWidth() / 2;
        int cy = rootLayout.getMeasuredHeight();

        // get the initial radius for the clipping circle
        int initialRadius = rootLayout.getHeight();

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, initialRadius, 0);
        anim.addListener(animAdp);


        anim.setDuration(400);


        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rootLayout.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }


    public static void enterCircularReveal(final ViewGroup rootLayout) {
        int cx = rootLayout.getWidth() / 2;
        int cy = rootLayout.getHeight();

        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
        circularReveal.setDuration(300);

        // make the view visible and start the animation
        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

}
