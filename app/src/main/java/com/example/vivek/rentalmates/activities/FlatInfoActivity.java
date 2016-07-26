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
import com.example.vivek.rentalmates.data.LocalFlatInfo;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class FlatInfoActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {

    private LocalFlatInfo localFlatInfo;
    private AppData appData;
    private TextView addressTextView;
    private TextView rentAmountTextView;
    private TextView securityAmountTextView;
    private GoogleApiClient mGoogleApiClient;
    private MapView mapView;
    private GoogleMap map;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flat_info);

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
        collapsingToolbarLayout.setTitle("Flat Information");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        appData = AppData.getInstance();

        //Get selected flatId from Intent
        Intent intent = getIntent();
        Long flatId = intent.getLongExtra("FLAT_ID", 0);
        localFlatInfo = appData.getAvailableFlats().get(flatId);

        //Initialize various TextView values

        addressTextView = (TextView) findViewById(R.id.addressTextView);
        addressTextView.setText(localFlatInfo.getAddress());

        rentAmountTextView = (TextView) findViewById(R.id.rentAmountTextView);
        rentAmountTextView.setText("Rs " + localFlatInfo.getRentAmount());

        securityAmountTextView = (TextView) findViewById(R.id.securityAmountTextView);
        securityAmountTextView.setText("Rs " + localFlatInfo.getSecurityAmount());

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .build();

        //Initialize MapView
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(localFlatInfo.getLatitude(), localFlatInfo.getLongitude()), localFlatInfo.getZoom()));
            }
        });
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
