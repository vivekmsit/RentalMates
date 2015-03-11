package com.example.vivek.rentalmates.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.AddExpenseActivity;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class MainFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    Button addExpenseFragment;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addExpenseFragment = (Button) getView().findViewById(R.id.addExpenseButton);
        addExpenseFragment.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.addExpenseButton:
                Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
                getActivity().startActivity(intent);
                break;

            default:
                break;
        }
    }
}
