package com.example.vivek.rentalmates.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.fragments.DevelopersFragment;
import com.example.vivek.rentalmates.fragments.ExpenseManagerFragment;
import com.example.vivek.rentalmates.fragments.ManageFlatsFragment;
import com.example.vivek.rentalmates.fragments.RequestsFragment;
import com.example.vivek.rentalmates.fragments.SearchFlatFragment;
import com.example.vivek.rentalmates.fragments.SearchRoomMateFragment;
import com.example.vivek.rentalmates.fragments.SharedContactsListFragment;
import com.example.vivek.rentalmates.interfaces.FragmentTransactionRequestReceiver;
import com.example.vivek.rentalmates.library.CreateNewExpenseGroupTask;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pkmmte.view.CircularImageView;

import java.util.HashMap;

public class MainTabActivity extends AppCompatActivity implements FragmentTransactionRequestReceiver, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainTabActivity_Debug";
    private static final String NAV_ITEM_ID = "navItemId";
    private static final int REGISTER_NEW_FLAT = 1;

    private Context context;
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
    private int currentPosition;
    private int backStackCount;
    private boolean newExpenseAvailable;
    private int mNavItemId;

    //Communicating Activity events to receiver fragments.
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
        eventReceiverHashMap = new HashMap<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        context = getApplicationContext();

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

        //Initialize Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize DrawerLayout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        //Initialize FragmentManager
        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                backStackCount = fragmentManager.getBackStackEntryCount();
                if (backStackCount == 0) {
                    toolbar.setTitle("RentalMates");
                }
                updateFABs();
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
                updateFABs();
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
        tabLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryColor));
        tabLayout.setupWithViewPager(viewPager);

        // Initialize NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem menuItem) {
                return navigate(menuItem);
            }
        });

        //Initialize HeaderView
        View headerView = navigationView.getHeaderView(0);
        circularImageView = (CircularImageView) headerView.findViewById(R.id.drawerHeaderImageView);
        String emailId = prefs.getString(AppConstants.EMAIL_ID, "no_email_id");
        circularImageView.setImageBitmap(appData.getProfilePictureBitmap(emailId));
        userNameTextView = (TextView) headerView.findViewById(R.id.nameTextView);
        String userName = prefs.getString(AppConstants.USER_NAME, "no_user_name");
        userNameTextView.setText(userName);
        emailTextView = (TextView) headerView.findViewById(R.id.emailTextView);
        emailTextView.setText(emailId);

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        if (prefs.contains(AppConstants.SIGN_IN_COMPLETED)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(AppConstants.SIGN_IN_COMPLETED, true);
            editor.putBoolean(AppConstants.FIRST_TIME_LOGIN, true);
            editor.apply();
        }

        updateFABs();

        Intent pendingIntent = getIntent();
        if (pendingIntent.getBooleanExtra("notification", false) && pendingIntent.getBooleanExtra("newExpenseAvailable", false)) {
            newExpenseAvailable = true;
        }
        //Toast.makeText(this, "gcm values: " + appData.getGcmData().values().toString(), Toast.LENGTH_LONG).show();
    }

    public void displayBottomSheet() {
        Toast.makeText(this, "To be implemented", Toast.LENGTH_SHORT).show();
        /*
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
                }).show();*/
    }

    private void registerNewFlat() {
        Intent intent = new Intent(this, NewFlatActivity.class);
        startActivity(intent);
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

    public boolean navigate(MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.drawer_item_home:
                for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                    fragmentManager.popBackStack();
                }
                break;
            case R.id.drawer_item_profile:
                break;
            case R.id.drawer_item_flats:
                toolbar.setTitle("Flats");
                ft.replace(R.id.mainTabActivityFrameLayout, new ManageFlatsFragment());
                ft.addToBackStack("ManageFlatsFragment");
                ft.commit();
                break;
            case R.id.drawer_item_account_settings:
                intent = new Intent(this, MyLoginActivity.class);
                this.startActivity(intent);
                break;
            case R.id.drawer_item_developer_mode:
                toolbar.setTitle("Developers Fragment");
                ft.replace(R.id.mainTabActivityFrameLayout, new DevelopersFragment());
                ft.addToBackStack("DevelopersFragment");
                ft.commit();
                break;
            case R.id.drawer_item_requests:
                toolbar.setTitle("Requests");
                ft.replace(R.id.mainTabActivityFrameLayout, new RequestsFragment());
                ft.addToBackStack("RequestsFragment");
                ft.commit();
                break;
            case R.id.drawer_item_about:
                break;
            case R.id.drawer_item_help:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    public void updateFABs() {
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
        } else {
            addFab.setVisibility(View.GONE);
            addFab.clearAnimation();
            filterFlatsFab.setVisibility(View.GONE);
            filterFlatsFab.clearAnimation();
            filterRoomMatesFab.setVisibility(View.GONE);
            filterRoomMatesFab.clearAnimation();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTER_NEW_FLAT) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(context, "new Flat Created", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0) {
                fragment = new ManageFlatsFragment();
            } else if (position == 1) {
                fragment = new SearchFlatFragment();
            } else if (position == 2) {
                fragment = new SearchRoomMateFragment();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "My Flats";
            } else if (position == 1) {
                return "Search Flats";
            } else if (position == 2) {
                return "Search RoomMates";
            } else {
                return null;
            }
        }
    }
}