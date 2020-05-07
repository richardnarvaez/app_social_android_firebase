package com.richardnarvaez.up.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.richardnarvaez.up.Adapter.Firebase.FirebaseRecyclerAdapter;
import com.richardnarvaez.up.Adapter.Firebase.UserViewHolder;
import com.richardnarvaez.up.Model.Author;
import com.richardnarvaez.up.Utility.FirebaseUtil;

import com.richardnarvaez.up.R;

/**
 * Created by richardnarvaez on 7/30/17.
 */

public class FragmentSearch extends Fragment {

    public static String TEXT_SEARCH = "text_send";
    RecyclerView mRecyclerView;
    FirebaseRecyclerAdapter<Author, UserViewHolder> mAdapter;
    private String TAG = "FragmentSearch";

    public void search(String data) {
        Query search =
                FirebaseUtil.getPeopleRef()
                        .orderByChild("author/user_name").startAt(data)
                        .endAt(data + "\uf8ff").limitToFirst(10);

        mAdapter = getFirebaseRecyclerAdapter(search);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        mRecyclerView = rootView.findViewById(R.id.recyclerSearch);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        search("");
        return rootView;
    }

    private FirebaseRecyclerAdapter<Author, UserViewHolder> getFirebaseRecyclerAdapter(Query query) {
        return new FirebaseRecyclerAdapter<Author, UserViewHolder>(
                Author.class,
                R.layout.item_profile_search,
                UserViewHolder.class,
                query) {
            @Override
            public void populateViewHolder(UserViewHolder userViewHolder,
                                           Author user, final int position) {
                setupPost(userViewHolder, user, position, null, getRef(position).getKey());

            }

            @Override
            public void onViewRecycled(@NonNull UserViewHolder holder) {
                super.onViewRecycled(holder);
                //FirebaseUtil.getLikesRef().child(holder.mPostKey).removeEventListener(holder.mLikeListener);
            }
        };
    }

    private void setupPost(final UserViewHolder postViewHolder, Author post, int position, Object o1, final String key) {

        FirebaseUtil.getPeopleRef().child(key + "/author/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Author user = dataSnapshot.getValue(Author.class);
                postViewHolder.setOnClick(getActivity(), user);
                postViewHolder.setImageUser(user.getProfile_picture());
                postViewHolder.setUser(user.getName());
                postViewHolder.setUserName("@" + user.getUser_name());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
