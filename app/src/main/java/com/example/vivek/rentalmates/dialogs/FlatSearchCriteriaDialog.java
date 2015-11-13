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
import android.view.ViewGroup;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatSearchCriteria;
import com.example.vivek.rentalmates.library.RangeSeekBar;

public class FlatSearchCriteriaDialog extends DialogFragment {
    private OnDialogResultListener listener;
    private Context context;
    FlatSearchCriteria flatSearchCriteria;

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
        setUpRangeSeekBar(view);

        flatSearchCriteria = new FlatSearchCriteria();

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

    private void setUpRangeSeekBar(View view) {
        RangeSeekBar<Integer> seekBar = new RangeSeekBar<>(0, 50, context);
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                flatSearchCriteria.setMaxRentAmountPerPerson((long) (maxValue * 1000));
                flatSearchCriteria.setMinRentAmountPerPerson((long) (minValue * 1000));
            }
        });
        // add RangeSeekBar to pre-defined layout
        ViewGroup layout = (ViewGroup) view.findViewById(R.id.rangeSeekBarLayout);
        layout.addView(seekBar);
    }

    public boolean verifyInputData() {
        return true;
    }
}