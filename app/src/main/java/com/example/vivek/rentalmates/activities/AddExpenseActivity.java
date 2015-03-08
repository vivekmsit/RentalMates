package com.example.vivek.rentalmates.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.ExpenseData;
import com.example.vivek.rentalmates.tasks.AddExpenseAsyncTask;

public class AddExpenseActivity extends ActionBarActivity implements View.OnClickListener {

    private static final String TAG = "MainTabActivity_Debug";

    EditText descriptionEditText;
    EditText amountEditText;
    Button editUsersButton;
    Button cancelExpenseButton;
    Button addExpenseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        amountEditText = (EditText) findViewById(R.id.amountEditText);
        editUsersButton = (Button) findViewById(R.id.editUsersButton);
        cancelExpenseButton = (Button) findViewById(R.id.cancelExpenseButton);
        addExpenseButton = (Button) findViewById(R.id.addExpenseButton);

        editUsersButton.setOnClickListener(this);
        cancelExpenseButton.setOnClickListener(this);
        addExpenseButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "inside onClick");

        switch(v.getId()){

            case R.id.editUsersButton:
                //To be implemented
                break;

            case R.id.cancelExpenseButton:
                Intent intent1 = new Intent(this, MainTabActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                break;

            case R.id.addExpenseButton:
                ExpenseData expenseData = new ExpenseData();
                expenseData.setAmount(Integer.parseInt(amountEditText.getText().toString()));
                expenseData.setDescription(descriptionEditText.getText().toString());
                new AddExpenseAsyncTask(this, expenseData).execute();
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_expense, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
