package com.example.vivek.rentalmates.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.fragments.ExpenseDataListFragment;
import com.example.vivek.rentalmates.fragments.NavigationDrawerFragment;
import com.example.vivek.rentalmates.fragments.NewsFeedFragment;
import com.example.vivek.rentalmates.fragments.SearchFragment;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.tabs.SlidingTabLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.melnykov.fab.FloatingActionButton;

public class MainTabActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainTabActivity_Debug";

    private ViewPager viewPager;
    private SlidingTabLayout mTabs;
    private Toolbar toolBar;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences prefs;
    private NavigationDrawerFragment drawerFragment;
    private MyAdapter pageAdapter;
    private FloatingActionButton fab;
    private int currentPosition;
    private boolean newExpenseAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        currentPosition = 0;
        newExpenseAvailable = false;

        toolBar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolBar);
        toolBar.setTitleTextColor(getResources().getColor(R.color.white));

        //Initialize ViewPager
        viewPager = (ViewPager) findViewById(R.id.pager);
        pageAdapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);

        //Initialize FloatingActionButton
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setType(FloatingActionButton.TYPE_NORMAL);
        fab.setShadow(true);
        //fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddExpenseActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });

        //Initialize SlidingTabLayout
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        mTabs.setDistributeEvenly(true);
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });
        mTabs.setViewPager(viewPager);
        mTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int newPosition, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int newPosition) {
                Log.d(TAG, "onPageSelected " + newPosition);
                if (newPosition == 0) {
                    fab.show();
                } else {
                    fab.hide();
                }
                currentPosition = newPosition;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

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

        String finalTitle = toolBar.getTitle() + ": " + prefs.getString(AppConstants.PRIMARY_FLAT_NAME, "no_flat_name");
        getSupportActionBar().setTitle(finalTitle);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
        drawerFragment.setup(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolBar);
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
        if (currentPosition == 0) {
            fab.show();
        }
    }

    public void hideFab() {
        fab.hide();
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
                fragment = new NewsFeedFragment();
            } else if (position == 2) {
                fragment = new SearchFragment();
            }
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Expenses";
            } else if (position == 1) {
                return "Activities";
            } else if (position == 2) {
                return "Search";
            } else {
                return null;
            }
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }
}