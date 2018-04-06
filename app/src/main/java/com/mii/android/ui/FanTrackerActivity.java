package com.mii.android.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.BindView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mii.android.R;
import com.mii.android.ui.base.BaseActivity;
import com.mii.android.ui.draft.DraftFragment;
import com.mii.android.ui.page.PageFragment;
import com.mii.android.ui.util.PlaceholderFragment;
import com.mii.android.util.AdType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FanTrackerActivity extends BaseActivity {

    private static final String TAG = FanTrackerActivity.class.getName();
    private static final int TABS_COUNT = 2;
    public static final int PAGE_TAB_POSITION = 0;
    private static final int POST_TAB_POSITION = 1;

    private static final int ACCESS_INTERNET = 3;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fan_tracker_viewpager)
    ViewPager viewPager;

    @BindView(R.id.fan_tracker_tabs)
    TabLayout tabLayout;

    private FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseAuth mAuth;

    ArrayList<Integer> mAdTypes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupViews();
        mAuth = FirebaseAuth.getInstance();
        // active listen to user logged in or not.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList("AdTypes", mAdTypes);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mAdTypes = savedInstanceState.getIntegerArrayList("AdTypes");
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_fan_tracker;
    }

    @SuppressWarnings("ConstantConditions")
    private void setupViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter tabsSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mAdTypes = bundle.getIntegerArrayList("AdTypes");
            tabsSectionsPagerAdapter.setAdTypes(mAdTypes);
        }

        viewPager = findViewById(R.id.fan_tracker_viewpager);
        viewPager.setAdapter(tabsSectionsPagerAdapter);

        tabLayout = findViewById(R.id.fan_tracker_tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(POST_TAB_POSITION).setIcon(R.drawable.ic_edit);
        tabLayout.getTabAt(PAGE_TAB_POSITION).setIcon(R.drawable.ic_home);
    }

    /**
     * Return the title of the currently displayed section.
     *
     * @return title of the section
     */
    private String getCurrentTitle() {
        int position = viewPager.getCurrentItem();
        switch (position) {
            case 0:
                return "Page";
            case 1:
                return "Post";
            default:
                return "Undefined";
        }
    }

    public void selectPage(int page) {
        viewPager.setCurrentItem(page);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE}, ACCESS_INTERNET);
                } else {
                    signInAnonymously();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ACCESS_INTERNET: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    signInAnonymously();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void signInAnonymously() {
        Task<AuthResult> task = FirebaseAuth.getInstance().signInAnonymously();
        task.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // Task completed successfully
                // ...
                Log.d(TAG, "signInAnonymously:onSuccess");
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Task failed with an exception
                // ...
                Log.d(TAG, "signInAnonymously:onFailure", task.getException());
            }
        });
        // Create a new ThreadPoolExecutor with 2 threads for each processor on the
        // device and a 60 second keep-alive time.
        int numCores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(numCores * 2, numCores *2,
                60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

        task.addOnCompleteListener(executor, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Task completed successfully
                    AuthResult result = task.getResult();
                    Log.d(TAG, "signInAnonymously:success");
                } else {
                    // Task failed with an exception
                    Exception exception = task.getException();
                    Log.w(TAG, "signInAnonymously:failure", task.getException());
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fan_tracker, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, FanTrackerActivity.class);
        Bundle bundle = new Bundle();
        ArrayList<Integer> types = new ArrayList<>();
        types.add(AdType.NATIVE);

        switch (item.getItemId()) {
            case R.id.about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
            case R.id.banner:
                types.add(AdType.BANNER);
                bundle.putIntegerArrayList("AdTypes", types);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            case R.id.interstitial:
                types.add(AdType.INTERSTITIAL);
                bundle.putIntegerArrayList("AdTypes", types);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            case R.id.instream:
                types.add(AdType.INTREAM);
                bundle.putIntegerArrayList("AdTypes", types);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            case R.id.rewarded_video:
                types.add(AdType.REWARD_VIDEO);
                bundle.putIntegerArrayList("AdTypes", types);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static class SectionsPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Integer> mAdTypes;
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            mAdTypes = new ArrayList<>();
            mAdTypes.add(AdType.NATIVE);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == PAGE_TAB_POSITION) {
                return PageFragment.newInstance(mAdTypes);
            } else if (position == POST_TAB_POSITION) {
                return DraftFragment.newInstance();
            }

            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return TABS_COUNT;
        }

        public void setAdTypes(ArrayList<Integer> adTypes) {
            this.mAdTypes = adTypes;
        }


    }
}
