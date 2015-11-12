package com.example.vivek.rentalmates.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatSearchCriteria;

public class FlatSearchCriteriaDialog extends DialogFragment {
    private OnDialogResultListener listener;
    private Context context;

    public interface OnDialogResultListener {
        void onPositiveResult(FlatSearchCriteria flatSearchCriteria);

        void onNegativeResult();
    }

    public void setOnDialogResultListener(OnDialogResultListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_flat_search_criteria, null);

        final FlatSearchCriteria flatSearchCriteria = new FlatSearchCriteria();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Search Criteria");
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (verifyInputData()) {
                    if (listener != null) {
                        listener.onPositiveResult(flatSearchCriteria);
                    }
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onNegativeResult();
                }
            }
        });
        return alertDialogBuilder.create();
    }

    public boolean verifyInputData() {
        return true;
    }
}