package com.richardnarvaez.up.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.richardnarvaez.up.Activity.LoginActivity;

import com.richardnarvaez.up.Activity.ProfileActivity;
import com.richardnarvaez.up.Model.Author;
import com.richardnarvaez.up.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by RICHARD on 07/04/2017.
 */

public class FirebaseUtil {

    private static String postsPath;
    private static String TAG = "FirebaseUtil";

    public static DatabaseReference getBaseRef() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static StorageReference getBaseStorageRef() {
        return FirebaseStorage.getInstance().getReference();
    }

    public static String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }

    public static boolean isLogin() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static Author getAuthorMain() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return null;
        return new Author(user.getDisplayName(), user.getPhotoUrl().toString(), user.getEmail(), user.getUid(), "", "");
    }

    public static DatabaseReference getAuthorAcount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return null;

        return FirebaseUtil.getPeopleRef()
                .child(user.getUid());

    }

    public static DatabaseReference getCurrentUserRef() {
        String uid = getCurrentUserId();
        if (uid != null) {
            return getBaseRef().child("users").child(getCurrentUserId());
        }
        return null;
    }

    public static DatabaseReference getCurrentUserAuthorRef() {
        String uid = getCurrentUserId();
        if (uid != null) {
            return getBaseRef().child("users").child(getCurrentUserId()).child("author");
        }
        return null;
    }

    public static StorageReference getCurrentUserStorageRef() {
        String uid = getCurrentUserId();
        if (uid != null) {
            return getBaseStorageRef().child("users").child(getCurrentUserId());
        }
        return null;
    }

    public static DatabaseReference getOtherPeopleRef(String uid) {
        return getBaseRef().child("users").child(uid);
    }

    public static DatabaseReference getPeopleRef() {
        return getBaseRef().child("users");
    }

    public static DatabaseReference getFeedRef() {
        return getBaseRef().child("feed");
    }

    public static DatabaseReference getPostsRef() {
        return getBaseRef().child("posts");
    }

    public static DatabaseReference getLikesRef() {
        return getBaseRef().child("likes");
    }

    public static DatabaseReference getProductSaleRef() {
        return getBaseRef().child("product_sale");
    }

    public static DatabaseReference getFollowersRef() {
        return getBaseRef().child("followers");
    }

    public static DatabaseReference getCommentsRef() {
        return getBaseRef().child("comments");
    }

    public static String getPeoplePath() {
        return "users/";
    }

    public static String getPostsPath() {
        return "posts/";
    }

    public static void isfollow(final Context context, final String mUserId) {

        final String currentUserId = getCurrentUserId();

        getPeopleRef().child(currentUserId).child("following").child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> updatedUserData = new HashMap<>();

                if (dataSnapshot.exists()) {
                    updatedUserData.put("users/" + currentUserId + "/following/" + mUserId, null);
                    updatedUserData.put("followers/" + mUserId + "/" + currentUserId, null);
                    FirebaseUtil.getFeedRef().child(FirebaseUtil.getCurrentUserId()).setValue(null);
                } else {
                    updatedUserData.put("followers/" + mUserId + "/" + currentUserId, true);
                    updatedUserData.put("users/" + currentUserId + "/following/" + mUserId, true);
                }

                FirebaseUtil.getBaseRef().updateChildren(updatedUserData, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            Toast.makeText(context, R.string.follow_user_error, Toast.LENGTH_LONG).show();
                            Log.d(TAG, context.getString(R.string.follow_user_error) + "\n" +
                                    firebaseError.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

    }

    public static void follow(final Context context, final String mUserId) {

        final String currentUserId = getCurrentUserId();

        if (getCurrentUserId() == null) {
            Toast.makeText(context, "You need to sign in to follow someone.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        getPeopleRef().child(currentUserId).child("following").child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> updatedUserData = new HashMap<>();

                if (dataSnapshot.exists()) {
                    updatedUserData.put("users/" + currentUserId + "/following/" + mUserId, null);
                    updatedUserData.put("followers/" + mUserId + "/" + currentUserId, null);
                    FirebaseUtil.getFeedRef().child(FirebaseUtil.getCurrentUserId()).setValue(null);
                } else {
                    updatedUserData.put("followers/" + mUserId + "/" + currentUserId, true);
                    updatedUserData.put("users/" + currentUserId + "/following/" + mUserId, true);
                }

                FirebaseUtil.getBaseRef().updateChildren(updatedUserData, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            Toast.makeText(context, R.string.follow_user_error, Toast.LENGTH_LONG).show();
                            Log.d(TAG, context.getString(R.string.follow_user_error) + "\n" +
                                    firebaseError.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

    }

    public static void toggleLikes(final String postKey) {
        final String userKey = getCurrentUserId();
        final DatabaseReference postLikesRef = getLikesRef();
        postLikesRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long likes = dataSnapshot.getChildrenCount();
                if (dataSnapshot.child(userKey).exists()) {
                    postLikesRef.child(postKey).child(userKey).removeValue();
                    FirebaseUtil.getPostsRef().child(postKey).child("likes").setValue(likes - 1);
                } else {
                    postLikesRef.child(postKey).child(userKey).setValue(ServerValue.TIMESTAMP);
                    FirebaseUtil.getPostsRef().child(postKey).child("likes").setValue(likes + 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError firebaseError) {

            }
        });
    }

    public static void toggleSave(final String postKey) {
        final DatabaseReference postLikesRef = FirebaseUtil.getCurrentUserRef();
        postLikesRef.child("save").child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    postLikesRef.child("save").child(postKey).removeValue();
                } else {
                    postLikesRef.child("save").child(postKey).setValue(ServerValue.TIMESTAMP);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    public static void logOut(final Activity ctx, GoogleApiClient mGoogleApiClient) {

        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        ctx.finish();
                        ctx.startActivity(new Intent(ctx, LoginActivity.class));
                    }
                });

    }


}
