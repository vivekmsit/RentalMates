package com.example.vivek.rentalmates.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.LocalFlatInfo;
import com.example.vivek.rentalmates.fragments.NewFlatAmenitiesFragment;
import com.example.vivek.rentalmates.fragments.NewFlatBasicInfoFragment;
import com.example.vivek.rentalmates.fragments.NewFlatLocationFragment;
import com.example.vivek.rentalmates.fragments.NewFlatRentDetailsFragment;
import com.example.vivek.rentalmates.interfaces.OnRegisterNewFlatReceiver;
import com.example.vivek.rentalmates.tasks.RegisterNewFlatAsyncTask;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class NewFlatActivity extends AppCompatActivity {
    private static final String TAG = "NewFlatActivity_Debug";
    private static final int INITIAL_REQUEST = 1337;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 3;

    FragmentManager fragmentManager;
    NewFlatBasicInfoFragment newFlatBasicInfoFragment;
    NewFlatAmenitiesFragment newFlatAmenitiesFragment;
    NewFlatLocationFragment newFlatLocationFragment;
    NewFlatRentDetailsFragment newFlatRentDetailsFragment;
    int currentFragment;
    Button nextButton;
    Button fragmentNameButton;
    LocalFlatInfo newFlatInfo;
    Context context;
    SharedPreferences prefs;
    private Firebase mFlatsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_flat);

        mFlatsRef = new Firebase(AppConstants.FIREBASE_ROOT_URL).child("flats");

        //Initialize Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        context = getApplicationContext();
        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        newFlatInfo = new LocalFlatInfo();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                currentFragment = fragmentManager.getBackStackEntryCount();
                if (currentFragment == 0) {
                    nextButton.setText("Next");
                    fragmentNameButton.setText("Basic Information");
                } else if (currentFragment == 1) {
                    nextButton.setText("Next");
                    fragmentNameButton.setText("Rent Details");
                } else if (currentFragment == 2) {
                    nextButton.setText("Next");
                    fragmentNameButton.setText("Amenities");
                }
            }
        });

        currentFragment = 0;

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextButtonClick();
            }
        });

        fragmentNameButton = (Button) findViewById(R.id.fragmentNameButton);

        newFlatBasicInfoFragment = new NewFlatBasicInfoFragment();
        newFlatAmenitiesFragment = new NewFlatAmenitiesFragment();
        newFlatLocationFragment = new NewFlatLocationFragment();
        newFlatRentDetailsFragment = new NewFlatRentDetailsFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentParentViewGroup, newFlatBasicInfoFragment)
                .commit();
    }

    private void onNextButtonClick() {
        if (currentFragment == 0) {
            nextButton.setText("Next");
            fragmentNameButton.setText("Rent Details");
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentParentViewGroup, newFlatRentDetailsFragment)
                    .addToBackStack("newFlatRentDetailsFragment")
                    .commit();
            currentFragment++;
        } else if (currentFragment == 1) {
            nextButton.setText("Next");
            fragmentNameButton.setText("Amenities");
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentParentViewGroup, newFlatAmenitiesFragment)
                    .addToBackStack("newFlatAmenitiesFragment")
                    .commit();
            currentFragment++;
        } else if (currentFragment == 2) {
            nextButton.setText("Finish");
            fragmentNameButton.setText("Flat Location");
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentParentViewGroup, newFlatLocationFragment)
                    .addToBackStack("newFlatLocationFragment")
                    .commit();
            currentFragment++;
        } else if (currentFragment == 3) {
            if (newFlatBasicInfoFragment.verifyInputData() && newFlatRentDetailsFragment.verifyInputData()) {
                registerNewFlat();
            }
        }
    }

    private void registerNewFlat() {
        newFlatInfo.setOwnerEmailId(prefs.getString(AppConstants.EMAIL_ID, "no_email_id"));
        newFlatInfo.setOwnerId(prefs.getLong(AppConstants.USER_PROFILE_ID, 0));
        //newFlatInfo.setFlatAddress("Bangalore");
        newFlatInfo.setCity("Bangalore");
        newFlatInfo.setFlatName(newFlatBasicInfoFragment.getFlatName());
        newFlatInfo.setRentAmount(newFlatRentDetailsFragment.getRentAmount());
        newFlatInfo.setSecurityAmount(newFlatRentDetailsFragment.getSecurityAmount());
        newFlatInfo.setLatitude(newFlatLocationFragment.getLatitude());
        newFlatInfo.setLongitude(newFlatLocationFragment.getLongitude());
        newFlatInfo.setZoom(newFlatLocationFragment.getZoom());
        registerFlat(newFlatInfo);
    }

    private void registerFlat(LocalFlatInfo localFlatInfo) {
        Firebase mFlatRef = mFlatsRef.child(localFlatInfo.getFlatName());
        mFlatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LocalFlatInfo uploadedLocalFlatInfo = dataSnapshot.getValue(LocalFlatInfo.class);
                Toast.makeText(context, "New Flat Registered: " + uploadedLocalFlatInfo.getFlatName(), Toast.LENGTH_SHORT).show();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(context, "FireBase error: " + firebaseError.getDetails(), Toast.LENGTH_SHORT).show();
            }
        });
        mFlatRef.setValue(localFlatInfo);
    }

    private void registerFlat_Old(FlatInfo flatInfo) {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        RegisterNewFlatAsyncTask task = new RegisterNewFlatAsyncTask(this, flatInfo);
        task.setOnRegisterNewFlatReceiver(new OnRegisterNewFlatReceiver() {
            @Override
            public void onRegisterNewFlatSuccessful(FlatInfo flatInfo) {
                progressDialog.cancel();
                if (flatInfo == null) {
                    Toast.makeText(context, "Flat with given name already registered. \n Please enter different name", Toast.LENGTH_LONG).show();
                    return;
                }
                Log.d(TAG, "FlatInfo uploaded");
                Toast.makeText(context, "New Flat Registered", Toast.LENGTH_SHORT).show();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }

            @Override
            public void onRegisterNewFlatFailed() {
                progressDialog.cancel();
                Toast.makeText(context, "Failed to Register New Flat", Toast.LENGTH_SHORT).show();
            }
        });
        task.execute();
        progressDialog.setMessage("Registering new flat");
        progressDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST:
                newFlatLocationFragment.locationPermissionGranted();
        }
    }
}
