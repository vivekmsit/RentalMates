package com.example.vivek.rentalmates.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;
import com.example.vivek.rentalmates.interfaces.OnAddExpenseReceiver;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.others.AppData;
import com.example.vivek.rentalmates.others.LocalExpenseGroup;
import com.example.vivek.rentalmates.tasks.AddExpenseAsyncTask;
import com.google.api.client.util.DateTime;

import java.util.Date;

public class AddExpenseActivity extends ActionBarActivity implements View.OnClickListener {

    private static final String TAG = "AdExpenseActivity_Debug";

    private boolean addExpenseButtonClicked;
    private EditText descriptionEditText;
    private EditText amountEditText;
    private Button editUsersButton;
    private Button addExpenseButton;
    private Toolbar toolBar;
    private Context context;
    private SharedPreferences prefs;
    private AppData appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        addExpenseButtonClicked = false;

        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        amountEditText = (EditText) findViewById(R.id.amountEditText);
        editUsersButton = (Button) findViewById(R.id.editUsersButton);
        addExpenseButton = (Button) findViewById(R.id.addExpenseButton);
        toolBar = (Toolbar) findViewById(R.id.app_bar);

        editUsersButton.setOnClickListener(this);
        addExpenseButton.setOnClickListener(this);

        setSupportActionBar(toolBar);
        setTitle("New Expense");

        prefs = this.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        appData = AppData.getInstance();
        context = getApplication().getApplicationContext();
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "inside onClick");

        switch (v.getId()) {

            case R.id.editUsersButton:
                //To be implemented
                break;

            case R.id.addExpenseButton:
                if (addExpenseButtonClicked || !verifyExpenseData()) {
                    return;
                }
                addExpenseButtonClicked = true;
                ExpenseData expenseData = new ExpenseData();
                expenseData.setDate(new DateTime(new Date()));
                expenseData.setAmount(Integer.parseInt(amountEditText.getText().toString()));
                expenseData.setDescription(descriptionEditText.getText().toString());
                expenseData.setOwnerEmailId(prefs.getString(AppConstants.EMAIL_ID, "no_email_id"));
                expenseData.setUserName(prefs.getString(AppConstants.USER_NAME, "no_user_name"));
                expenseData.setExpenseGroupName(prefs.getString(AppConstants.PRIMARY_FLAT_NAME, "no_flat_name"));
                expenseData.setExpenseGroupId(prefs.getLong(AppConstants.FLAT_EXPENSE_GROUP_ID, 0));
                expenseData.setSubmitterId(prefs.getLong(AppConstants.USER_PROFILE_ID, 0));
                expenseData.setPayerId(prefs.getLong(AppConstants.USER_PROFILE_ID, 0));
                if (appData.getExpenseGroups().size() == 0) {
                    Toast.makeText(this, "No ExpenseGroups available", Toast.LENGTH_LONG).show();
                    return;
                }
                for (LocalExpenseGroup group : appData.getExpenseGroups()) {
                    if (group.getId() == prefs.getLong(AppConstants.FLAT_EXPENSE_GROUP_ID, 0)) {
                        expenseData.setMemberIds(group.getMemberIds());
                        expenseData.setNumberOfMembers(group.getNumberOfMembers());
                    }
                }
                AddExpenseAsyncTask task = new AddExpenseAsyncTask(this, expenseData);
                task.setOnAddExpenseReceiver(new OnAddExpenseReceiver() {
                    @Override
                    public void onAddExpenseSuccessful() {
                        addExpenseButtonClicked = true;
                        Toast.makeText(context, "Expense uploaded", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, MainTabActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onAddExpenseFailed() {
                        addExpenseButtonClicked = true;
                    }
                });
                task.execute();
                break;

            default:
                break;
        }
    }

    public boolean verifyExpenseData() {
        if (descriptionEditText.getText().toString().trim().matches("")) {
            Toast.makeText(this, "No description entered", Toast.LENGTH_LONG).show();
            return false;
        } else if (amountEditText.getText().toString().trim().matches("")) {
            Toast.makeText(this, "No amount entered", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}
