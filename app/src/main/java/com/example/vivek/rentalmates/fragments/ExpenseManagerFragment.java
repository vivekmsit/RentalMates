package com.example.vivek.rentalmates.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
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
import com.example.vivek.rentalmates.activities.AddExpenseActivity;

public class ExpenseManagerFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "ExpenseManager_Debug";
    private Context context;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fab;
    private ExpenseManagerAdapter pagerAdapter;
    private int currentPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_expense_manager, container, false);
        context = getActivity().getApplicationContext();
        viewPager = (ViewPager) layout.findViewById(R.id.pager);
        pagerAdapter = new ExpenseManagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
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

        tabLayout = (TabLayout) layout.findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        tabLayout.setTabsFromPagerAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        fab = (FloatingActionButton) layout.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddExpenseActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        return layout;
    }


    public class ExpenseManagerAdapter extends FragmentStatePagerAdapter {

        SparseArray<Fragment> registeredFragments = new SparseArray<>();

        public ExpenseManagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "inside getItem() with position " + position);
            Fragment fragment = null;
            if (position == 0) {
                fragment = new ExpenseDataListFragment();
            } else if (position == 1) {
                fragment = new ManageExpenseGroupsFragment();
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
                return "Expenses";
            } else if (position == 1) {
                return "Expense Groups";
            } else {
                return null;
            }
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

}
