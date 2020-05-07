package com.richardnarvaez.up.Activity.MainActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.transition.Fade;
import android.support.transition.Transition;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.richardnarvaez.up.Activity.AboutActivity;
import com.richardnarvaez.up.Activity.NotificationActivity;
import com.richardnarvaez.up.Activity.ProfileActivity;
import com.richardnarvaez.up.Activity.SearchActivity;
import com.richardnarvaez.up.Activity.SettingsActivity;
import com.richardnarvaez.up.Fragment.FragmentInfluencer;
import com.richardnarvaez.up.Fragment.FragmentSaved;
import com.richardnarvaez.up.Fragment.Home.FragmentChallenges;
import com.richardnarvaez.up.Fragment.Home.FragmentDiscoverPost;
import com.richardnarvaez.up.Interface.GestureEvents;
import com.richardnarvaez.up.Interface.OnFeedPostListener;
import com.richardnarvaez.up.NavigationViewPersonalized.Widget.AnimatedTextView;
import com.richardnarvaez.up.Utility.GlideUtil;
import com.richardnarvaez.up.Utility.NeftyUtil;
import com.richardnarvaez.up.R;
import com.richardnarvaez.up.Utility.Utils;
import com.richardnarvaez.up.View.BottomNavigationViewHelper;
import com.webianks.library.PopupBubble;

import org.jetbrains.annotations.NotNull;

