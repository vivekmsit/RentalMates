package com.example.vivek.rentalmates.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;
import com.example.vivek.rentalmates.backend.mainApi.model.Request;
import com.example.vivek.rentalmates.backend.userProfileApi.model.ExpenseGroup;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfile;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.data.LocalFlatInfo;
import com.example.vivek.rentalmates.dialogs.GetExistingFlatInfoDialog;
import com.example.vivek.rentalmates.interfaces.OnExpenseGroupListReceiver;
import com.example.vivek.rentalmates.interfaces.OnExpenseListReceiver;
import com.example.vivek.rentalmates.interfaces.OnFlatInfoListReceiver;
import com.example.vivek.rentalmates.interfaces.OnRequestJoinExistingEntityReceiver;
import com.example.vivek.rentalmates.interfaces.OnUserProfileListReceiver;
import com.example.vivek.rentalmates.services.BackendApiService;
import com.example.vivek.rentalmates.tasks.GetAllExpenseListAsyncTask;
import com.example.vivek.rentalmates.tasks.GetExpenseGroupListAsyncTask;
import com.example.vivek.rentalmates.tasks.GetFlatInfoListAsyncTask;
import com.example.vivek.rentalmates.tasks.GetUserProfileListAsyncTask;
import com.example.vivek.rentalmates.tasks.RequestAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class DetermineFlatActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "DetermineFlat_Debug";
    private static final int REGISTER_NEW_FLAT = 0;

    private TextView alreadyTextView;
    private Spinner chooseFlatSpinner;
    private Button continueWithOldFlatButton;
    private Button requestRegisterWithOtherFlatButton;
    private Button registerNewFlatButton;
    private Button skipButton;

    private Context context;
    private AppData appData;
    private boolean registerWithAlreadyRegisteredFlatButtonClicked;
    private boolean alreadyRegisteredFlat;
    private List<LocalFlatInfo> localFlats = new ArrayList<>();
    private Long selectedFlatId;
    private String selectedFlatName;
    private Long selectedGroupExpenseId;
    private ProgressDialog progressDialog;
    private SharedPreferences prefs;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_determine_flat);

        context = getApplicationContext();
        appData = AppData.getInstance();
        fragmentManager = getSupportFragmentManager();
        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);

        for (LocalFlatInfo localFlatInfo : appData.getFlats().values()) {
            localFlats.add(localFlatInfo);
        }
        alreadyTextView = (TextView) findViewById(R.id.alreadyTextView);
        chooseFlatSpinner = (Spinner) findViewById(R.id.chooseFlatSpinner);
        continueWithOldFlatButton = (Button) findViewById(R.id.continueWithOldFlatButton);
        requestRegisterWithOtherFlatButton = (Button) findViewById(R.id.requestRegisterWithOtherFlatButton);
        registerNewFlatButton = (Button) findViewById(R.id.registerNewFlatButton);
        skipButton = (Button) findViewById(R.id.skipButton);

        continueWithOldFlatButton.setOnClickListener(this);
        requestRegisterWithOtherFlatButton.setOnClickListener(this);
        registerNewFlatButton.setOnClickListener(this);
        skipButton.setOnClickListener(this);

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

        registerWithAlreadyRegisteredFlatButtonClicked = false;

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
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
                BackendApiService.storePrimaryFlatId(this, selectedFlatId);
                BackendApiService.storePrimaryFlatName(this, selectedFlatName);
                BackendApiService.storeFlatExpenseGroupId(this, selectedGroupExpenseId);
                loadAllExpenseGroups();
                break;

            case R.id.requestRegisterWithOtherFlatButton:
                if (registerWithAlreadyRegisteredFlatButtonClicked) {
                    return;
                }
                registerWithAlreadyRegisteredFlatButtonClicked = true;
                joinExistingFlat();
                break;

            case R.id.registerNewFlatButton:
                registerNewFlat();
                break;

            case R.id.skipButton:
                Intent intent = new Intent(getApplicationContext(), MainTabActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getApplicationContext().startActivity(intent);
                break;

            default:
                break;
        }
    }

    public void joinExistingFlat() {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(DetermineFlatActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        GetExistingFlatInfoDialog dialog = new GetExistingFlatInfoDialog();
        dialog.setOnDialogResultListener(new GetExistingFlatInfoDialog.OnDialogResultListener() {
            @Override
            public void onPositiveResult(final String flatName, String ownerEmailId) {
                RequestAsyncTask task = new RequestAsyncTask(context, "FlatInfo", flatName, ownerEmailId);
                task.setOnRequestJoinExistingEntityReceiver(new OnRequestJoinExistingEntityReceiver() {
                    @Override
                    public void onRequestJoinExistingEntitySuccessful(Request request) {
                        progressDialog.cancel();
                        switch (request.getStatus()) {
                            case "PENDING":
                                Toast.makeText(context, "Request sent to owner of the Flat", Toast.LENGTH_LONG).show();
                                break;
                            case "ENTITY_NOT_AVAILABLE":
                                Toast.makeText(context, "Flat with given name doesn't exist.\nPlease enter different name", Toast.LENGTH_LONG).show();
                                break;
                            case "ALREADY_MEMBER":
                                Toast.makeText(context, "You are already member of " + flatName, Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(context, "Failed request due to Unknown Reason", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }

                    @Override
                    public void onRequestJoinExistingEntityFailed() {
                        progressDialog.cancel();
                    }
                });
                task.execute();
                progressDialog.setMessage("Requesting for Register with flat " + flatName);
                progressDialog.show();
            }

            @Override
            public void onNegativeResult() {

            }
        });
        dialog.show(fragmentManager, "Fragment");
    }

    public void registerNewFlat() {
        Intent intent = new Intent(this, NewFlatActivity.class);
        startActivityForResult(intent, REGISTER_NEW_FLAT);
    }

    public void getCompleteUserInformation() {
        //Download FlatInfo List
        GetFlatInfoListAsyncTask flatTask = new GetFlatInfoListAsyncTask(context);
        flatTask.setOnFlatInfoListReceiver(new OnFlatInfoListReceiver() {
            @Override
            public void onFlatInfoListLoadSuccessful(List<com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfo> flats) {
                progressDialog.cancel();
                if (flats == null) {
                    Toast.makeText(context, "No flat registered for given user", Toast.LENGTH_LONG).show();
                    return;
                }
                appData.storeFlatInfoList(context, flats);

                //Download ExpenseGroup List
                GetExpenseGroupListAsyncTask expenseGroupTask = new GetExpenseGroupListAsyncTask(context);
                expenseGroupTask.setOnExpenseGroupListReceiver(new OnExpenseGroupListReceiver() {
                    @Override
                    public void onExpenseGroupListLoadSuccessful(List<ExpenseGroup> expenseGroups) {
                        progressDialog.cancel();
                        appData.storeExpenseGroupList(context, expenseGroups);
                        Intent intent = new Intent(getApplicationContext(), MainTabActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        getApplicationContext().startActivity(intent);
                    }

                    @Override
                    public void onExpenseGroupListLoadFailed() {
                        progressDialog.cancel();
                    }
                });
                expenseGroupTask.execute();
                progressDialog.setMessage("Loading Expense Groups");
                progressDialog.show();
            }

            @Override
            public void onFlatInfoListLoadFailed() {
                progressDialog.cancel();
            }
        });
        flatTask.execute();
        progressDialog.setMessage("Loading Flat Information");
        progressDialog.show();
    }

    public void loadAllExpenseGroups() {
        //Download ExpenseGroup List
        GetExpenseGroupListAsyncTask expenseGroupTask = new GetExpenseGroupListAsyncTask(context);
        expenseGroupTask.setOnExpenseGroupListReceiver(new OnExpenseGroupListReceiver() {
            @Override
            public void onExpenseGroupListLoadSuccessful(List<ExpenseGroup> expenseGroups) {
                appData.storeExpenseGroupList(context, expenseGroups);
                progressDialog.cancel();
                loadAllUserProfiles();
            }

            @Override
            public void onExpenseGroupListLoadFailed() {
                progressDialog.cancel();
            }
        });
        expenseGroupTask.execute();
        progressDialog.setMessage("Loading Expense Groups");
        progressDialog.show();
    }

    public void loadAllUserProfiles() {
        //Download new user profiles related data
        GetUserProfileListAsyncTask task = new GetUserProfileListAsyncTask(context);
        task.setOnUserProfileListReceiver(new OnUserProfileListReceiver() {
            @Override
            public void onUserProfileListLoadSuccessful(List<UserProfile> userProfiles) {
                progressDialog.cancel();
                if (userProfiles == null) {
                    Toast.makeText(context, "No user profiles available", Toast.LENGTH_LONG).show();
                    return;
                }
                appData.updateProfilePictures(context, userProfiles);
                loadAllExpenses();
            }

            @Override
            public void onUserProfileListLoadFailed() {
                progressDialog.cancel();
            }
        });
        task.execute();
        progressDialog.setMessage("Loading User Profiles");
        progressDialog.show();
    }

    public void loadAllExpenses() {
        GetAllExpenseListAsyncTask task = new GetAllExpenseListAsyncTask(getApplicationContext());
        task.setOnExpenseListReceiver(new OnExpenseListReceiver() {
            @Override
            public void onExpenseDataListLoadSuccessful(List<ExpenseData> expenses) {
                progressDialog.cancel();
                Intent intent = new Intent(getApplicationContext(), MainTabActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getApplicationContext().startActivity(intent);
            }

            @Override
            public void onExpenseDataListLoadFailed() {
                progressDialog.cancel();
            }
        });
        task.execute();
        progressDialog.setMessage("Loading Expense List");
        progressDialog.show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTER_NEW_FLAT) {
            if (resultCode == Activity.RESULT_OK) {
                //getCompleteUserInformation();
                Intent intent = new Intent(getApplicationContext(), MainTabActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getApplicationContext().startActivity(intent);
            }
        }
    }
}
