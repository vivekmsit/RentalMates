package com.example.vivek.rentalmates.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.fragments.ExpenseDataListFragment;
import com.example.vivek.rentalmates.fragments.MainFragment;
import com.example.vivek.rentalmates.fragments.NewsFeedFragment;
import com.example.vivek.rentalmates.fragments.SearchRoomiesFragment;
import com.example.vivek.rentalmates.fragments.SettingsFragment;
import com.example.vivek.rentalmates.others.AppConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

public class MainTabActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainTabActivity_Debug";

    private ViewPager viewPager;
    private Toolbar toolBar;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        viewPager = (ViewPager) findViewById(R.id.pager);
        toolBar = (Toolbar) findViewById(R.id.app_bar);

        setSupportActionBar(toolBar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new MyAdapter(fragmentManager));

        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        prefs = this.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        if (prefs.contains(AppConstants.SIGN_IN_COMPLETED)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(AppConstants.SIGN_IN_COMPLETED, true);
            editor.putBoolean(AppConstants.FIRST_TIME_LOGIN, true);
            editor.commit();
        }
        String finalTitle = toolBar.getTitle() + prefs.getString(AppConstants.PRIMARY_FLAT_NAME, "no_flat_name");
        toolBar.setTitle(finalTitle);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "inside onStart");
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
            return;
        }
    }

    class MyAdapter extends FragmentStatePagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0) {
                fragment = new MainFragment();
            } else if (position == 1) {
                fragment = new ExpenseDataListFragment();
            } else if (position == 2) {
                fragment = new SearchRoomiesFragment();
            } else if (position == 3) {
                fragment = new NewsFeedFragment();
            } else if (position == 4) {
                fragment = new SettingsFragment();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "MainFragment";
            } else if (position == 1) {
                return "Expenses";
            } else if (position == 2) {
                return "Search Roommates";
            } else if (position == 3) {
                return "News Feed";
            } else if (position == 4) {
                return "Settings";
            } else {
                return null;
            }
        }
    }
}