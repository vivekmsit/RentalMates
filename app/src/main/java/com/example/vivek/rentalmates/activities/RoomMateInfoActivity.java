package com.example.vivek.rentalmates.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.data.LocalFlatSearchCriteria;

public class RoomMateInfoActivity extends AppCompatActivity {

    TextView roomMateName;
    TextView rentRange;
    TextView securityRange;
    TextView numberOfPersons;
    TextView location;
    LocalFlatSearchCriteria localFlatSearchCriteria;
    AppData appData;
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_mate_info);

        appData = AppData.getInstance();

        //Initialize Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //Initialize message FAB
        FloatingActionButton messageFab = (FloatingActionButton) findViewById(R.id.messageFab);
        messageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Initialize call FAB
        FloatingActionButton callFab = (FloatingActionButton) findViewById(R.id.callFab);
        callFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Initialize CollapsingToolbarLayout
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("RoomMate Information");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        //Get selected flatId from Intent
        Intent intent = getIntent();
        Long flatSearchCriteriaId = intent.getLongExtra("FLAT_SEARCH_CRITERIA_ID", 0);
        Long flatId = intent.getLongExtra("FLAT_ID", 0);
        for (LocalFlatSearchCriteria criteria : appData.getRoomMateList(flatId)) {
            if (criteria.getId().equals(flatSearchCriteriaId)) {
                localFlatSearchCriteria = criteria;
            }
        }

        roomMateName = (TextView) findViewById(R.id.nameTextView);
        rentRange = (TextView) findViewById(R.id.rentRangeTextView);
        securityRange = (TextView) findViewById(R.id.securityRangeTextView);
        numberOfPersons = (TextView) findViewById(R.id.numberOfPersonsTextView);
        location = (TextView) findViewById(R.id.roomLocationTextView);

        roomMateName.setText(localFlatSearchCriteria.getRequesterName());
        rentRange.setText("Rs. " + localFlatSearchCriteria.getMinRentAmountPerPerson() + " to " + localFlatSearchCriteria.getMaxRentAmountPerPerson());
        securityRange.setText("Rs. " + localFlatSearchCriteria.getMinSecurityAmountPerPerson() + " to " + localFlatSearchCriteria.getMaxSecurityAmountPerPerson());
        numberOfPersons.setText(" 1 Person");
        location.setText(localFlatSearchCriteria.getSelectedLocation());
    }
}
