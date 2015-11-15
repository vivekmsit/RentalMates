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
import android.widget.TextView;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatSearchCriteria;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.library.RangeSeekBar;

public class FlatSearchCriteriaDialog extends DialogFragment {
    private OnDialogResultListener listener;
    private Context context;
    private AppData appData;
    private FlatSearchCriteria flatSearchCriteria;
    private TextView minRentValueTextView;
    private TextView maxRentValueTextView;
    private TextView minSecurityValueTextView;
    private TextView maxSecurityValueTextView;

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
        appData = AppData.getInstance();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_flat_search_criteria, null);
        minRentValueTextView = (TextView) view.findViewById(R.id.minRentValueTextView);
        maxRentValueTextView = (TextView) view.findViewById(R.id.maxRentValueTextView);
        minSecurityValueTextView = (TextView) view.findViewById(R.id.minSecurityValueTextView);
        maxSecurityValueTextView = (TextView) view.findViewById(R.id.maxSecurityValueTextView);

        FlatSearchCriteria flatSearchCriteriaSaved = appData.getFlatSearchCriteria();
        int minRent = flatSearchCriteriaSaved.getMinRentAmountPerPerson();
        int maxRent = flatSearchCriteriaSaved.getMaxRentAmountPerPerson();
        int minSecurity = flatSearchCriteriaSaved.getMinSecurityAmountPerPerson();
        int maxSecurity = flatSearchCriteriaSaved.getMaxSecurityAmountPerPerson();
        double locationLatitude = flatSearchCriteriaSaved.getLocationLatitude();
        double locationLongitude = flatSearchCriteriaSaved.getLocationLongitude();
        int areaRange = flatSearchCriteriaSaved.getAreaRange();

        minRentValueTextView.setText("Rs " + minRent);
        maxRentValueTextView.setText("Rs " + maxRent);
        minSecurityValueTextView.setText("Rs " + minSecurity);
        maxSecurityValueTextView.setText("Rs " + maxSecurity);
        setUpRentRangeSeekBar(view);
        setUpSecurityRangeSeekBar(view);

        flatSearchCriteria = new FlatSearchCriteria();

        flatSearchCriteria.setLocationLatitude(locationLatitude);
        flatSearchCriteria.setLocationLongitude(locationLongitude);
        flatSearchCriteria.setAreaRange(areaRange);
        flatSearchCriteria.setMinRentAmountPerPerson(minRent);
        flatSearchCriteria.setMaxRentAmountPerPerson(maxRent);
        flatSearchCriteria.setMinSecurityAmountPerPerson(minSecurity);
        flatSearchCriteria.setMaxSecurityAmountPerPerson(maxSecurity);

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

    private void setUpRentRangeSeekBar(View view) {
        RangeSeekBar<Integer> seekBar = new RangeSeekBar<>(0, 100000, context);
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                flatSearchCriteria.setMaxRentAmountPerPerson(maxValue);
                flatSearchCriteria.setMinRentAmountPerPerson(minValue);
                minRentValueTextView.setText("Rs " + minValue);
                maxRentValueTextView.setText("Rs " + maxValue);
            }
        });
        seekBar.setSelectedMinValue(appData.getFlatSearchCriteria().getMinRentAmountPerPerson());
        seekBar.setSelectedMaxValue(appData.getFlatSearchCriteria().getMaxRentAmountPerPerson());
        ViewGroup layout = (ViewGroup) view.findViewById(R.id.rentRangeSeekBarInnerLayout);
        layout.addView(seekBar);
    }

    private void setUpSecurityRangeSeekBar(View view) {
        RangeSeekBar<Integer> seekBar = new RangeSeekBar<>(0, 300000, context);
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                flatSearchCriteria.setMaxSecurityAmountPerPerson(maxValue);
                flatSearchCriteria.setMinSecurityAmountPerPerson(minValue);
                minSecurityValueTextView.setText("Rs " + minValue);
                maxSecurityValueTextView.setText("Rs " + maxValue);
            }
        });
        seekBar.setSelectedMinValue(appData.getFlatSearchCriteria().getMinSecurityAmountPerPerson());
        seekBar.setSelectedMaxValue(appData.getFlatSearchCriteria().getMaxSecurityAmountPerPerson());
        ViewGroup layout = (ViewGroup) view.findViewById(R.id.securityRangeSeekBarInnerLayout);
        layout.addView(seekBar);
    }

    public boolean verifyInputData() {
        return true;
    }
}