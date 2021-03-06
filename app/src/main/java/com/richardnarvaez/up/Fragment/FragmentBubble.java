package com.richardnarvaez.up.Fragment;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.richardnarvaez.up.Adapter.Firebase.FirebaseRecyclerAdapter;
import com.vanniktech.emoji.EmojiEditText;

import com.richardnarvaez.up.Model.Comment;
import com.richardnarvaez.up.R;
import com.richardnarvaez.up.ViewHolder.CommentViewHolder;

/**
 * Created by macbookpro on 2/27/18.
 */

public class FragmentBubble extends Fragment {
    public static final String TAG = "CommentsFragment";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String POST_REF_PARAM = "post_ref_param";
    private static final int DEFAULT_MSG_LENGTH_LIMIT = 256;
    private EmojiEditText mEditText;
    private FirebaseRecyclerAdapter<Comment, CommentViewHolder> mAdapter;
    private String mPostRef;
    private LinearLayoutManager linearLayoutManager;

    public FragmentBubble() {
        // Required empty public constructor
    }

    public static FragmentBubble newInstance(String postRef) {
        FragmentBubble fragment = new FragmentBubble();
        Bundle args = new Bundle();
        args.putString(POST_REF_PARAM, postRef);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mPostRef = getArguments().getString(POST_REF_PARAM);
        } else {
            throw new RuntimeException("You must specify a post reference.");
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View rootView = inflater.inflate(R.layout.fragment_bubble, container, false);

        final String[] titles = getResources().getStringArray(R.array.countries);
        final TypedArray colors = getResources().obtainTypedArray(R.array.colors);
        //final TypedArray images = getResources().obtainTypedArray(R.array.images);

        //mEditText = (EmojiEditText) rootView.findViewById(R.id.commenttext);
        //emoji.setVisibility(View.GONE);
        return rootView;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            //mAdapter.stopListening();
        }
    }


}
