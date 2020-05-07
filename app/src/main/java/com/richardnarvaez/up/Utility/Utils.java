package com.richardnarvaez.up.Utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.richardnarvaez.up.Effect.BadgeDrawable;
import com.richardnarvaez.up.Model.Author;
import com.richardnarvaez.up.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by RICHARD on 02/04/2017.
 */

public class Utils {

    private static String TAG = "Utils";

    //    public void setCurrentLocation() {
//        if (Utils.isGPSEnabled(getActivity())) {
//            if (Build.VERSION.SDK_INT >= 23 &&
//                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
//                // return;
//            } else {
//                getCurrentAddress();
//            }
//
//        } else {
//            Utils.alertbox("Gps Status", "Your Device's GPS is Disable", getActivity());
//        }
//    }


    //Minimo tiempo para updates en Milisegundos
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minuto
    //Minima distancia para updates en metros.
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1f;

//    public void getCurrentAddress() {
//        // Get the location manager
//        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//
//        if (locationManager != null) {
//
//            try {
//
//                if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//
//                locationManager.requestLocationUpdates(
//                        LocationManager.NETWORK_PROVIDER,
//                        MIN_TIME_BW_UPDATES,
//                        MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
//                            public void onLocationChanged(Location location) {
//                                Log.i(TAG, "Lat " + location.getLatitude() + " Long " + location.getLongitude());
//                                //textViewGPS.setText("Lat " + location.getLatitude() + " Long " + location.getLongitude());
//                            }
//
//                            public void onProviderDisabled(String provider) {
//                                Log.i(TAG, "onProviderDisabled()");
//                            }
//
//                            public void onProviderEnabled(String provider) {
//                                Log.i(TAG, "onProviderEnabled()");
//                            }
//
//                            public void onStatusChanged(String provider, int status, Bundle extras) {
//                                Log.i(TAG, "onStatusChanged()");
//                            }
//
//                        });
//
//            } catch (Exception ex) {
//                Log.i("msg", "fail to request location update, ignore", ex);
//            }
//
//            if (locationManager != null) {
//                location = locationManager
//                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            }
//
//            Geocoder gcd = new Geocoder(getActivity(),
//                    Locale.getDefault());
//            List<Address> addresses;
//            try {
//                addresses = gcd.getFromLocation(location.getLatitude(),
//                        location.getLongitude(), 1);
//                if (addresses.size() > 0) {
//                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                    String locality = addresses.get(0).getLocality();
//                    String subLocality = addresses.get(0).getSubLocality();
//                    String state = addresses.get(0).getAdminArea();
//                    String country = addresses.get(0).getCountryName();
//                    String postalCode = addresses.get(0).getPostalCode();
//                    String knownName = addresses.get(0).getFeatureName();
//                    if (subLocality != null) {
//
//                        currentLocation = locality + "," + subLocality;
//                    } else {
//
//                        currentLocation = locality;
//                    }
//
//                    current_locality = country;
//
//                    Log.e(TAG, "" + addresses.get(0));
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public static void printHashKey(Context pContext) {
        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.e(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, int count) {

        BadgeDrawable badge;

        // Reusar drawable
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    public static void newDialogMainData(final Context context, View mView) {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setView(mView);

        final EditText userName = (EditText) mView.findViewById(R.id.username);
        final EditText Name = (EditText) mView.findViewById(R.id.name);
        final TextInputLayout til = (TextInputLayout) mView.findViewById(R.id.text_input_layout_user_name);
        final TextInputLayout ti_name = mView.findViewById(R.id.ti_name);
        final String[] username = new String[1];


        final String onlyname = FirebaseUtil.getAuthorMain().getName();
        Name.setText(onlyname);

        final RadioButton male = (RadioButton) mView.findViewById(R.id.radioButtonMale);

        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        final AlertDialog dialog = mBuilder.create();
        dialog.setCancelable(false);
        dialog.show();

        final boolean[] flag = {false};
        final boolean[] ok = {false};

        mView.findViewById(R.id.aceptar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flag[0] = false;
                username[0] = (userName.getText().toString()).toLowerCase().trim();

                FirebaseUtil.getPeopleRef().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        for (DataSnapshot IDGenerado : snapshot.getChildren()) {
                            String usernameTaken = IDGenerado.child("author").child("user_name").getValue(String.class);
                            if (username[0].equals(usernameTaken))
                                flag[0] = true;
                        }

                        if (flag[0] || username[0].length() <= 3) {
                            if (username[0].length() <= 3) {
                                til.setError("This name is short");
                            }
//                            if (username[0].length()) {
//                                til.setError("This name is longer");
//                            }

                            if (flag[0]) {
                                til.setError("This name is used");
                            }

                            ok[0] = false;
                        } else {
                            til.setError(null);
                            ok[0] = true;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });

                if (ok[0]) {
                    til.setError(null);
                    guardar(onlyname, username[0], male.isChecked());
                }

            }
        });

    }

    public static void guardar(String onlyname, String userName, boolean checked) {
        Author autor = FirebaseUtil.getAuthorMain();
        DatabaseReference mDatabase = FirebaseUtil.getCurrentUserRef().child("author");

        mDatabase.child("name").setValue(onlyname);
        assert autor != null;
        mDatabase.child("profile_picture").setValue(autor.getProfile_picture());
        mDatabase.child("email").setValue(autor.getEmail());
        mDatabase.child("type").setValue(0);
        mDatabase.child("verified").setValue(false);
        mDatabase.child("user_name").setValue(userName.replace(" ", ""));
        mDatabase.child("sex").setValue(checked);
        mDatabase.child("uid").setValue(autor.getUid());
        mDatabase.child("following").child(autor.getUid()).setValue(System.currentTimeMillis());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(onlyname)
                //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                .build();

        assert user != null;
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            // dialog.hide();
                        }
                    }
                });

    }

    public static void newDialog(Context context, View mView) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setView(mView);
        EditText userName = (EditText) mView.findViewById(R.id.username);
        AlertDialog dialog = mBuilder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public static int pxToDp(Context ctx, int px) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int dpToPx(Context ctx, int dp) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static void setTags(TextView pTextView, String pTagString) {
        SpannableString string = new SpannableString(pTagString);

        int start = -1;
        for (int i = 0; i < pTagString.length(); i++) {
            if (pTagString.charAt(i) == '#') {
                start = i;
            } else if (pTagString.charAt(i) == ' ' || (i == pTagString.length() - 1 && start != -1)) {
                if (start != -1) {
                    if (i == pTagString.length() - 1) {
                        i++; // case for if hash is last word and there is no
                        // space after word
                    }

                    final String tag = pTagString.substring(start, i);
                    string.setSpan(new ClickableSpan() {

                        @Override
                        public void onClick(View widget) {
                            Log.d("Hash", String.format("Clicked %s!", tag));
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            // link color
                            ds.setColor(Color.parseColor("#33b5e5"));
                            ds.setUnderlineText(false);
                        }
                    }, start, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = -1;
                }
            }
        }

        pTextView.setMovementMethod(LinkMovementMethod.getInstance());
        pTextView.setText(string);
    }


    public static int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public static boolean isGPSEnabled(FragmentActivity activity) {
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        assert manager != null;
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    public static void alertbox(String title, String mymessage, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(mymessage)
                .setCancelable(false)
                .setTitle(title)
                .setPositiveButton("Gps On",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
                                Intent myIntent = new Intent(
                                        Settings.ACTION_SECURITY_SETTINGS);
                                context.startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static int getRandomIntInRange(int max, int min) {
        return new Random().nextInt((max - min) + min) + min;
    }

}
