package com.example.vivek.rentalmates.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vivek.rentalmates.adapters.ExpenseListViewAdapter;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.ExpenseData;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfile;
import com.example.vivek.rentalmates.others.AppData;
import com.example.vivek.rentalmates.others.LocalExpenseData;
import com.example.vivek.rentalmates.viewholders.ExpenseListViewItem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDataListFragment extends android.support.v4.app.ListFragment {

    private static final String TAG = "ExpenseList_Debug";

    AppData appData;
    Context context;
    private List<ExpenseListViewItem> mItems = new ArrayList<>();

    public ExpenseDataListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreate");
        super.onCreate(savedInstanceState);
        appData = AppData.getInstance();
        context = getActivity().getApplicationContext();

        List<ExpenseData> expenses = appData.getExpenses();
        if (expenses == null) {
            ExpenseData data = new ExpenseData();
            data.setAmount(0);
            data.setDescription("Description");
            data.setOwnerEmailId("vivekmsit@gmail.com");
            mItems.add(new ExpenseListViewItem(data));
        } else {
            for (ExpenseData expenseData : expenses) {
                mItems.add(new ExpenseListViewItem(expenseData));
            }
        }
        setListAdapter(new ExpenseListViewAdapter(getActivity(), mItems));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setDivider(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ExpenseListViewItem item = mItems.get(position);
        Toast.makeText(getActivity(), item.description, Toast.LENGTH_SHORT).show();
    }
}
