package com.example.vivek.rentalmates.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.vivek.rentalmates.fragments.NewFlatAmenitiesFragment;
import com.example.vivek.rentalmates.fragments.NewFlatBasicInfoFragment;
import com.example.vivek.rentalmates.fragments.NewFlatLocationFragment;
import com.example.vivek.rentalmates.interfaces.OnRegisterNewFlatReceiver;
import com.example.vivek.rentalmates.tasks.RegisterNewFlatAsyncTask;

public class NewFlatActivity extends AppCompatActivity {
    private static final String TAG = "NewFlatActivity_Debug";

    FragmentManager fragmentManager;
    NewFlatBasicInfoFragment newFlatBasicInfoFragment;
    NewFlatAmenitiesFragment newFlatAmenitiesFragment;
    NewFlatLocationFragment newFlatLocationFragment;
    int currentFragment;
    Button nextButton;
    Button fragmentNameButton;
    FlatInfo newFlatInfo;
    Context context;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_flat);

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
        newFlatInfo = new FlatInfo();

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
                    fragmentNameButton.setText("Flat Amenities");
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

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentParentViewGroup, newFlatBasicInfoFragment)
                .commit();
    }

    private void onNextButtonClick() {
        if (currentFragment == 0 && newFlatBasicInfoFragment.verifyInputData()) {
            nextButton.setText("Next");
            fragmentNameButton.setText("Flat Amenities");
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentParentViewGroup, newFlatAmenitiesFragment)
                    .addToBackStack("newFlatAmenitiesFragment")
                    .commit();
            currentFragment++;
        } else if (currentFragment == 1) {
            nextButton.setText("Finish");
            fragmentNameButton.setText("Flat Location");
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentParentViewGroup, newFlatLocationFragment)
                    .addToBackStack("newFlatLocationFragment")
                    .commit();
            currentFragment++;
        } else if (currentFragment == 2) {
            registerNewFlat();
        }
    }

    private void registerNewFlat() {
        newFlatInfo.setOwnerEmailId(prefs.getString(AppConstants.EMAIL_ID, "no_email_id"));
        newFlatInfo.setOwnerId(prefs.getLong(AppConstants.USER_PROFILE_ID, 0));
        newFlatInfo.setFlatAddress("Bangalore");
        newFlatInfo.setCity("Bangalore");
        newFlatInfo.setFlatName(newFlatBasicInfoFragment.getFlatName());
        newFlatInfo.setRentAmount(newFlatBasicInfoFragment.getRentAmount());
        newFlatInfo.setSecurityAmount(newFlatBasicInfoFragment.getSecurityAmount());
        newFlatInfo.setLatitude(newFlatLocationFragment.getLatitude());
        newFlatInfo.setLongitude(newFlatLocationFragment.getLongitude());
        newFlatInfo.setZoom(newFlatLocationFragment.getZoom());
        registerFlat(newFlatInfo);
    }

    private void registerFlat(FlatInfo flatInfo) {
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
}
