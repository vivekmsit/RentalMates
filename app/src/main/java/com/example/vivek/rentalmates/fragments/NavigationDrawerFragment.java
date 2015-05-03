package com.example.vivek.rentalmates.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.MainActivity;
import com.example.vivek.rentalmates.adapters.DrawerListViewAdapter;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.others.AppData;
import com.example.vivek.rentalmates.viewholders.DrawerListItem;
import com.melnykov.fab.FloatingActionButton;
import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "Navigation_Debug";
    private static final String KEY_USER_LEARNED_DRAWER = "key_user_learned_drawer";

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout mDrawerLayout;

    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private SharedPreferences prefs;
    private View containerView;
    private TextView userNameTextView;
    private TextView emailTextView;
    private RecyclerView recyclerView;
    private DrawerListViewAdapter drawerListViewAdapter;
    private CircularImageView circularImageView;

    public NavigationDrawerFragment() {
        mUserLearnedDrawer = false;
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "inside onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        prefs = getActivity().getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        mUserLearnedDrawer = prefs.getBoolean(KEY_USER_LEARNED_DRAWER, false);
        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }

        //Initialize CircularImageView
        circularImageView = (CircularImageView) getView().findViewById(R.id.drawerImageView);
        AppData appData = AppData.getInstance();
        String emailId = prefs.getString(AppConstants.EMAIL_ID, "no_email_id");
        circularImageView.setImageBitmap(appData.getProfilePictureBitmap(getActivity(), emailId));

        //Initialize UserName TextView
        userNameTextView = (TextView) getView().findViewById(R.id.userNameTextView);
        String userName = prefs.getString(AppConstants.USER_NAME, "no_user_name");
        userNameTextView.setText(userName);

        //Initialize UserEmailId TextView
        emailTextView = (TextView) getView().findViewById(R.id.userEmailTextView);
        emailTextView.setText(emailId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreateView");

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.listDrawer);
        drawerListViewAdapter = new DrawerListViewAdapter(getActivity(), getData());
        recyclerView.setAdapter(drawerListViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return layout;
    }


    public static List<DrawerListItem> getData() {
        Log.d(TAG, "inside getData");
        List<DrawerListItem> data = new ArrayList<>();
        int[] icons = {R.drawable.ic_action_person_light, R.drawable.ic_action_person_light, R.drawable.ic_action_person_light, R.drawable.ic_action_settings_light, R.drawable.ic_action_person_light, R.drawable.ic_action_about_light};
        String[] titles = {"Manage Profile", "Manage Flat Info", "Manage Services", "Account Settings", "Developer Mode", "About"};
        for (int i = 0; i < titles.length && i < icons.length; i++) {
            DrawerListItem current = new DrawerListItem();
            current.iconId = icons[i];
            current.title = titles[i];
            data.add(current);
        }
        return data;
    }


    public void setup(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        Log.d(TAG, "inside setup");
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
                FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
                if (fab != null) {
                    fab.hide();
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
                FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
                if (fab != null) {
                    fab.show();
                }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset < 0.6) {
                    toolbar.setAlpha(1 - slideOffset);
                }
            }
        };

        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(containerView);
            mUserLearnedDrawer = true;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_USER_LEARNED_DRAWER, true);
            editor.apply();
        }

        mDrawerLayout.setDrawerListener(drawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });
    }
}
