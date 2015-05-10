package com.example.vivek.rentalmates.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfile;
import com.example.vivek.rentalmates.interfaces.OnExpenseGroupListReceiver;
import com.example.vivek.rentalmates.interfaces.OnExpenseListReceiver;
import com.example.vivek.rentalmates.interfaces.OnRegisterWithOldFlatReceiver;
import com.example.vivek.rentalmates.interfaces.OnUserProfileListReceiver;
import com.example.vivek.rentalmates.others.AppData;
import com.example.vivek.rentalmates.others.LocalFlatInfo;
import com.example.vivek.rentalmates.services.BackendApiService;
import com.example.vivek.rentalmates.tasks.GetAllExpenseListAsyncTask;
import com.example.vivek.rentalmates.tasks.GetExpenseGroupListAsyncTask;
import com.example.vivek.rentalmates.tasks.GetUserProfileListAsyncTask;
import com.example.vivek.rentalmates.tasks.RegisterWithOldFlatAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class DetermineFlatActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "DetermineFlat_Debug";

    private TextView alreadyTextView;
    private TextView newRegisteredTextView;
    private TextView registerNewFlatTextView;
    private Spinner chooseFlatSpinner;
    private EditText alreadyRegisteredFlatEditText;
    private Button continueWithOldFlatButton;
    private Button registerWithOldFlatButton;
    private Button registerNewFlatButton;
    private Context context;
    private AppData appData;
    private boolean registerWithOldFlatButtonClicked;
    private boolean alreadyRegisteredFlat;
    private List<LocalFlatInfo> localFlats = new ArrayList<>();
    private Long selectedFlatId;
    private String selectedFlatName;
    private Long selectedGroupExpenseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_determine_flat);

        context = getApplicationContext();
        appData = AppData.getInstance();
        localFlats = appData.getFlats();
        alreadyTextView = (TextView) findViewById(R.id.alreadyTextView);
        newRegisteredTextView = (TextView) findViewById(R.id.newRegisteredTextView);
        registerNewFlatTextView = (TextView) findViewById(R.id.registerNewFlatTextView);
        alreadyRegisteredFlatEditText = (EditText) findViewById(R.id.alreadyRegisteredFlatEditText);
        chooseFlatSpinner = (Spinner) findViewById(R.id.chooseFlatSpinner);
        continueWithOldFlatButton = (Button) findViewById(R.id.continueWithOldFlatButton);
        registerWithOldFlatButton = (Button) findViewById(R.id.registerWithOldFlatButton);
        registerNewFlatButton = (Button) findViewById(R.id.registerNewFlatButton);

        continueWithOldFlatButton.setOnClickListener(this);
        registerWithOldFlatButton.setOnClickListener(this);
        registerNewFlatButton.setOnClickListener(this);

        Intent intent = getIntent();
        alreadyRegisteredFlat = intent.getBooleanExtra("FLAT_REGISTERED", false);
        if (alreadyRegisteredFlat) {
            LocalFlatInfo flat = localFlats.get(0);
            selectedFlatId = flat.getFlatId();
            selectedFlatName = flat.getFlatName();
            selectedGroupExpenseId = flat.getFlatExpenseGroupId();
        }

        if (!alreadyRegisteredFlat) {
            alreadyTextView.setVisibility(View.INVISIBLE);
            chooseFlatSpinner.setVisibility(View.INVISIBLE);
            continueWithOldFlatButton.setVisibility(View.INVISIBLE);
        }

        List<String> flatNames = new ArrayList<>();
        for (LocalFlatInfo flat : localFlats) {
            flatNames.add(flat.getFlatName());
        }
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, flatNames);
        chooseFlatSpinner.setAdapter(stringArrayAdapter);
        chooseFlatSpinner.setOnItemSelectedListener(this);

        registerWithOldFlatButtonClicked = false;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "inside onClick");

        switch (v.getId()) {

            case R.id.continueWithOldFlatButton:
                if (!alreadyRegisteredFlat) {
                    Toast.makeText(this, "No Flat registered yet", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(this, "retrieving ExpenseData list", Toast.LENGTH_SHORT).show();
                BackendApiService.storePrimaryFlatId(this, selectedFlatId);
                BackendApiService.storePrimaryFlatName(this, selectedFlatName);
                BackendApiService.storeFlatExpenseGroupId(this, selectedGroupExpenseId);
                loadAllExpenseGroups();
                break;

            case R.id.registerWithOldFlatButton:
                if (registerWithOldFlatButtonClicked || !verifyFlatInfoData()) {
                    return;
                }
                registerWithOldFlatButtonClicked = true;
                Toast.makeText(this, "registering with old flat", Toast.LENGTH_LONG).show();
                RegisterWithOldFlatAsyncTask task = new RegisterWithOldFlatAsyncTask(this, alreadyRegisteredFlatEditText.getText().toString());
                task.setOnRegisterWithOldFlatReceiver(new OnRegisterWithOldFlatReceiver() {
                    @Override
                    public void onRegisterWithOldFlatSuccessful(String message, FlatInfo flatInfo) {
                        registerWithOldFlatButtonClicked = false;
                        if (message.equals("SUCCESS_FLAT_AVAILABLE")) {
                            Toast.makeText(getApplicationContext(), "Registered with old flat: " + flatInfo.getFlatName() + "\nretrieving ExpenseData info", Toast.LENGTH_SHORT).show();
                            BackendApiService.storePrimaryFlatId(getApplicationContext(), flatInfo.getFlatId());
                            BackendApiService.storePrimaryFlatName(getApplicationContext(), flatInfo.getFlatName());
                            BackendApiService.storeFlatExpenseGroupId(getApplicationContext(), flatInfo.getExpenseGroupId());
                            loadAllExpenseGroups();
                        } else {
                            Toast.makeText(getApplicationContext(), "Flat with given name doesn't exist.\nPlease enter different name", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onRegisterWithOldFlatFailed() {
                        registerWithOldFlatButtonClicked = false;
                    }
                });
                task.execute();
                break;

            case R.id.registerNewFlatButton:
                Intent intent = new Intent(this, RegisterNewFlatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                this.startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void loadAllExpenseGroups() {
        //Download ExpenseGroup List
        GetExpenseGroupListAsyncTask expenseGroupTask = new GetExpenseGroupListAsyncTask(context);
        expenseGroupTask.setOnExpenseGroupListReceiver(new OnExpenseGroupListReceiver() {
            @Override
            public void onExpenseGroupListLoadSuccessful() {
                loadAllUserProfiles();
            }

            @Override
            public void onExpenseGroupListLoadFailed() {
            }
        });
        expenseGroupTask.execute();
    }

    public void loadAllUserProfiles() {
        //Download new user profiles related data
        GetUserProfileListAsyncTask task = new GetUserProfileListAsyncTask(context);
        task.setOnUserProfileListReceiver(new OnUserProfileListReceiver() {
            @Override
            public void onUserProfileListLoadSuccessful(List<UserProfile> userProfiles) {
                if (userProfiles == null) {
                    Toast.makeText(context, "No user profiles available", Toast.LENGTH_LONG);
                    return;
                }
                Toast.makeText(context, "UserProfile List retrieved successfully/Number of Users: " + userProfiles.size(), Toast.LENGTH_SHORT).show();
                appData.updateProfilePictures(context, userProfiles);
                loadAllExpenses();
            }

            @Override
            public void onUserProfileListLoadFailed() {
            }
        });
        task.execute();
    }

    public void loadAllExpenses() {
        GetAllExpenseListAsyncTask task = new GetAllExpenseListAsyncTask(getApplicationContext());
        task.setOnExpenseListReceiver(new OnExpenseListReceiver() {
            @Override
            public void onExpenseDataListLoadSuccessful() {
                Toast.makeText(getApplicationContext(), "ExpenseData retrieved successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainTabActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getApplicationContext().startActivity(intent);
            }

            @Override
            public void onExpenseDataListLoadFailed() {
            }
        });
        task.execute();
    }

    public boolean verifyFlatInfoData() {
        if (alreadyRegisteredFlatEditText.getText().toString().trim().matches("")) {
            Toast.makeText(this, "No Flat Name entered", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LocalFlatInfo flatInfo = localFlats.get(position);
        selectedFlatId = flatInfo.getFlatId();
        selectedFlatName = flatInfo.getFlatName();
        selectedGroupExpenseId = flatInfo.getFlatExpenseGroupId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
