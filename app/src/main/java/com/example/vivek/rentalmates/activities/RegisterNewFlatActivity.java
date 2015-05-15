package com.example.vivek.rentalmates.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;
import com.example.vivek.rentalmates.interfaces.OnExpenseGroupListReceiver;
import com.example.vivek.rentalmates.interfaces.OnFlatInfoListReceiver;
import com.example.vivek.rentalmates.interfaces.OnRegisterNewFlatReceiver;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.others.AppData;
import com.example.vivek.rentalmates.tasks.GetExpenseGroupListAsyncTask;
import com.example.vivek.rentalmates.tasks.GetFlatInfoListAsyncTask;
import com.example.vivek.rentalmates.tasks.RegisterNewFlatAsyncTask;

import java.util.List;

public class RegisterNewFlatActivity extends ActionBarActivity {

    private static final String TAG = "RegisterFlat_Debug";

    boolean registerButtonClicked;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private EditText editText1;
    private Button button1;
    private Button registerButton;
    private SharedPreferences prefs;
    private Context context;
    private AppData appData;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_flat);
        registerButtonClicked = false;
        textView1 = (TextView) findViewById(R.id.textView5);
        textView2 = (TextView) findViewById(R.id.textView6);
        textView3 = (TextView) findViewById(R.id.textView4);
        editText1 = (EditText) findViewById(R.id.editText2);
        button1 = (Button) findViewById(R.id.button2);
        registerButton = (Button) findViewById(R.id.button3);
        updateView(false);//need to be changed to true later
        prefs = this.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        context = getApplicationContext();
        appData = AppData.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
    }

    void updateView(boolean isFlatAlreadyRegistered) {
        if (isFlatAlreadyRegistered) {
            textView1.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
            editText1.setVisibility(View.GONE);
            button1.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);
            registerButton.setText("NEXT");
        } else {
            textView1.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            textView3.setVisibility(View.VISIBLE);
            editText1.setVisibility(View.VISIBLE);
            button1.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
        }
    }

    public void onRegisterFlatButtonClick(View view) {
        if (registerButtonClicked || !verifyFlatInfoData()) {
            return;
        }
        registerButtonClicked = true;
        FlatInfo flatInfo = new FlatInfo();
        flatInfo.setFlatName(editText1.getText().toString());
        flatInfo.setOwnerEmailId(prefs.getString(AppConstants.EMAIL_ID, "no_email_id"));
        flatInfo.setUserProfileId(prefs.getLong(AppConstants.USER_PROFILE_ID, 0));
        RegisterNewFlatAsyncTask task = new RegisterNewFlatAsyncTask(this, flatInfo);
        task.setOnRegisterNewFlatReceiver(new OnRegisterNewFlatReceiver() {
            @Override
            public void onRegisterNewFlatSuccessful(FlatInfo flatInfo) {
                registerButtonClicked = false;
                progressDialog.cancel();
                if (flatInfo == null) {
                    Toast.makeText(getApplicationContext(), "Flat with given name already registered. \n Please enter different name", Toast.LENGTH_LONG).show();
                    return;
                }
                Log.d(TAG, "FlatInfo uploaded");

                getCompleteUserInformation();
            }

            @Override
            public void onRegisterNewFlatFailed() {
                registerButtonClicked = false;
                progressDialog.cancel();
            }
        });
        task.execute();
        progressDialog.setMessage("Registering new flat");
        progressDialog.show();
    }

    public boolean verifyFlatInfoData() {
        if (editText1.getText().toString().trim().matches("")) {
            Toast.makeText(this, "No Flat Name entered", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
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
                    public void onExpenseGroupListLoadSuccessful() {
                        progressDialog.cancel();
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
}
