package com.richardnarvaez.up.Adapter.Firebase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by macbookpro on 4/30/18.
 */

public class InfiniteFireSnapshot<T> {
    private String key;
    private T value;

    public InfiniteFireSnapshot(@NonNull String key, T value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    @Nullable
    public T getValue() {
        return value;
    }
}
