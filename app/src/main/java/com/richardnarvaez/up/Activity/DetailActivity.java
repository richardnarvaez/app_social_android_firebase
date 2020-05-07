package com.richardnarvaez.up.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.richardnarvaez.up.Fragment.DetailFragment;
import com.richardnarvaez.up.Fragment.FragmentComments;
import com.richardnarvaez.up.Fragment.FragmentTopPostDaly;
import com.richardnarvaez.up.R;
import com.richardnarvaez.up.Utility.UtilsEffects;
import com.richardnarvaez.up.View.ElasticDragDismissFrameLayout;

import java.util.ArrayList;
import java.util.List;

import xyz.klinker.android.drag_dismiss.activity.DragDismissActivity;

public class DetailActivity extends AppCompatActivity {

    public static String POST_KEY = "_post_key";
    public static String ID_USER = "_id_user";
    public static String URL_THUM = "url_thum";
    public static String URL_FULL = "url_full";
    public static String TITLE = "_title";
    public static String CATEGORY = "_category";
    private String url_thum, url_full, id_user;
    private boolean animFinish = true;
    CoordinatorLayout frameLayout;
    String postKey;
    public static String TYPE = "_type";

    private ElasticDragDismissFrameLayout draggableFrame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        draggableFrame = findViewById(R.id.draggableFrame);
        draggableFrame.addListener(
                new ElasticDragDismissFrameLayout.SystemChromeFader(this) {
                    @Override
                    public void onDragDismissed() {
                        // if we drag dismiss downward then the default reversal of the enter
                        // transition would slide content upward which looks weird. So reverse it.
                        if (draggableFrame.getTranslationY() > 0) {
                            getWindow().setReturnTransition(
                                    TransitionInflater.from(DetailActivity.this)
                                            .inflateTransition(android.R.transition.slide_bottom));//about_return_downward
                        }
                        finishAfterTransition();
                    }
                });

        Intent i = getIntent();
        if (i != null) {

            postKey = getIntent().getStringExtra(POST_KEY);

            if (postKey == null) {
                onBackPressed();
            }

            frameLayout = findViewById(R.id.root_layout);

//            frameLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                @Override
//                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                    v.removeOnLayoutChangeListener(this);
//                    UtilsEffects.enterCircularReveal(frameLayout);
//                }
//            });

            url_thum = i.getStringExtra(URL_THUM);
            url_full = i.getStringExtra(URL_FULL);
            id_user = i.getStringExtra(ID_USER);
            ViewPager viewPager = findViewById(R.id.viewpager);

            Adapter adapter = new Adapter(getSupportFragmentManager());
            //adapter.addFragment(new DetailFragment(), null);
            adapter.addFragment(new FragmentComments().newInstance(postKey, url_thum, url_full, id_user), null);
            viewPager.setAdapter(adapter);

            if (i.getStringExtra(TYPE).equals("comment")) {
                Log.d("TAG", "Comment");
                viewPager.setCurrentItem(1);
            } else {
                viewPager.setCurrentItem(0);
            }


//            FragmentManager fm = getSupportFragmentManager();
//            fm.beginTransaction().add(R.id.comments_fragment, FragmentComments.newInstance(postKey, url_thum, url_full, id_user))
//                    .commit();
        } else {
            onBackPressed();
        }
    }

//    @Override
//    public View onCreateContent(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
//        final View v = inflater.inflate(R.layout.activity_detail, parent, false);
//        return v;
//    }


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

    @Override
    public void onBackPressed() {

        if (animFinish) {
            super.onBackPressed();
        } else {
            UtilsEffects.exitCircularReveal(frameLayout, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animFinish = true;
                    onBackPressed();
                }
            });
        }

    }
}
