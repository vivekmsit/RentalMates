package com.example.vivek.rentalmates.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class FlatSearchCriteriaDialog extends DialogFragment implements GoogleApiClient.ConnectionCallbacks {
    private OnDialogResultListener listener;
    private Context context;
    private AppData appData;
    private FlatSearchCriteria flatSearchCriteria;
    private TextView minRentValueTextView;
    private TextView maxRentValueTextView;
    private TextView minSecurityValueTextView;
    private TextView maxSecurityValueTextView;
    private TextView selectedLocationTextView;
    private TextView seekBarValueTextView;
    private SeekBar searchRadiusSeekBar;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAutoCompleteAdapter mAdapter;
    private AutoCompleteTextView autocompleteView;
    private SharedPreferences prefs;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

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
        Toast.makeText(context, "Suspended", Toast.LENGTH_SHORT).show();
    }

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
        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        flatSearchCriteria = new FlatSearchCriteria();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .build();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_flat_search_criteria, null);

        seekBarValueTextView = (TextView) view.findViewById(R.id.seekBarValueTextView);

        //Initialize searchRadiusSeekBar
        searchRadiusSeekBar = (SeekBar) view.findViewById(R.id.searchRadiusSeekBar);
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
        mAdapter = new PlaceAutoCompleteAdapter(context, mGoogleApiClient, BOUNDS_GREATER_SYDNEY, null);

        // Initialize AutoCompleteView
        autocompleteView = (AutoCompleteTextView) view.findViewById(R.id.autocomplete);
        autocompleteView.setAdapter(mAdapter);
        // Register a listener that receives callbacks when a suggestion has been selected
        autocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        minRentValueTextView = (TextView) view.findViewById(R.id.minRentValueTextView);
        maxRentValueTextView = (TextView) view.findViewById(R.id.maxRentValueTextView);
        minSecurityValueTextView = (TextView) view.findViewById(R.id.minSecurityValueTextView);
        maxSecurityValueTextView = (TextView) view.findViewById(R.id.maxSecurityValueTextView);
        selectedLocationTextView = (TextView) view.findViewById(R.id.selectedLocationTextView);

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
        setUpRentRangeSeekBar(view);
        setUpSecurityRangeSeekBar(view);

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
            View currentView = getActivity().getCurrentFocus();
            if (currentView != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
            }
        }
    };
}