package com.richardnarvaez.up.Fragment.Home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.richardnarvaez.up.R;
import com.richardnarvaez.up.Utility.FirebaseUtil;
import com.richardnarvaez.up.Utility.GlideUtil;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by richardnarvaez on 6/30/17.
 */

public class FragmentMapGift extends Fragment implements RewardedVideoAdListener, OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

    private static final String AD_UNIT_ID = "ca-app-pub-9401504218472902/2194598878";
    private static final String APP_ID = "ca-app-pub-9401504218472902~9717865674";
    private static final long COUNTER_TIME = 10;
    private static final int GAME_OVER_REWARD = 1;
    private static final String TAG = "FragmentMapGift";

    private int mCoinCount;
    private TextView mCoinCountText;
    private CountDownTimer mCountDownTimer;
    private boolean mGameOver;
    private boolean mGamePaused;
    private RewardedVideoAd mRewardedVideoAd;
    private Button mRetryButton;
    private Button mShowVideoButton;
    private long mTimeRemaining;

    private Context context;
    private TextView textView;

    private RewardedVideoAd rewardedVideoAd;
    private String YOUR_PLACEMENT_ID;

    MapView mapView;
    GoogleMap googleMap;
    private int RC_LOCATION = 101;
    private Task task;
    private LocationListener gpsListener;

    public FragmentMapGift() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    ImageView imageView;
    //TextView type;
    //TextView participation;
    //ProgressBar progress;

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gift, container, false);

        context = getActivity();

        gpsListener = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String msg = "New Latitude: " + latitude + ", New Longitude: " + longitude;
                Log.e(TAG, msg);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        mapView = view.findViewById(R.id.mapView);
        //mapView.onCreate(savedInstanceState);

        MobileAds.initialize(context, APP_ID);
        //googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            //mapView.onResume();
            MapsInitializer.initialize(getActivity());
            mapView.getMapAsync(this);
        } else {
            EasyPermissions.requestPermissions(this, "Geolocalizacion",
                    RC_LOCATION, perms);
        }


        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls


        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getContext());
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        textView = ((TextView) view.findViewById(R.id.timer));
        //type = (TextView) view.findViewById(R.id.textType);
        //participation = (TextView) view.findViewById(R.id.textParticipation);
        //progress = (ProgressBar) view.findViewById(R.id.progressBar2);
        //progress.setIndeterminate(true);


        // Create the "retry" button, which tries to show an interstitial between game plays.
        mRetryButton = ((Button) view.findViewById(R.id.retry_button));
        mRetryButton.setVisibility(View.VISIBLE);
        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });

        // Create the "show" button, which shows a rewarded video if one is loaded.
        mShowVideoButton = ((Button) view.findViewById(R.id.watch_video));
        mShowVideoButton.setVisibility(View.GONE);
        mShowVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRewardedVideo();
            }
        });

        imageView = (ImageView) view.findViewById(R.id.image_promo);

        // Display current coin count to user.
        mCoinCountText = ((TextView) view.findViewById(R.id.coin_count_text));
        getInfo();

        return view;
    }

    private Boolean permissionsGranted() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void getInfo() {
        FirebaseUtil.getCurrentUserRef().child("author")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("candies").exists()) {
                            mCoinCount = dataSnapshot.child("candies").getValue(Integer.class);
                            mCoinCountText.setText("Candies: " + mCoinCount);
                        } else {
                            FirebaseUtil.getCurrentUserRef().child("author").child("candies").setValue(0);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                });

        FirebaseUtil.getBaseRef().child("awards")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("key1").exists()) {

                            //Problemas al actualizar porcentaje.
                            if (imageView.getDrawable() == null) {
                                String thumb = dataSnapshot.child("key1").child("thumbnail").getValue(String.class);
                                String name = dataSnapshot.child("key1").child("name").getValue(String.class);
                                //type.setText(name);
                                GlideUtil.loadImage(thumb, imageView);
                            }

                            int porcentaje = dataSnapshot.child("key1").child("port").getValue(Integer.class);
                            //participation.setText(porcentaje + "%" + " - " + "150" + " participantes");
                            //progress.setIndeterminate(false);
                            //progress.setProgress(porcentaje);


                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                });
    }

    public void setCandies(int candies) {
        mCoinCount = mCoinCount + candies;
        FirebaseUtil.getCurrentUserRef().child("author").child("candies").setValue(mCoinCount);
        mCoinCountText.setText("Candies: " + mCoinCount);

    }


    private void pauseGame() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mGamePaused = true;
        }
    }

    private void resumeGame() {
        createTimer(mTimeRemaining);
        mGamePaused = false;
    }

    private void loadRewardedVideoAd() {
        if (!mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.loadAd(AD_UNIT_ID, new AdRequest.Builder().build());
        }
    }

    private void startGame() {
        // Hide the retry button, load the ad, and start the timer.
        mRetryButton.setVisibility(View.INVISIBLE);
        mShowVideoButton.setVisibility(View.INVISIBLE);
        loadRewardedVideoAd();
        createTimer(COUNTER_TIME);
        mGamePaused = false;
        mGameOver = false;
    }

    // Create the game timer, which counts down to the end of the level
    // and shows the "retry" button.
    private void createTimer(long time) {

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        mCountDownTimer = new CountDownTimer(time * 1000, 50) {
            @Override
            public void onTick(long millisUnitFinished) {
                mTimeRemaining = ((millisUnitFinished / 1000) + 1);
                textView.setText("Estamos buscando un anuncio: " + mTimeRemaining);
                mRetryButton.setVisibility(View.GONE);
                mShowVideoButton.setVisibility(View.GONE);
            }

            @Override
            public void onFinish() {
                if (mRewardedVideoAd.isLoaded()) {
                    mShowVideoButton.setVisibility(View.VISIBLE);
                    mRetryButton.setVisibility(View.GONE);
                    textView.setText("Woow more candies!");
                } else {
                    mShowVideoButton.setVisibility(View.GONE);
                    textView.setText("No encontramos nada, intentalo otra vez.");
                    //addCoins(GAME_OVER_REWARD);
                    mRetryButton.setVisibility(View.VISIBLE);
                    mGameOver = true;
                }

            }
        };
        mCountDownTimer.start();
    }

    private void showRewardedVideo() {
        mShowVideoButton.setVisibility(View.INVISIBLE);
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.e(TAG, "onRewardedVideoAdLeftApplication");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Log.e(TAG, "onRewardedVideoAdClosed");
        // Preload the next video ad.
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Log.e(TAG, "onRewardedVideoAdFailedToLoad");
    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Log.e(TAG, "onRewardedVideoAdLoaded");
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.e(TAG, "onRewardedVideoAdOpened");
    }

    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(context,
                String.format("Felicidades! Ganaste %d Candies",
                        reward.getAmount()),
                Toast.LENGTH_SHORT).show();
        textView.setText("Encuentra mas Candies");
        setCandies(reward.getAmount());
    }

    @Override
    public void onRewardedVideoStarted() {
        Log.e(TAG, "onRewardedVideoStarted");
    }

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    LocationManager lm;

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap gm) {

//        LatLng sydney = new LatLng(-1.65, -78.67);
//        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));
//
//        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(16).build();
//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        googleMap = gm;
        googleMap.setMyLocationEnabled(true);

        LatLng loc;
        Location location = getLocation();


        if (location != null) {

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

            try {

                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses.size() > 0) {
                    Address fetchedAddress = addresses.get(0);

                    Log.e(TAG, "N: " + fetchedAddress.getMaxAddressLineIndex());

                    for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {

                        Log.e(TAG, "ADRESS: " + fetchedAddress.getAddressLine(i));
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }


//                String address = addresses.get(0).getSubLocality();
//                String cityName = addresses.get(0).getLocality();
//                String stateName = addresses.get(0).getAdminArea();

//                Log.e(TAG, "Adress: " + address);
//                Log.e(TAG, "City: " + cityName);
//                Log.e(TAG, "State: " + stateName);
//                Log.e(TAG, "State: " + addresses.get(0).getCountryName());
//                Log.e(TAG, "State: " + addresses.get(0).getCountryCode());
//                Log.e(TAG, "State: " + addresses.get(0).getPostalCode());
//                Log.e(TAG, "State: " + addresses.get(0).getThoroughfare());


            loc = new LatLng(latitude, longitude);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(16).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } else {
            Toast.makeText(context, "Debes activar tener conexion a internet o activar la Geolocalizacion", Toast.LENGTH_SHORT).show();
//            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
//            alertDialog.setTitle("Confirm Location");
//            alertDialog.setMessage("Your Location is enabled, please enjoy");
//            alertDialog.setPositiveButton("Acepetar", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
//                }
//            });
//            alertDialog.setNegativeButton("Back to interface", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//
//            AlertDialog alert = alertDialog.create();
//            alert.show();

        }


    }

    @SuppressLint("MissingPermission")
    private Location getLocation() {

        Location gpslocation = null;
        Location networkLocation = null;


        if (lm == null)
            lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        assert lm != null;
        try {
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsListener);
                gpslocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            }

            if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsListener);
                networkLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        } catch (IllegalArgumentException e) {
            Log.e("error", e.toString());
        }

        if (gpslocation == null && networkLocation == null)
            return null;

        if (gpslocation != null && networkLocation != null) {
            if (gpslocation.getTime() < networkLocation.getTime()) {
                gpslocation = null;
                return networkLocation;
            } else {
                networkLocation = null;
                return gpslocation;
            }
        }

        if (gpslocation == null) {
            return networkLocation;
        }

        if (networkLocation == null) {
            return gpslocation;
        }

        return null;
    }


    @Override
    public void onResume() {
        super.onResume();
        //mapView.onResume();

        if (!mGameOver && mGamePaused) {
            resumeGame();
        }
        mRewardedVideoAd.resume(context);
    }

    @Override
    public void onPause() {
        super.onPause();
        //mapView.onPause();
        pauseGame();
        mRewardedVideoAd.pause(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }


}
