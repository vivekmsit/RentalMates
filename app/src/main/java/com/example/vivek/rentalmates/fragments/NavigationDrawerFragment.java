package com.example.vivek.rentalmates.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.MainActivity;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.others.AppData;

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
    private ImageView drawerImageView;

    public NavigationDrawerFragment() {
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
        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(KEY_USER_LEARNED_DRAWER, "false"));
        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }
        drawerImageView = (ImageView) getView().findViewById(R.id.drawerImageView);
        AppData appData = AppData.getInstance();
        String emailId = prefs.getString(AppConstants.EMAIL_ID, "no_email_id");
        drawerImageView.setImageBitmap(appData.getProfilePictureBitmap(getActivity(), emailId));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
    }


    public void setup(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        Log.d(TAG, "inside setup");
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    saveToPreferences(KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer + "");
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
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
        }

        mDrawerLayout.setDrawerListener(drawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });
    }

    public void saveToPreferences(String preferenceName, String preferenceValue) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public String readFromPreferences(String preferenceName, String defaultValue) {
        return prefs.getString(preferenceName, defaultValue);
    }
}
