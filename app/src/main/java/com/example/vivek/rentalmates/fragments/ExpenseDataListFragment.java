package com.example.vivek.rentalmates.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vivek.rentalmates.adapters.ExpenseListViewAdapter;
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

    private List<ExpenseListViewItem> mItems = new ArrayList<>();

    public ExpenseDataListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreate");
        super.onCreate(savedInstanceState);

        List<LocalExpenseData> expenses = getExpenses();
        if (expenses == null){
            mItems.add(new ExpenseListViewItem(0, "Description", "ownerEmailId"));
        }
        else {
            for (LocalExpenseData expenseData : expenses) {
                mItems.add(new ExpenseListViewItem(expenseData.getAmount(), expenseData.getDescription(), expenseData.getOwner()));
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

    //get list of expensedata from expenses.tmp file
    public List<LocalExpenseData> getExpenses(){
        List<LocalExpenseData> localExpenses = new ArrayList<>();
        String path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES).getPath();
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(path+"/"+"expenses.tmp");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(fis);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            localExpenses = (List<LocalExpenseData>) ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return localExpenses;
    }
}
