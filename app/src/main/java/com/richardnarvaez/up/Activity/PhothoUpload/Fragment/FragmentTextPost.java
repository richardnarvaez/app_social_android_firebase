package com.richardnarvaez.up.Activity.PhothoUpload.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.richardnarvaez.up.R;

/**
 * Created by macbookpro on 3/30/18.
 */

@SuppressLint("ValidFragment")
public class FragmentTextPost extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_text_post, container, false);
        return v;
    }
}
