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
import com.example.vivek.rentalmates.others.LocalUserProfile;

import java.util.ArrayList;
import java.util.List;

public class ChooseExpenseMembersDialog extends DialogFragment {

    private static final String TAG = "EMembersDialog_Debug";

    private AppData appData;
    private ArrayList<Integer> selectedItemsIndexList;
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
        List<LocalUserProfile> userProfiles = appData.getUserProfiles();
        int count = 0;
        memberNames = new String[userProfiles.size()];
        isSelectedArray = new boolean[userProfiles.size()];
        for (LocalUserProfile profile : userProfiles) {
            memberNames[count] = profile.getUserName();
            isSelectedArray[count] = true;
            selectedItemsIndexList.add(count);
            count++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Expense Members")
                .setMultiChoiceItems(memberNames, isSelectedArray,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    selectedItemsIndexList.add(which);
                                } else if (selectedItemsIndexList.contains(which)) {
                                    selectedItemsIndexList.remove(Integer.valueOf(which));
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

