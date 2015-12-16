package com.example.vivek.rentalmates.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
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
import android.support.v7.app.ActionBar;
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

import com.cocosw.bottomsheet.BottomSheet;
import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.fragments.DevelopersFragment;
import com.example.vivek.rentalmates.fragments.ExpenseManagerFragment;
import com.example.vivek.rentalmates.fragments.MainFragment;
import com.example.vivek.rentalmates.fragments.ManageFlatsFragment;
import com.example.vivek.rentalmates.fragments.RequestsFragment;
import com.example.vivek.rentalmates.fragments.SearchFlatFragment;
import com.example.vivek.rentalmates.fragments.SearchRoomMateFragment;
import com.example.vivek.rentalmates.fragments.SharedContactsListFragment;
import com.example.vivek.rentalmates.interfaces.FragmentTransactionRequestReceiver;
import com.example.vivek.rentalmates.library.CreateNewExpenseGroupTask;
import com.example.vivek.rentalmates.library.RegisterNewFlatTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.pkmmte.view.CircularImageView;

import java.util.HashMap;

public class MainTabActivity extends AppCompatActivity implements FragmentTransactionRequestReceiver, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

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
    private FloatingActionButton addFab;
    private FloatingActionButton filterFlatsFab;
    private FloatingActionButton filterRoomMatesFab;
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

    private HashMap<String, ActivityEventReceiver> eventReceiverHashMap;

    public interface ActivityEventReceiver {
        void onEventReceived(String eventType);
    }

    public boolean registerForActivityEvents(String fragment, ActivityEventReceiver receiver) {
        if (eventReceiverHashMap.containsKey(fragment))
            return false;
        eventReceiverHashMap.put(fragment, receiver);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreate");
        eventReceiverHashMap = new HashMap<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        currentPosition = 0;
        backStackCount = 0;
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
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            //ab.setHomeAsUpIndicator(R.drawable.ic_menu_search);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        //Initialize FragmentManager
        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                backStackCount = fragmentManager.getBackStackEntryCount();
                if (backStackCount == 0) {
                    navigationView.getMenu().findItem(R.id.drawer_item_home).setChecked(true);
                    toolbar.setTitle("RentalMates");
                }
                updateFab();
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
                currentPosition = position;
                updateFab();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Initialize Add FloatingActionButton
        addFab = (FloatingActionButton) findViewById(R.id.addFab);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayBottomSheet();
            }
        });
        ScaleAnimation anim1 = new ScaleAnimation(0, 1, 0, 1);
        anim1.setFillBefore(true);
        anim1.setFillAfter(true);
        anim1.setFillEnabled(true);
        anim1.setDuration(300);
        anim1.setInterpolator(new OvershootInterpolator());
        addFab.setAnimation(anim1);
        addFab.animate();
        addFab.setVisibility(View.VISIBLE);

        //Initialize Flats Filter FloatingActionButton
        filterFlatsFab = (FloatingActionButton) findViewById(R.id.filterFlatsFab);
        filterFlatsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventReceiverHashMap.containsKey("searchFlatsFragment")) {
                    eventReceiverHashMap.get("searchFlatsFragment").onEventReceived("filterFlatsFABPressed");
                }
            }
        });
        ScaleAnimation anim2 = new ScaleAnimation(0, 1, 0, 1);
        anim2.setFillBefore(true);
        anim2.setFillAfter(true);
        anim2.setFillEnabled(true);
        anim2.setDuration(300);
        anim2.setInterpolator(new OvershootInterpolator());
        filterFlatsFab.setAnimation(anim2);
        filterFlatsFab.animate();
        filterFlatsFab.setVisibility(View.GONE);

        //Initialize RoomMates Filter FloatingActionButton
        filterRoomMatesFab = (FloatingActionButton) findViewById(R.id.filterRoomMatesFab);
        filterRoomMatesFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventReceiverHashMap.containsKey("searchRoomMatesFragment")) {
                    eventReceiverHashMap.get("searchRoomMatesFragment").onEventReceived("filterRoomMatesFABPressed");
                }
            }
        });
        ScaleAnimation anim3 = new ScaleAnimation(0, 1, 0, 1);
        anim3.setFillBefore(true);
        anim3.setFillAfter(true);
        anim3.setFillEnabled(true);
        anim3.setDuration(300);
        anim3.setInterpolator(new OvershootInterpolator());
        filterRoomMatesFab.setAnimation(anim3);
        filterRoomMatesFab.animate();
        filterRoomMatesFab.setVisibility(View.GONE);

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
        userNameTextView = (TextView) headerView.findViewById(R.id.userNameTextView);
        String userName = prefs.getString(AppConstants.USER_NAME, "no_user_name");
        userNameTextView.setText(userName);
        emailTextView = (TextView) headerView.findViewById(R.id.userEmailTextView);
        emailTextView.setText(emailId);

        // Initialize DrawerToggle
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
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
        Toast.makeText(this, "gcm values: " + appData.getGcmData().values().toString(), Toast.LENGTH_LONG).show();
    }

    public void displayBottomSheet() {
        new BottomSheet.Builder(this, R.style.BottomSheet_Dialog)
                .title("New")
                .grid()
                .sheet(R.menu.menu_bottom_sheet)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.newFlatMenu:
                                registerNewFlat();
                                break;
                            case R.id.newExpenseGroupMenu:
                                createNewExpenseGroup();
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void registerNewFlat() {
        RegisterNewFlatTask registerNewFlatTask = new RegisterNewFlatTask(MainTabActivity.this, getSupportFragmentManager(), "NONE");
        registerNewFlatTask.setOnRegisterNewFlatTask(new RegisterNewFlatTask.OnRegisterNewFlatTask() {
            @Override
            public void onRegisterNewFlatTaskSuccess(com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo newFlatInfo) {
            }

            @Override
            public void onRegisterNewFlatTaskFailed() {

            }
        });
        registerNewFlatTask.execute();
    }

    private void createNewExpenseGroup() {
        CreateNewExpenseGroupTask task = new CreateNewExpenseGroupTask(MainTabActivity.this, getSupportFragmentManager());
        task.setOnRegisterNewFlatTask(new CreateNewExpenseGroupTask.OnCreateNewExpenseGroupTask() {
            @Override
            public void onCreateNewExpenseGroupTaskSuccess(com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseGroup expenseGroup) {

            }

            @Override
            public void onCreateNewExpenseGroupTaskFailed() {

            }
        });
        task.execute();
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
                for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                    fragmentManager.popBackStack();
                }
                break;
            case R.id.drawer_item_profile:
                break;
            case R.id.drawer_item_flats:
                toolbar.setTitle("Flats");
                ft.replace(R.id.fragmentFrameLayout, new ManageFlatsFragment());
                ft.addToBackStack("ManageFlatsFragment");
                ft.commit();
                break;
            case R.id.drawer_item_account_settings:
                intent = new Intent(this, MyLoginActivity.class);
                this.startActivity(intent);
                break;
            case R.id.drawer_item_developer_mode:
                toolbar.setTitle("Developers Fragment");
                ft.replace(R.id.fragmentFrameLayout, new DevelopersFragment());
                ft.addToBackStack("DevelopersFragment");
                ft.commit();
                break;
            case R.id.drawer_item_requests:
                toolbar.setTitle("Requests");
                ft.replace(R.id.fragmentFrameLayout, new RequestsFragment());
                ft.addToBackStack("RequestsFragment");
                ft.commit();
                break;
            case R.id.drawer_item_about:
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

    public void updateFab() {
        if (currentPosition == 0 && backStackCount == 0) {
            addFab.setVisibility(View.VISIBLE);
            filterFlatsFab.setVisibility(View.GONE);
            filterFlatsFab.clearAnimation();
            filterRoomMatesFab.setVisibility(View.GONE);
            filterRoomMatesFab.clearAnimation();
        } else if (currentPosition == 1 && backStackCount == 0) {
            addFab.setVisibility(View.GONE);
            addFab.clearAnimation();
            filterFlatsFab.setVisibility(View.VISIBLE);
            filterRoomMatesFab.setVisibility(View.GONE);
            filterRoomMatesFab.clearAnimation();
        } else if (currentPosition == 2 && backStackCount == 0) {
            addFab.setVisibility(View.GONE);
            addFab.clearAnimation();
            filterFlatsFab.setVisibility(View.GONE);
            filterFlatsFab.clearAnimation();
            filterRoomMatesFab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void OnFragmentTransactionRequest(String requestType) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        switch (requestType) {
            case "SharedContacts":
                toolbar.setTitle("Shared Contacts");
                ft.replace(R.id.fragmentFrameLayout, new SharedContactsListFragment());
                ft.addToBackStack("SharedContactsListFragment");
                ft.commit();
                break;
            case "ExpenseManager":
                toolbar.setTitle("Expense Manager");
                ft.replace(R.id.fragmentFrameLayout, new ExpenseManagerFragment());
                ft.addToBackStack("ExpenseManagerFragment");
                ft.commit();
                break;
            default:
                break;
        }
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
                fragment = new MainFragment();
            } else if (position == 1) {
                fragment = new SearchFlatFragment();
            } else if (position == 2) {
                fragment = new SearchRoomMateFragment();
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
                return "Home";
            } else if (position == 1) {
                return "Search Flats";
            } else if (position == 2) {
                return "Search RoomMates";
            } else {
                return null;
            }
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }
}