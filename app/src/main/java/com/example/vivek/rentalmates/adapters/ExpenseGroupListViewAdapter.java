package com.example.vivek.rentalmates.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.data.LocalExpenseGroup;
import com.example.vivek.rentalmates.viewholders.ExpenseGroupListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExpenseGroupListViewAdapter extends RecyclerView.Adapter<ExpenseGroupListViewAdapter.ExpenseGroupViewHolder> {

    private static final String TAG = "ExpenseAdapter_Debug";

    private List<ExpenseGroupListItem> data;
    private LayoutInflater inflater;
    private Context context;
    private AppData appData;
    private SharedPreferences prefs;
    private FragmentManager manager;

    public ExpenseGroupListViewAdapter(Context context, FragmentManager manager) {
        Log.d(TAG, "inside Constructor");
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.appData = AppData.getInstance();
        this.data = new ArrayList<>();
        this.manager = manager;
        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        updateExpenseGroupsData();
    }

    public void updateExpenseGroupsData() {
        this.data.clear();
        Long userProfileId = prefs.getLong(AppConstants.USER_PROFILE_ID, 0);
        for (LocalExpenseGroup localExpenseGroup : appData.getExpenseGroups()) {
            this.data.add(new ExpenseGroupListItem(localExpenseGroup, userProfileId));
        }
    }

    @Override
    public ExpenseGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "inside onCreateViewHolder");
        View view = inflater.inflate(R.layout.expense_group_list_item, parent, false);
        return new ExpenseGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExpenseGroupViewHolder viewHolder, int position) {
        Log.d(TAG, "inside onBindViewHolder");
        ExpenseGroupListItem current = data.get(position);
        viewHolder.flatName.setText(current.flatName);
        viewHolder.date.setText(current.date);
        viewHolder.location.setText(current.location);
        viewHolder.members.setText(current.ownerName);
        viewHolder.ownerName.setText(current.ownerName);
        viewHolder.payBackAmountValue.setText(current.paybackAmountValue);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     */
    class ExpenseGroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView flatName;
        TextView ownerName;
        TextView location;
        TextView members;
        TextView date;
        TextView payBackAmountValue;

        public ExpenseGroupViewHolder(View itemView) {
            super(itemView);
            flatName = (TextView) itemView.findViewById(R.id.flatNameTextView);
            ownerName = (TextView) itemView.findViewById(R.id.ownerTextView);
            location = (TextView) itemView.findViewById(R.id.locationTextView);
            members = (TextView) itemView.findViewById(R.id.membersTextView);
            date = (TextView) itemView.findViewById(R.id.dateTextView);
            payBackAmountValue = (TextView) itemView.findViewById(R.id.paybackAmountValueTextView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "inside onClick");
            ExpenseGroupListItem currentItem = data.get(getAdapterPosition());
            Toast.makeText(context, currentItem.flatName, Toast.LENGTH_SHORT).show();
            openDistributionDialog();
        }

        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(context, "To be implemented", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void openDistributionDialog() {
        DialogFragment expenseDistributionDialog = new android.support.v4.app.DialogFragment() {
            private ProgressDialog progressDialog;
            private TextView expenseDistributionTextView;

            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_expense_distribution, null);
                expenseDistributionTextView = (TextView) view.findViewById(R.id.expenseDistributionTextView);

                LocalExpenseGroup localExpenseGroup = appData.getLocalExpenseGroup(prefs.getLong(AppConstants.FLAT_EXPENSE_GROUP_ID, 0));
                Map<Long, Long> membersData = localExpenseGroup.getMembersData();
                Set<Long> memberIds = localExpenseGroup.getMembersData().keySet();
                String finalData = "";
                for (Long memberId : memberIds) {
                    String name = appData.getLocalUserProfile(memberId).getUserName();
                    Long share = membersData.get(memberId);
                    if (share > 0) {
                        finalData = finalData + "\n" + name + " will pay Rs. " + share.toString() + " to others";
                    } else if (share < 0) {
                        finalData = finalData + "\n" + name + " will get Rs. " + share.toString() + " from others";
                    } else {
                        finalData = finalData + "\n" + name + " will not pay to anyone";
                    }
                }
                expenseDistributionTextView.setText(finalData);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Expense Distribution");
                alertDialogBuilder.setView(view);
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Cancel: onClick");
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Cancel: onClick");
                        dialog.dismiss();
                    }
                });
                return alertDialogBuilder.create();
            }
        };
        expenseDistributionDialog.show(manager, "Fragment");
    }
}
