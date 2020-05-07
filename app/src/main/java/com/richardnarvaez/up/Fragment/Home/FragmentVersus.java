package com.richardnarvaez.up.Fragment.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.richardnarvaez.up.Adapter.SwipeStackAdapter;
import com.richardnarvaez.up.Utility.FirebaseUtil;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.SwipeDirection;

import java.util.ArrayList;
import java.util.List;

import com.richardnarvaez.up.R;

/**
 * Created by RICHARD on 01/04/2017.
 */

public class FragmentVersus extends Fragment implements CardStackView.CardEventListener {//implements SwipeStack.SwipeStackListener {
    private static final String TAG = "FragmentVersus";
    List<String> mData;
    DatabaseReference databaseReference;
    ValueEventListener listener;
    Query search;
    Button versus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = new ArrayList<>();

        databaseReference = FirebaseUtil.getPeopleRef();
        search = databaseReference
                .orderByChild("user").limitToFirst(100);

        adapter = new SwipeStackAdapter(getActivity(), mData);

        callListener();
        assert listener != null;
        search.addListenerForSingleValueEvent(listener);

    }

    private void callListener() {
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mData.size() >= 1) {
                    //Nada
                } else {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.e(TAG, "KEY OF FOLLOWING: " + dataSnapshot.getChildren());
                        if (snapshot.exists() && snapshot.child("products").exists()) {
                            Log.e(TAG, "USER: " + snapshot.child("author").child("name").getValue(String.class));
                            Log.e(TAG, "KEY OF FOLLOWING: " + snapshot.getKey());
                            mData.add(snapshot.getKey());
                        }
                    }
                }

                //Log.e(TAG, "Adaptador listo: " + mData.get(0));
                swipeStackUp.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //En caso de error en la base de datos.
            }
        };
    }

    SwipeStackAdapter adapter;
    CardStackView swipeStackUp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_versus, container, false);
        versus = (Button) rootView.findViewById(R.id.versus);
        //versus.setVisibility(View.GONE);
        swipeStackUp = rootView.findViewById(R.id.swipeStackUp);
        swipeStackUp.setAdapter(adapter);
        swipeStackUp.setCardEventListener(this);
        //swipeStackUp.setListener(this);

        return rootView;
    }

    @Override
    public void onCardDragging(float percentX, float percentY) {

    }

    @Override
    public void onCardSwiped(SwipeDirection direction) {
        switch (direction) {
            case Top:
                swipeStackUp.reverse();
                break;
            default:
                //adapter.getView(swipeStackUp.getTopIndex()).;

                break;
        }

        Log.d("CardStackView", "onCardSwiped: " + direction.toString());
        Log.d("CardStackView", "topIndex: " + swipeStackUp.getTopIndex());
        if (swipeStackUp.getTopIndex() == adapter.getCount() - 5) {
            Log.d("CardStackView", "Paginate: " + swipeStackUp.getTopIndex());
            //paginate();
        }
    }

    @Override
    public void onCardReversed() {

    }

    @Override
    public void onCardMovedToOrigin() {

    }

    @Override
    public void onCardClicked(int index) {

    }

    /*@Override
    public void onViewSwipedToLeft(int position) {
        Toast.makeText(getContext(), "Left", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewSwipedToRight(int position) {
        assert listener != null;
        //search.removeEventListener(listener);
        callListener();
        search.addListenerForSingleValueEvent(listener);
        Toast.makeText(getContext(), "Right", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStackEmpty() {
        versus.animate()
                .scaleX(0.0f)
                .scaleY(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        versus.setVisibility(View.GONE);
                    }
                });
    }*/
}
