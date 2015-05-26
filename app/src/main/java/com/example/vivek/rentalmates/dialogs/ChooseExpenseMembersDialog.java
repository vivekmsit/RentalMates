package com.example.vivek.rentalmates.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.example.vivek.rentalmates.interfaces.OnExpenseMembersSelectedReceiver;
import com.example.vivek.rentalmates.others.AppData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChooseExpenseMembersDialog extends DialogFragment {

    private static final String TAG = "EMembersDialog_Debug";

    private AppData appData;
    private Long[] expenseGroupMemberIds;
    private ArrayList<Long> selectedItemsIndexList;
    private boolean[] isSelectedArray;
    private String[] memberNames;
    private OnExpenseMembersSelectedReceiver receiver;

    public ChooseExpenseMembersDialog() {
    }

    public void setOnExpenseMembersSelectedReceiver(OnExpenseMembersSelectedReceiver receiver) {
        this.receiver = receiver;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreateDialog");
        selectedItemsIndexList = new ArrayList<>();
        appData = AppData.getInstance();
        Bundle bundle = getArguments();
        Long expenseGroupId = bundle.getLong("expenseGroupId");
        int count = 0;
        Set<Long> expenseMemberIds = appData.getLocalExpenseGroup(expenseGroupId).getMembersData().keySet();
        int size = expenseMemberIds.size();

        memberNames = new String[size];
        isSelectedArray = new boolean[size];
        expenseGroupMemberIds = new Long[size];

        for (Long id : expenseMemberIds) {
            memberNames[count] = appData.getLocalUserProfile(id).getUserName();
            isSelectedArray[count] = true;
            selectedItemsIndexList.add(id);
            expenseGroupMemberIds[count] = id;
            count++;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Expense Members")
                .setMultiChoiceItems(memberNames, isSelectedArray,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                Long currentId = expenseGroupMemberIds[which];
                                if (isChecked) {
                                    selectedItemsIndexList.add(currentId);
                                } else if (selectedItemsIndexList.contains(currentId)) {
                                    selectedItemsIndexList.remove(currentId);
                                }
                            }
                        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (receiver != null) {
                    receiver.onOkay(selectedItemsIndexList);
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (receiver != null) {
                    receiver.onCancel();
                }
            }
        });
        return builder.create();
    }
}

