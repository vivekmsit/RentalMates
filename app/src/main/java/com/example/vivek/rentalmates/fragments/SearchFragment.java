package com.example.vivek.rentalmates.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.tabs.SlidingTabLayout;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment_Debug";

    private SlidingTabLayout mTabs;
    private ViewPager viewPager;
    private MySearchAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreateView");
        View layout = inflater.inflate(R.layout.fragment_search, container, false);

        //Initialize ViewPager
        viewPager = (ViewPager) layout.findViewById(R.id.roommatePager);
        adapter = new MySearchAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);

        //Initialize SlidingTabLayout
        mTabs = (SlidingTabLayout) layout.findViewById(R.id.roommateTabs);
        mTabs.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        //mTabs.setDistributeEvenly(true);
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });
        mTabs.setViewPager(viewPager);
        return layout;
    }

    public class MySearchAdapter extends FragmentStatePagerAdapter {

        SparseArray<Fragment> registeredFragments = new SparseArray<>();

        public MySearchAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "inside getItem() with position " + position);
            Fragment fragment = null;
            if (position == 0) {
                fragment = new SearchRoomMateFragment();
            } else if (position == 1) {
                fragment = new SearchFlatFragment();
            }
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "RoomMates";
            } else if (position == 1) {
                return "Flats";
            } else {
                return null;
            }
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }
}
