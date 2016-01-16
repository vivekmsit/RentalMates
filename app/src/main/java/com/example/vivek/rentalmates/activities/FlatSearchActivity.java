package com.example.vivek.rentalmates.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatSearchCriteria;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.data.LocalUserProfile;
import com.example.vivek.rentalmates.library.PlaceAutoCompleteAdapter;
import com.example.vivek.rentalmates.library.RangeSeekBar;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class FlatSearchActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {

    private AppData appData;
    private FlatSearchCriteria flatSearchCriteria;
    private Button flatSearchButton;
    private TextView minRentValueTextView;
    private TextView maxRentValueTextView;
    private TextView minSecurityValueTextView;
    private TextView maxSecurityValueTextView;
    private TextView selectedLocationTextView;
    private TextView seekBarValueTextView;
    private SeekBar searchRadiusSeekBar;
    private GoogleApiClient mGoogleApiClient;
    private MapView mapView;
    private GoogleMap map;
    private PlaceAutoCompleteAdapter mAdapter;
    private AutoCompleteTextView autocompleteView;
    private SharedPreferences prefs;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flat_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appData = AppData.getInstance();
        prefs = getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        flatSearchCriteria = new FlatSearchCriteria();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .build();

        seekBarValueTextView = (TextView) findViewById(R.id.seekBarValueTextView);

        //Initialize searchRadiusSeekBar
        searchRadiusSeekBar = (SeekBar) findViewById(R.id.searchRadiusSeekBar);
        searchRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                flatSearchCriteria.setAreaRange(progress * 1000);
                seekBarValueTextView.setText("Radius: " + progress + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutoCompleteAdapter(this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY, null);

        // Initialize AutoCompleteView
        autocompleteView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        autocompleteView.setAdapter(mAdapter);
        // Register a listener that receives callbacks when a suggestion has been selected
        autocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        minRentValueTextView = (TextView) findViewById(R.id.minRentValueTextView);
        maxRentValueTextView = (TextView) findViewById(R.id.maxRentValueTextView);
        minSecurityValueTextView = (TextView) findViewById(R.id.minSecurityValueTextView);
        maxSecurityValueTextView = (TextView) findViewById(R.id.maxSecurityValueTextView);
        selectedLocationTextView = (TextView) findViewById(R.id.selectedLocationTextView);

        final FlatSearchCriteria flatSearchCriteriaSaved = appData.getFlatSearchCriteria();
        int minRent = flatSearchCriteriaSaved.getMinRentAmountPerPerson();
        int maxRent = flatSearchCriteriaSaved.getMaxRentAmountPerPerson();
        int minSecurity = flatSearchCriteriaSaved.getMinSecurityAmountPerPerson();
        int maxSecurity = flatSearchCriteriaSaved.getMaxSecurityAmountPerPerson();
        double locationLatitude = flatSearchCriteriaSaved.getLocationLatitude();
        double locationLongitude = flatSearchCriteriaSaved.getLocationLongitude();
        int areaRange = flatSearchCriteriaSaved.getAreaRange();
        String selectedLocation = flatSearchCriteriaSaved.getSelectedLocation();

        minRentValueTextView.setText("Rs " + minRent);
        maxRentValueTextView.setText("Rs " + maxRent);
        minSecurityValueTextView.setText("Rs " + minSecurity);
        maxSecurityValueTextView.setText("Rs " + maxSecurity);
        selectedLocationTextView.setText(selectedLocation);
        if (areaRange != 0) {
            searchRadiusSeekBar.setProgress(areaRange / 1000);
        } else {
            searchRadiusSeekBar.setProgress(0);
        }
        setUpRentRangeSeekBar();
        setUpSecurityRangeSeekBar();

        Long profileId = prefs.getLong(AppConstants.USER_PROFILE_ID, 0);
        LocalUserProfile userProfile = appData.getLocalUserProfile(profileId);

        flatSearchCriteria.setLocationLatitude(locationLatitude);
        flatSearchCriteria.setLocationLongitude(locationLongitude);
        flatSearchCriteria.setAreaRange(areaRange);
        flatSearchCriteria.setMinRentAmountPerPerson(minRent);
        flatSearchCriteria.setMaxRentAmountPerPerson(maxRent);
        flatSearchCriteria.setMinSecurityAmountPerPerson(minSecurity);
        flatSearchCriteria.setMaxSecurityAmountPerPerson(maxSecurity);
        flatSearchCriteria.setSelectedLocation(selectedLocation);
        flatSearchCriteria.setRequesterId(userProfile.getUserProfileId());
        flatSearchCriteria.setRequesterName(userProfile.getUserName());
        flatSearchCriteria.setRequesterProfilePicture(userProfile.getProfilePhotoURL());

        flatSearchButton = (Button) findViewById(R.id.flatSearchButton);
        flatSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flatSearchCriteria.setFilterResetDone(false);
                appData.storeFlatSearchCriteria(getApplicationContext(), flatSearchCriteria);
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(flatSearchCriteria.getLocationLatitude(), flatSearchCriteria.getLocationLongitude()), 17.23f));
    }


    private void setUpRentRangeSeekBar() {
        RangeSeekBar<Integer> seekBar = new RangeSeekBar<>(0, 100000, this);
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
        ViewGroup layout = (ViewGroup) findViewById(R.id.rentRangeSeekBarInnerLayout);
        layout.addView(seekBar);
    }

    private void setUpSecurityRangeSeekBar() {
        RangeSeekBar<Integer> seekBar = new RangeSeekBar<>(0, 300000, this);
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
        ViewGroup layout = (ViewGroup) findViewById(R.id.securityRangeSeekBarInnerLayout);
        layout.addView(seekBar);
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

    private void makeUseOfNewLocation() {
        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(flatSearchCriteria.getLocationLatitude(), flatSearchCriteria.getLocationLongitude()), (float) 17.23);
        map.animateCamera(cameraUpdate);
    }


    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            autocompleteView.setText("");
            autocompleteView.dismissDropDown();
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                //Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            // Updates the latitude and longitude of flatSearchCriteria
            selectedLocationTextView.setText(place.getAddress());
            flatSearchCriteria.setLocationLatitude(place.getLatLng().latitude);
            flatSearchCriteria.setLocationLongitude(place.getLatLng().longitude);
            flatSearchCriteria.setSelectedLocation(place.getAddress().toString());
            places.release();
            // Check if no view has focus:
            View currentView = getCurrentFocus();
            if (currentView != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
            }
            makeUseOfNewLocation();
        }
    };

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Suspended", Toast.LENGTH_SHORT).show();
    }
}
