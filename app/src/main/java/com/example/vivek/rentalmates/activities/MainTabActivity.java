package com.example.vivek.rentalmates.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.fragments.ExpenseDataListFragment;
import com.example.vivek.rentalmates.fragments.NewsFeedFragment;
import com.example.vivek.rentalmates.fragments.SearchFlatFragment;
import com.example.vivek.rentalmates.fragments.SearchRoomMateFragment;
import com.example.vivek.rentalmates.data.AppConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.pkmmte.view.CircularImageView;

public class MainTabActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainTabActivity_Debug";
    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    private static final String NAV_ITEM_ID = "navItemId";

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences prefs;
    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    private MyAdapter pageAdapter;
    private FloatingActionButton fab;
    private NavigationView navigationView;
    private CircularImageView circularImageView;
    private TextView userNameTextView;
    private TextView emailTextView;
    private ActionBarDrawerToggle mDrawerToggle;
    private AppData appData;
    private final Handler mDrawerActionHandler = new Handler();
    private int currentPosition;
    private int backStackCount;
    private boolean newExpenseAvailable;
    private int mNavItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        currentPosition = 0;
        newExpenseAvailable = false;
        appData = AppData.getInstance();
        prefs = this.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);

        // load saved navigation state if present
        if (null == savedInstanceState) {
            mNavItemId = R.id.drawer_item_home;
        } else {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        //To be moved to xml file later when issue will be fixed
        tabLayout.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        tabLayout.setTabsFromPagerAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // Initialize NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {
                // update highlighted item in the navigation menu
                menuItem.setChecked(true);
                mNavItemId = menuItem.getItemId();

                // allow some time after closing the drawer before performing real navigation
                // so the user can see what is happening
                drawerLayout.closeDrawer(GravityCompat.START);
                mDrawerActionHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        navigate(menuItem.getItemId());
                    }
                }, DRAWER_CLOSE_DELAY_MS);
                return true;
            }
        });
        // select the correct nav menu item
        navigationView.getMenu().findItem(mNavItemId).setChecked(true);

        //Initialize HeaderView
        View headerView = navigationView.inflateHeaderView(R.layout.drawer_header);
        circularImageView = (CircularImageView) headerView.findViewById(R.id.drawerImageView);
        String emailId = prefs.getString(AppConstants.EMAIL_ID, "no_email_id");
        circularImageView.setImageBitmap(appData.getProfilePictureBitmap(emailId));
        //Initialize UserName TextView
        userNameTextView = (TextView) headerView.findViewById(R.id.userNameTextView);
        String userName = prefs.getString(AppConstants.USER_NAME, "no_user_name");
        userNameTextView.setText(userName);
        //Initialize UserEmailId TextView
        emailTextView = (TextView) headerView.findViewById(R.id.userEmailTextView);
        emailTextView.setText(emailId);
//        navigationView.addHeaderView(headerView);

        // Initialize DrawerToggle
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideFab();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                showFab();
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        navigate(mNavItemId);

        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

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
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.support.v7.appcompat.R.id.home) {
            return mDrawerToggle.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, mNavItemId);
    }

    private void navigate(final int itemId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Intent intent;
        switch (itemId) {
            case R.id.drawer_item_home:
                break;
            case R.id.drawer_item_profile:
                break;
            case R.id.drawer_item_flats:
                intent = new Intent(this, ManageFlatsActivity.class);
                this.startActivity(intent);
                break;
            case R.id.drawer_item_expense_groups:
                intent = new Intent(this, ManageExpenseGroupsActivity.class);
                this.startActivity(intent);
                break;
            case R.id.drawer_item_account_settings:
                intent = new Intent(this, MyLoginActivity.class);
                this.startActivity(intent);
                break;
            case R.id.drawer_item_developer_mode:
                intent = new Intent(this, DeveloperModeActivity.class);
                this.startActivity(intent);
                break;
            case R.id.drawer_item_requests:
                break;
            case R.id.drawer_item_about:
                ft.replace(R.id.mainDrawerView, new NewsFeedFragment());
                ft.addToBackStack("NewsFeedFragment");
                ft.commit();
                break;
            case R.id.drawer_item_help:
                break;
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