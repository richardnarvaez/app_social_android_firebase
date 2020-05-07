package com.richardnarvaez.up.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.richardnarvaez.up.Activity.MainActivity.MainActivity;
import com.richardnarvaez.up.Utility.FirebaseUtil;
import com.richardnarvaez.up.Utility.NeftyUtil;

import com.richardnarvaez.up.R;
import com.richardnarvaez.up.Utility.Utils;
import com.richardnarvaez.up.domain.entity.User;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
        Firebase();
    }



    private void Firebase() {

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (user != null) {
                            FirebaseUtil.getCurrentUserRef().child("author").child("user_name").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull final DataSnapshot snapshot) {

                                    if (!snapshot.exists()) {
                                        NeftyUtil.goUserNameActivity(SplashActivity.this);
                                    } else {
                                        NeftyUtil.goMainActivity(SplashActivity.this);
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                }
                            });
                            //overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        } else {
                            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            overridePendingTransition(R.anim.fadein, R.anim.pop_out);
                        }
                    }
                }, 1000 * 3);

                /*FirebaseUser user = firebaseAuth.getCurrentUser();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                if (user != null) {
                    String uid = user.getUid();
                    mDatabase.child("users").child(uid).child("type")
                            .child("business").addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //User user = dataSnapshot.getValue(User.class);
                                    type = dataSnapshot.getValue(Boolean.class);
                                    Log.e(TAG, "Type: " + type);
                                    if (type) {
                                        startActivity(new Intent(SplashActivity.this, CustomerActivity.class));
                                        finish();
                                    } else {
                                        startActivity(new Intent(SplashActivity.this, CustomerActivity.class));
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                }
                            });

                    Log.e(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                    Log.e(TAG, "onAuthStateChanged:signed_out");
                }*/
            }
        };

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

}

