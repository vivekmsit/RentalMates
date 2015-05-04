package com.example.vivek.rentalmates.activities;

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
import com.example.vivek.rentalmates.interfaces.OnExpenseListReceiver;
import com.example.vivek.rentalmates.interfaces.OnRegisterWithOldFlatReceiver;
import com.example.vivek.rentalmates.services.BackendApiService;
import com.example.vivek.rentalmates.tasks.GetAllExpenseListAsyncTask;
import com.example.vivek.rentalmates.tasks.RegisterWithOldFlatAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class DetermineFlatActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "DetermineFlat_Debug";

    TextView alreadyTextView;
    TextView newRegisteredTextView;
    TextView registerNewFlatTextView;
    Spinner chooseFlatSpinner;
    EditText alreadyRegisteredFlatEditText;
    Button continueWithOldFlatButton;
    Button registerWithOldFlatButton;
    Button registerNewFlatButton;

    boolean registerWithOldFlatButtonClicked;
    boolean alreadyRegisteredFlat;
    List<Long> flatIds = new ArrayList<>();
    List<String> flatNames = new ArrayList<>();
    List<Long> groupExpenseIds = new ArrayList<>();
    Long selectedFlatId;
    String selectedFlatName;
    Long selectedGroupExpenseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_determine_flat);

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
            flatIds = (List<Long>) intent.getSerializableExtra("flatIds");
            flatNames = (List<String>) intent.getSerializableExtra("flatNames");
            groupExpenseIds = (List<Long>) intent.getSerializableExtra("groupExpenseIds");
            selectedFlatId = flatIds.get(0);
            selectedFlatName = flatNames.get(0);
            selectedGroupExpenseId = groupExpenseIds.get(0);
        }

        if (!alreadyRegisteredFlat) {
            alreadyTextView.setVisibility(View.INVISIBLE);
            chooseFlatSpinner.setVisibility(View.INVISIBLE);
            continueWithOldFlatButton.setVisibility(View.INVISIBLE);
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
                FlatInfo flatInfo = new FlatInfo();
                flatInfo.setFlatId(selectedFlatId);
                loadAllExpenses(flatInfo);
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
                            loadAllExpenses(flatInfo);
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

    public void loadAllExpenses(final FlatInfo flatInfo) {
        GetAllExpenseListAsyncTask task = new GetAllExpenseListAsyncTask(getApplicationContext());
        task.setOnExpenseListReceiver(new OnExpenseListReceiver() {
            @Override
            public void onExpenseDataListLoadSuccessful() {
                Toast.makeText(getApplicationContext(), "ExpenseData retrieved successfully", Toast.LENGTH_SHORT).show();
                BackendApiService.storePrimaryFlatId(getApplicationContext(), flatInfo.getFlatId());
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
        selectedFlatId = flatIds.get(position);
        selectedFlatName = flatNames.get(position);
        selectedGroupExpenseId = groupExpenseIds.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
