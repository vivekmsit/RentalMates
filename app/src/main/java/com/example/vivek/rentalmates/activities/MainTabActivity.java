package com.example.vivek.rentalmates.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.fragments.ExpenseDataListFragment;
import com.example.vivek.rentalmates.fragments.NavigationDrawerFragment;
import com.example.vivek.rentalmates.fragments.NewsFeedFragment;
import com.example.vivek.rentalmates.fragments.SearchFlatFragment;
import com.example.vivek.rentalmates.fragments.SearchRoomMateFragment;
import com.example.vivek.rentalmates.data.AppConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

public class MainTabActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainTabActivity_Debug";

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences prefs;
    private NavigationDrawerFragment drawerFragment;
    private FragmentManager fragmentManager;
    private MyAdapter pageAdapter;
    private FloatingActionButton fab;
    private int currentPosition;
    private int backStackCount;
    private boolean newExpenseAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        currentPosition = 0;
        newExpenseAvailable = false;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolBar.setTitleTextColor(getResources().getColor(R.color.white));

        //Initialize FragmentManager
        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                backStackCount = fragmentManager.getBackStackEntryCount();
            }
        });

        //Initialize ViewPager
        viewPager = (ViewPager) findViewById(R.id.pager);
        pageAdapter = new MyAdapter(fragmentManager);
        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected " + position);
                if (position == 0) {
                    fab.setVisibility(View.VISIBLE);
                } else {
                    fab.clearAnimation();
                    fab.setVisibility(View.GONE);
                }
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Initialize FloatingActionButton
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddExpenseActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });
        ScaleAnimation anim = new ScaleAnimation(0, 1, 0, 1);
        anim.setFillBefore(true);
        anim.setFillAfter(true);
        anim.setFillEnabled(true);
        anim.setDuration(300);
        anim.setInterpolator(new OvershootInterpolator());
        fab.setAnimation(anim);

        //Initialize TabLayout
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        tabLayout.setTabsFromPagerAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        prefs = this.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains(AppConstants.SIGN_IN_COMPLETED)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(AppConstants.SIGN_IN_COMPLETED, true);
            editor.putBoolean(AppConstants.FIRST_TIME_LOGIN, true);
            editor.apply();
        }

        Intent pendingIntent = getIntent();
        if (pendingIntent.getBooleanExtra("notification", false) && pendingIntent.getBooleanExtra("newExpenseAvailable", false)) {
            newExpenseAvailable = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "inside onStart");
        drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setup(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "inside onStop");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "inside onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "inside onConnectionSuspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "inside onConnectionFailed");
        Log.d(TAG, "Failure Reason: " + connectionResult.toString());
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this,
                    0).show();
        }
    }

    public void showFab() {
        if (currentPosition == 0 && backStackCount == 0) {
            fab.setVisibility(View.VISIBLE);
        }
    }

    public void hideFab() {
        fab.clearAnimation();
        fab.setVisibility(View.GONE);
    }

    public class MyAdapter extends FragmentStatePagerAdapter {

        SparseArray<Fragment> registeredFragments = new SparseArray<>();

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0) {
                fragment = new ExpenseDataListFragment();
                if (newExpenseAvailable) {
                    Log.d(TAG, "new expense available");
                    Bundle bundle = new Bundle();
                    bundle.putInt("newExpenseAvailable", 1);
                    fragment.setArguments(bundle);
                    newExpenseAvailable = false;
                }
            } else if (position == 1) {
                fragment = new SearchFlatFragment();
            } else if (position == 2) {
                fragment = new SearchRoomMateFragment();
            } else if (position == 3) {
                fragment = new NewsFeedFragment();
            }
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Expenses";
            } else if (position == 1) {
                return "Search Flats";
            } else if (position == 2) {
                return "Search RoomMates";
            } else if (position == 3) {
                return "Activities";
            } else {
                return null;
            }
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }
}