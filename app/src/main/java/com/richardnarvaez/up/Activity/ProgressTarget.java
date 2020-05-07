package com.richardnarvaez.up.Activity;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

/**
 * Created by Richard Narvaez on 28/12/2017.
 */

public abstract class ProgressTarget<T, Z> extends WrappingTarget<Z> implements OkHttpProgressGlideModule.UIProgressListener {
    private T model;
    private boolean ignoreProgress = true;

    public ProgressTarget(Target<Z> target) {
        this(null, target);
    }

    public ProgressTarget(T model, Target<Z> target) {
        super(target);
        this.model = model;
    }

    public final T getModel() {
        return model;
    }

    public final void setModel(T model) {
        //Glide.clear(this); // indirectly calls cleanup
        this.model = model;
    }

    protected String toUrlString(T model) {
        return String.valueOf(model);
    }

    @Override
    public float getGranualityPercentage() {
        return 1.0f;
    }

    @Override
    public void onProgress(long bytesRead, long expectedLength) {
        if (ignoreProgress) {
            return;
        }
        if (expectedLength == Long.MAX_VALUE) {
            onConnecting();
        } else if (bytesRead == expectedLength) {
            onDownloaded();
        } else {
            onDownloading(bytesRead, expectedLength);
        }
    }

    protected abstract void onConnecting();

    protected abstract void onDownloading(long bytesRead, long expectedLength);

    protected abstract void onDownloaded();

    protected abstract void onDelivered();

    private void start() {
        OkHttpProgressGlideModule.expect(toUrlString(model), this);
        ignoreProgress = false;
        onProgress(0, Long.MAX_VALUE);
    }

    private void cleanup() {
        ignoreProgress = true;
        T model = this.model; // save in case it gets modified
        onDelivered();
        OkHttpProgressGlideModule.forget(toUrlString(model));
        this.model = null;
    }

    @Override
    public void onLoadStarted(Drawable placeholder) {
        super.onLoadStarted(placeholder);
        start();
    }

    @Override
    public void onResourceReady(@NonNull Z resource, Transition<? super Z> animation) {
        cleanup();
        super.onResourceReady(resource, animation);
    }

    @Override
    public void onLoadFailed(Drawable errorDrawable) {
        cleanup();
        super.onLoadFailed(errorDrawable);
    }

    @Override
    public void onLoadCleared(Drawable placeholder) {
        cleanup();
        super.onLoadCleared(placeholder);
    }
}