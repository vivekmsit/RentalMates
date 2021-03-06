package com.example.vivek.rentalmates.activities;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.data.LocalFlatInfo;
import com.example.vivek.rentalmates.fragments.ExpenseDataListFragment;
import com.example.vivek.rentalmates.fragments.SharedContactsListFragment;
import com.example.vivek.rentalmates.services.BackendApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FlatManagerActivity extends AppCompatActivity {
    private static final String TAG = "MainTabActivity_Debug";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private FloatingActionButton addFAB;
    private int currentPosition;
    private Spinner toolbarSpinner;
    private AppData appData;
    private Context context;
    private List<LocalFlatInfo> localFlats = new ArrayList<>();

    //Communicating Activity events to receiver fragments.
    private HashMap<String, OnActivityEventReceiver> eventReceiverHashMap;

    public interface OnActivityEventReceiver {
        void onEventReceived(String eventType);
    }

    public boolean registerForActivityEvents(String fragment, OnActivityEventReceiver receiver) {
        if (eventReceiverHashMap.containsKey(fragment))
            return false;
        eventReceiverHashMap.put(fragment, receiver);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        eventReceiverHashMap = new HashMap<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flat_manager);

        context = getApplicationContext();
        appData = AppData.getInstance();

        //Initialize Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize Toolbar Spinner
        toolbarSpinner = (Spinner) toolbar.findViewById(R.id.toolbarSpinner);
        for (LocalFlatInfo localFlatInfo : appData.getFlats().values()) {
            localFlats.add(localFlatInfo);
        }
        List<String> flatNames = new ArrayList<>();
        for (LocalFlatInfo flat : localFlats) {
            flatNames.add(flat.getFlatName());
        }
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, R.layout.flat_manager_toolbar_spinner_item, flatNames);
        toolbarSpinner.setAdapter(stringArrayAdapter);
        toolbarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LocalFlatInfo flatInfo = localFlats.get(position);
                BackendApiService.storePrimaryFlatId(context, flatInfo.getFlatId());
                BackendApiService.storePrimaryFlatName(context, flatInfo.getFlatName());
                BackendApiService.storeFlatExpenseGroupId(context, flatInfo.getFlatExpenseGroupId());
                notifyPrimaryFlatChangedEvent();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Initialize ViewPager and TabLayout
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //Initialize Add FAB
        addFAB = (FloatingActionButton) findViewById(R.id.addFab);
        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddFABClicked();
            }
        });

    }

    private void onAddFABClicked() {
        if (currentPosition == 0) {
            if (eventReceiverHashMap.containsKey("expensesFragment")) {
                eventReceiverHashMap.get("expensesFragment").onEventReceived("addFABPressed");
            }
        } else if (currentPosition == 1) {
            if (eventReceiverHashMap.containsKey("sharedContactsFragment")) {
                eventReceiverHashMap.get("sharedContactsFragment").onEventReceived("addFABPressed");
            }
        }
    }

    private void notifyPrimaryFlatChangedEvent() {
        if (eventReceiverHashMap.containsKey("expensesFragment")) {
            eventReceiverHashMap.get("expensesFragment").onEventReceived("primaryFlatChanged");
        }
        if (eventReceiverHashMap.containsKey("sharedContactsFragment")) {
            eventReceiverHashMap.get("sharedContactsFragment").onEventReceived("primaryFlatChanged");
        }
    }

    private void updateFABs() {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new ExpenseDataListFragment();
                    break;
                case 1:
                    fragment = new SharedContactsListFragment();
                    break;
                default:
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Expenses";
                case 1:
                    return "Shared Contacts";
                default:
                    break;
            }
            return null;
        }
    }
}