import com.richardnarvaez.up.Activity.PhothoUpload.NewPostActivity;
import com.richardnarvaez.up.Fragment.Home.FragmentHome;
import com.richardnarvaez.up.Fragment.Home.Top.FragmentMainTop;
import com.richardnarvaez.up.Model.Author;
import com.richardnarvaez.up.NavigationViewPersonalized.Widget.AnimatedImageView;
import com.richardnarvaez.up.Utility.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        OnFeedPostListener,
        GoogleApiClient.OnConnectionFailedListener,
        NavigationView.OnNavigationItemSelectedListener, GestureEvents {

    String TAG = "MainActivity";
    BottomNavigationView bottomNavigationView;

    DrawerLayout drawerLayout;
    ImageView home, news, favorite, buy, account;
    Context context = MainActivity.this;
    Toolbar toolbar;

    //FrameLayout rootLayout;
    AnimatedTextView textUp;
    GoogleApiClient mGoogleApiClient;
    FragmentTransaction transactionFragment;
    private AnimatedImageView arcImage;
    public static PopupBubble popup_bubble;
    private AppBarLayout appbarLayout;
    private AppBarLayout.Behavior behavior;
    private RelativeLayout navigationBottom;
    private long MOVE_DEFAULT_TIME = 100;
    private long FADE_DEFAULT_TIME = 200;
    private AnimatedImageView notification;
    ImageView search;

    NavigationView navView;
    CardView mainView;
    private ViewPager viewPager;
    private FloatingActionButton fab;


    private Fragment firstFragment, secondFragment, thirdFragment, fourthFragment;
    private Fragment influencerFragment;
    private int REQUEST_INVITE = 20;
//    ConstraintLayout.LayoutParams paramsGlHorizontal, paramsGlVertical, paramsGlBottom, paramsGlMarginEnd;
//    private FrameLayout frmDetailsContainer, frmVideoContainer;

    @SuppressLint("SetTextI18n")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUtil.getCurrentUserRef().child("author").child("user_name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    NeftyUtil.goUserNameActivity(MainActivity.this);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        updateUI();
    }

    private void initbottomNavigationView() {
        bottomNavigationView = findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.idFeed:
                                textUp.setAnimatedText(getString(R.string.app_name), 0);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                remplaceFragment(firstFragment);
                                break;

                            case R.id.idDiscover:
                                //noSelected();
                                //news.setColorFilter(ContextCompat.getColor(context, R.color.colorSecundary));
                                notification.setVisibility(View.GONE);
                                search.setVisibility(View.VISIBLE);
                                popup_bubble.hide();
                                appbarLayout.setExpanded(true);
                                textUp.setAnimatedText(getString(R.string.title_search), 0);
                                remplaceFragment(secondFragment);
                                break;

                            case R.id.idNew:
                                popup_bubble.hide();
                                appbarLayout.setExpanded(true);
                                //viewPage.setCurrentItem(4);
                                startActivity(new Intent(MainActivity.this, NewPostActivity.class));
                                break;
                            case R.id.idUpvote:
                                popup_bubble.hide();
                                appbarLayout.setExpanded(true);
                                textUp.setAnimatedText(getString(R.string.title_search), 0);
                                remplaceFragment(new FragmentMainTop());
                                break;
                            case R.id.idGift:
                                popup_bubble.hide();
                                appbarLayout.setExpanded(true);
                                textUp.setAnimatedText(getString(R.string.title_search), 0);
                                remplaceFragment(fourthFragment);
                                break;

                        }
                        return true;
                    }
                });
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitleBar(getString(R.string.app_name));
        appbarLayout = findViewById(R.id.appBar);
        ToolbarActions();
    }

    private void drawerLayoutEffect() {
        //        drawerLayout.setDrawerElevation(0f);
//        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
//            @Override
//            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
//                float moveFactor = navView.getWidth() * slideOffset;
//                mainView.setTranslationX(moveFactor);
//                mainView.setScaleX(1 - slideOffset / 4);
//                mainView.setScaleY(1 - slideOffset / 4);
//                mainView.setCardElevation(slideOffset * CommonExKt.toPx(10, MainActivity.this));
//                mainView.setRadius(slideOffset * CommonExKt.toPx(10, MainActivity.this));
//            }
//
//            @Override
//            public void onDrawerOpened(@NonNull View drawerView) {
//                //presenter.handleDrawerOpen()
//            }
//
//            @Override
//            public void onDrawerClosed(@NonNull View drawerView) {
//                //presenter.handleDrawerClose()
//            }
//
//            @Override
//            public void onDrawerStateChanged(int newState) {
//
//            }
//        });
//        drawerLayout.setScrimColor(Color.TRANSPARENT);
    }

    private void initSwipeVideoView() {
        //        frmDetailsContainer = findViewById(R.id.frmDetailsContainer);
//        frmVideoContainer = findViewById(R.id.frmVideoContainer);
//
//        paramsGlHorizontal = (ConstraintLayout.LayoutParams) findViewById(R.id.guidelineHorizontal).getLayoutParams();
//        paramsGlVertical = (ConstraintLayout.LayoutParams) findViewById(R.id.guidelineVertical).getLayoutParams();
//        paramsGlBottom = (ConstraintLayout.LayoutParams) findViewById(R.id.guidelineBottom).getLayoutParams();
//        paramsGlMarginEnd = (ConstraintLayout.LayoutParams) findViewById(R.id.guidelineMarginEnd).getLayoutParams();


//        Fragment fragment = VideoPlayerFragment.newInstance("ymq1WdGUcw8");
//        Fade enterFade = new Fade();
//        enterFade.setStartDelay(MOVE_DEFAULT_TIME + FADE_DEFAULT_TIME);
//        enterFade.setDuration(FADE_DEFAULT_TIME);
//        fragment.setEnterTransition(enterFade);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.frmVideoContainer, fragment)
//                .commit();
//
//        Fragment fragment2 = DetailContainerFragment.newInstance("ymq1WdGUcw8");
//        fragmentManager.beginTransaction().replace(R.id.frmDetailsContainer, fragment2)
//                .commit();

//        loadFragment {
//            replace(R.id.frmDetailsContainer, VideoDetailsFragment.newInstance(it.id), VideoDetailsFragment.TAG)
//        }


//        animationTouchListener = new VideoTouchHandler(MainActivity.this, MainActivity.this);
//        animationTouchListener.show();
//        animationTouchListener.setExpanded(true);

        //hide();
        //frmVideoContainer.setOnTouchListener(animationTouchListener);

    }

    private void getGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .enableAutoManage(this /* FragmentActivity */, MainActivity.this/* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    private void hide() {
//        ConstraintLayout root = findViewById(R.id.rootContainer);
//        cont.clone(root);
//        Guideline var10001 = findViewById(R.id.guidelineHorizontal);
//        cont.setGuidelinePercent(var10001.getId(), 100.0F);
//        var10001 = findViewById(R.id.guidelineVertical);
//        cont.setGuidelinePercent(var10001.getId(), 100.0F);
//        FrameLayout var12 = findViewById(R.id.frmDetailsContainer);
//        cont.setAlpha(var12.getId(), 0.0F);
//
//        TransitionManager.beginDelayedTransition(root, new ChangeBounds().setInterpolator((TimeInterpolator) (new AnticipateOvershootInterpolator(1.0F))).setDuration(250L));
//        cont.applyTo(root);
    }

    private void ToolbarActions() {
        search = findViewById(R.id.searchMenu);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        notification = findViewById(R.id.notification);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
            }
        });
    }

    // Add Fragments to Tabs
    private void setupViewPager() {

        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentHome(), "Today");
        adapter.addFragment(new FragmentDiscoverPost(), "Year");
        adapter.addFragment(new FragmentMainTop(), "Challenge");
        viewPager.setAdapter(adapter);

    }

    @Override
    public void onDismiss(@NotNull View view) {
        dismiss();
    }

    private void dismiss() {
//        ConstraintLayout root = findViewById(R.id.rootContainer);
//        cont.clone(root);
//        Guideline var10001 = (Guideline) findViewById(R.id.guidelineVertical);
//        cont.setGuidelinePercent(var10001.getId(), -0.55F);
//        var10001 = (Guideline) findViewById(R.id.guidelineMarginEnd);
//        cont.setGuidelinePercent(var10001.getId(), 0.0F);
//
//        TransitionManager.beginDelayedTransition(root, new ChangeBounds()
//                .setDuration(250L)
//                .addListener((Transition.TransitionListener) (new DismissListener(this)))
//                .setInterpolator((TimeInterpolator) (new AnticipateOvershootInterpolator(1.0F))));
//        cont.applyTo(root);
    }

    public final class DismissListener implements Transition.TransitionListener {

        Activity activity;

        public DismissListener(MainActivity mainActivity) {
            this.activity = mainActivity;
        }

        @Override
        public void onTransitionStart(@NonNull Transition transition) {

        }

        @Override
        public void onTransitionEnd(@NonNull Transition transition) {

        }

        @Override
        public void onTransitionCancel(@NonNull Transition transition) {

        }

        @Override
        public void onTransitionPause(@NonNull Transition transition) {

        }

        @Override
        public void onTransitionResume(@NonNull Transition transition) {

        }
    }

    @Override
    public void onScale(float percentage) {
        scaleVideo(percentage);
    }

    private void scaleVideo(float percentScrollUp) {
//        float percentVerticalMoved = Math.max(0F, Math.min(VideoTouchHandler.MIN_VERTICAL_LIMIT, percentScrollUp));
//        float movedPercent = percentVerticalMoved / VideoTouchHandler.MIN_VERTICAL_LIMIT;
//        float percentHorizontalMoved = VideoTouchHandler.MIN_HORIZONTAL_LIMIT * movedPercent;
//        float percentBottomMoved = 1F - movedPercent * (1F - VideoTouchHandler.MIN_BOTTOM_LIMIT);
//        float percentMarginMoved = 1F - movedPercent * (1F - VideoTouchHandler.MIN_MARGIN_END_LIMIT);
//
//        paramsGlHorizontal.guidePercent = percentVerticalMoved;
//        paramsGlVertical.guidePercent = percentHorizontalMoved;
//        paramsGlBottom.guidePercent = percentBottomMoved;
//        paramsGlMarginEnd.guidePercent = percentMarginMoved;
//
//        findViewById(R.id.guidelineHorizontal).setLayoutParams(paramsGlHorizontal);
//        findViewById(R.id.guidelineVertical).setLayoutParams(paramsGlVertical);
//        findViewById(R.id.guidelineBottom).setLayoutParams(paramsGlBottom);
//        findViewById(R.id.guidelineMarginEnd).setLayoutParams(paramsGlMarginEnd);
//
//        frmDetailsContainer.setAlpha(1.0F - movedPercent);
    }


    @Override
    public void onSwipe(float percentage) {
        swipeVideo(percentage);
    }

    private void swipeVideo(float percentScrollSwipe) {
//        float percentHorizontalMoved = Math.max(-0.25F, Math.min(VideoTouchHandler.MIN_HORIZONTAL_LIMIT, percentScrollSwipe));
//        float percentMarginMoved = percentHorizontalMoved + (VideoTouchHandler.MIN_MARGIN_END_LIMIT - VideoTouchHandler.MIN_HORIZONTAL_LIMIT);
//
//        paramsGlVertical.guidePercent = percentHorizontalMoved;
//        paramsGlMarginEnd.guidePercent = percentMarginMoved;
//        findViewById(R.id.guidelineVertical).setLayoutParams(paramsGlVertical);
//        findViewById(R.id.guidelineMarginEnd).setLayoutParams(paramsGlMarginEnd);
    }

    @Override
    public void onExpand(boolean isExpanded) {
        setViewExpanded(isExpanded);
    }

    ConstraintSet cont = new ConstraintSet();

    public void setViewExpanded(boolean isExpanded) {
//        ConstraintLayout root = findViewById(R.id.rootContainer);
//
//        cont.clone(root);
//
//        cont.setGuidelinePercent(R.id.guidelineHorizontal, isExpanded ? 0F : VideoTouchHandler.MIN_VERTICAL_LIMIT);
//        cont.setGuidelinePercent(R.id.guidelineVertical, (isExpanded) ? 0F : VideoTouchHandler.MIN_HORIZONTAL_LIMIT);
//        cont.setGuidelinePercent(R.id.guidelineBottom, (isExpanded) ? 1F : VideoTouchHandler.MIN_BOTTOM_LIMIT);
//        cont.setGuidelinePercent(R.id.guidelineMarginEnd, (isExpanded) ? 1F : VideoTouchHandler.MIN_MARGIN_END_LIMIT);
//        cont.setAlpha(R.id.frmDetailsContainer, isExpanded ? 1.0F : 0F);
//
//        TransitionManager.beginDelayedTransition(root, new ChangeBounds().setDuration(250L).setInterpolator(new AnticipateOvershootInterpolator(1.0F)));
//        cont.applyTo(root);

    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void updateUI() {
        setContentView(R.layout.activity_main);

        initToolbar();

        navigationBottom = findViewById(R.id.navigationBottom);
        navigationBottom.setVisibility(View.VISIBLE);
        fragmentManager = getSupportFragmentManager();

        firstFragment = new FragmentHome();
        secondFragment = new FragmentDiscoverPost();
        thirdFragment = new FragmentMainTop();
        fourthFragment = new FragmentChallenges();
        influencerFragment = new FragmentInfluencer();

        remplaceFragment(firstFragment);

        initSwipeVideoView();

        getGoogleApiClient();


        viewPager = findViewById(R.id.viewpager_main);


        setupViewPager();

        mainView = findViewById(R.id.mainView);
        drawerLayout = findViewById(R.id.drawerLayout);

        drawerLayoutEffect();

        navView = findViewById(R.id.navView);
        navView.getMenu().getItem(0).setChecked(true);
        navView.setNavigationItemSelectedListener(this);

        getUser();

        //initbottomNavigationView();

        popup_bubble = findViewById(R.id.popup_bubble);
        popup_bubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_bubble.hide();
                Toast.makeText(MainActivity.this, "Ver nuevas historias", Toast.LENGTH_SHORT).show();
            }
        });


        arcImage = findViewById(R.id.arcImage);
        arcImage.setOnClickListener(this);


        home = findViewById(R.id.nav_home);
        news = findViewById(R.id.nav_news);
        favorite = findViewById(R.id.nav_favorite);
        buy = findViewById(R.id.nav_buy);
        account = findViewById(R.id.nav_account);

        fab = findViewById(R.id.fab_add_post);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NewPostActivity.class));
            }
        });

        buttons();

    }

    private void buttons() {
        home.setOnClickListener(this);
        findViewById(R.id.nav_news).setOnClickListener(this);
        findViewById(R.id.nav_favorite).setOnClickListener(this);
        findViewById(R.id.nav_buy).setOnClickListener(this);
        findViewById(R.id.nav_account).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void noSelected() {
        home.setColorFilter(ContextCompat.getColor(context, R.color.colorNoSelect));
        news.setColorFilter(ContextCompat.getColor(context, R.color.colorNoSelect));
        favorite.setColorFilter(ContextCompat.getColor(context, R.color.colorNoSelect));
        buy.setColorFilter(ContextCompat.getColor(context, R.color.colorNoSelect));
        account.setColorFilter(ContextCompat.getColor(context, R.color.colorNoSelect));
    }

    @Override
    public void onClick(@NonNull View v) {
        int i = v.getId();

        search.setVisibility(View.GONE);
        notification.setVisibility(View.VISIBLE);

        switch (i) {
            case R.id.arcImage:
                drawerLayout.openDrawer(GravityCompat.START);
                break;

            case R.id.nav_home:
                noSelected();
                home.setColorFilter(ContextCompat.getColor(context, R.color.colorSecundary));
                textUp.setAnimatedText(getString(R.string.app_name), 0);
                if (fragment instanceof FragmentHome) {
                    remplaceFragment(new FragmentHome());
                } else {
                    remplaceFragment(firstFragment);
                }
                //viewPage.setCurrentItem(0);
                break;

            case R.id.nav_news:
                noSelected();

                search.setVisibility(View.VISIBLE);
                notification.setVisibility(View.GONE);

                news.setColorFilter(ContextCompat.getColor(context, R.color.colorSecundary));
                popup_bubble.hide();
                appbarLayout.setExpanded(true);
                textUp.setAnimatedText(getString(R.string.title_search), 0);
                remplaceFragment(secondFragment);
                break;

            case R.id.nav_buy:
                noSelected();
                buy.setColorFilter(ContextCompat.getColor(context, R.color.colorSecundary));
                popup_bubble.hide();
                appbarLayout.setExpanded(true);
                textUp.setAnimatedText(getString(R.string.title_news), 0);
                remplaceFragment(thirdFragment);
                //viewPage.setCurrentItem(2);
                break;

            case R.id.nav_favorite:
                noSelected();
                favorite.setColorFilter(ContextCompat.getColor(context, R.color.colorSecundary));
                popup_bubble.hide();
                appbarLayout.setExpanded(true);
                textUp.setAnimatedText(getString(R.string.title_fourth), 0);
                remplaceFragment(fourthFragment);
                //viewPage.setCurrentItem(3);
                break;

            case R.id.nav_account:
                popup_bubble.hide();
                appbarLayout.setExpanded(true);
                //viewPage.setCurrentItem(4);
                startActivity(new Intent(MainActivity.this, NewPostActivity.class));
                break;
        }

    }

    Fragment fragment = null;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransition;

    private void remplaceFragment(Fragment newFragment) {

        if (fragment != null) {
            fragmentTransition = fragmentManager.beginTransaction();
            fragmentTransition.hide(fragment).commit();
        }

        try {
            fragment = newFragment;

            if (fragment instanceof FragmentHome
                    || fragment instanceof FragmentDiscoverPost
                    || fragment instanceof FragmentMainTop
                    || fragment instanceof FragmentChallenges) {
                navigationBottom.setVisibility(View.VISIBLE);
            } else {
                navigationBottom.setVisibility(View.GONE);
            }


            //Effect
            Fade enterFade = new Fade();
            enterFade.setStartDelay(MOVE_DEFAULT_TIME + FADE_DEFAULT_TIME);
            enterFade.setDuration(FADE_DEFAULT_TIME);
            fragment.setEnterTransition(enterFade);

            // Insert the fragment
            fragmentTransition = fragmentManager.beginTransaction();

            if (fragment.isAdded()) {
                fragmentTransition.show(fragment);
            } else {
                fragmentTransition.add(R.id.fragment_container, fragment);
//                if (fragmentManager.getBackStackEntryCount() != 0) {
//                    fragmentTransition.addToBackStack(null);
//                }
            }

            fragmentTransition.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTitleBar(String titleBar) {
        textUp = findViewById(R.id.toolbarTitle);
        textUp.setText(titleBar);
    }

    @Override
    public void onPostComment(String postKey) {
        // NeftyUtil.Alert(this, "Comentarios", "Actualmente estamos trabajando en ello, por el momento no podras realizar comentarios. Gracias por tu comprension.\n\nEquipo Nefty.");
    }

    @Override
    public void onPostLike(final String postKey) {
        final String userKey = FirebaseUtil.getCurrentUserId();
        final DatabaseReference postLikesRef = FirebaseUtil.getLikesRef();
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

    @Override
    public void onPostSave(final String postKey) {
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

    ImageView profile, backProfile;
    TextView userName, userInfo;

    public void getUser() {
        FirebaseUtil.getCurrentUserAuthorRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Author author = dataSnapshot.getValue(Author.class);

                if (author != null) {
                    profile = navView.getHeaderView(0).findViewById(R.id.userAvatar);
                    userName = navView.getHeaderView(0).findViewById(R.id.userName);
                    userInfo = navView.getHeaderView(0).findViewById(R.id.userInfo);
                    backProfile = navView.getHeaderView(0).findViewById(R.id.userBackProfile);

                    /*GET USER*/
                    if (!author.getCover().isEmpty()) {
                        GlideUtil.loadImage(author.getCover(), backProfile);
                    } else {
                        GlideUtil.loadBlurImage(author.getProfile_picture(), backProfile, 25);
                    }

                    GlideUtil.loadProfileIcon(author.getProfile_picture(), profile);

                    userName.setText(author.getName());
                    userInfo.setText("@" + author.getUser_name());
                    View.OnClickListener goProfile = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity((new Intent(MainActivity.this, ProfileActivity.class))
                                    .putExtra(ProfileActivity.ID_USER, author.getUid()));
                        }
                    };
                    profile.setOnClickListener(goProfile);
                    userName.setOnClickListener(goProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            remplaceFragment(firstFragment);
            //bottomNavigationView.setVisibility(View.VISIBLE);
            textUp.setAnimatedText(getString(R.string.app_name), 0);
        } else if (id == R.id.nav_gallery) {
            remplaceFragment(new FragmentSaved());
            //bottomNavigationView.setVisibility(View.GONE);
            textUp.setAnimatedText("SAVED", 0);
        }

        switch (id) {
            case R.id.nav_logout:
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Cerrar sesion");
                alert.setMessage("Estas seguro de que quieres irte?, te extañaremos, vuelve preonto.");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        FirebaseUtil.logOut(MainActivity.this, mGoogleApiClient);
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();


                break;
            case R.id.nav_setting:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.nav_google:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Log.e(TAG, "Name: " + getApplicationContext().getPackageName());
                intent.setData(Uri.parse("market://details?id=com.richardnarvaez.up"));
                startActivity(intent);
                break;
            case R.id.nav_influencer:
                remplaceFragment(influencerFragment);
                Toast.makeText(context, getResources().getString(R.string.comming_soon), Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_info:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case R.id.nav_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Descarga UPSTORIES \uD83D\uDCF1 y comparte las mejores experiencias de Ecuador \uD83D\uDE0D\uD83C\uDDEA\uD83C\uDDE8 y el Mundo \uD83D\uDE0A. Compite con tus amigos por las mejores fotos \uD83E\uDD47 y recomienda lugares donde has estado \uD83D\uDDFD. \n" +
                        "Disponible en Google Play: https://upstories.page.link/download";

                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download App");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
//                Intent ints = new AppInviteInvitation.IntentBuilder("Invitación")
//                        .setMessage("Messenger")
//                        .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
//                        .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
//                        .setCallToActionText(getString(R.string.invitation_cta))
//                        .build();
//                startActivityForResult(ints, REQUEST_INVITE);
                break;

        }

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        navView.setCheckedItem(id);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (fragment != null && fragment instanceof FragmentHome) {
                finish();
            } else {
                navView.getMenu().getItem(0).setChecked(true);
                remplaceFragment(firstFragment);
                noSelected();
                home.setColorFilter(ContextCompat.getColor(context, R.color.colorSecundary));
            }
        }
    }
}
