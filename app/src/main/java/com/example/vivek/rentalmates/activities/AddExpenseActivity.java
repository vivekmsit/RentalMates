package com.example.vivek.rentalmates.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.tasks.AddExpenseAsyncTask;

public class AddExpenseActivity extends ActionBarActivity implements View.OnClickListener {

    private static final String TAG = "AdExpenseActivity_Debug";

    boolean addExpenseButtonClicked;

    EditText descriptionEditText;
    EditText amountEditText;
    Button editUsersButton;
    Button cancelExpenseButton;
    Button addExpenseButton;

    SharedPreferences prefs;


    public void setAddExpenseButtonClicked(boolean value) {
        addExpenseButtonClicked = value;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        addExpenseButtonClicked = false;

        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        amountEditText = (EditText) findViewById(R.id.amountEditText);
        editUsersButton = (Button) findViewById(R.id.editUsersButton);
        cancelExpenseButton = (Button) findViewById(R.id.cancelExpenseButton);
        addExpenseButton = (Button) findViewById(R.id.addExpenseButton);

        editUsersButton.setOnClickListener(this);
        cancelExpenseButton.setOnClickListener(this);
        addExpenseButton.setOnClickListener(this);

        prefs = this.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "inside onClick");

        switch (v.getId()) {

            case R.id.editUsersButton:
                //To be implemented
                break;

            case R.id.cancelExpenseButton:
                Intent intent1 = new Intent(this, MainTabActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                break;

            case R.id.addExpenseButton:
                if (addExpenseButtonClicked || !verifyExpenseData()) {
                    return;
                }
                addExpenseButtonClicked = true;
                ExpenseData expenseData = new ExpenseData();
                //expenseData.setDate(new LocalDate());
                expenseData.setAmount(Integer.parseInt(amountEditText.getText().toString()));
                expenseData.setDescription(descriptionEditText.getText().toString());
                expenseData.setOwnerEmailId(prefs.getString(AppConstants.EMAIL_ID, "no_email_id"));
                expenseData.setUserName(prefs.getString(AppConstants.USER_NAME, "no_user_name"));
                expenseData.setExpenseGroupName(prefs.getString(AppConstants.PRIMARY_FLAT_NAME, "no_flat_name"));
                expenseData.setExpenseGroupId(prefs.getLong(AppConstants.FLAT_EXPENSE_GROUP_ID, 0));
                new AddExpenseAsyncTask(this, this, expenseData).execute();
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
