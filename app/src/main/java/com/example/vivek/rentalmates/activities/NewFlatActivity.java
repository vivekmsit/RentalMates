package com.example.vivek.rentalmates.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.fragments.NewFlatAmenitiesFragment;
import com.example.vivek.rentalmates.fragments.NewFlatBasicInfoFragment;

public class NewFlatActivity extends AppCompatActivity {

    NewFlatBasicInfoFragment newFlatBasicInfoFragment;
    NewFlatAmenitiesFragment newFlatAmenitiesFragment;
    int currentFragment;
    Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_flat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentFragment = 0;

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextButtonClick();
            }
        });

        newFlatBasicInfoFragment = new NewFlatBasicInfoFragment();
        newFlatAmenitiesFragment = new NewFlatAmenitiesFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentParentViewGroup, newFlatBasicInfoFragment)
                .commit();
    }

    private void onNextButtonClick() {
        if (currentFragment == 0) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentParentViewGroup, newFlatAmenitiesFragment)
                    .commit();
        } else if (currentFragment == 1) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentParentViewGroup, newFlatBasicInfoFragment)
                    .commit();
        }
    }
}
