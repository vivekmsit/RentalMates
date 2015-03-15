package com.example.vivek.rentalmates.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.tasks.GetExpenseDataListAsyncTask;
import com.example.vivek.rentalmates.tasks.RegisterWithOldFlatAsyncTask;

public class DetermineFlatActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    private static final String TAG = "DetermineFlat_Debug";

    TextView alreadyTextView;
    TextView newRegisteredTextView;
    TextView registerNewFlatTextView;
    Spinner chooseFlatSpinner;
    EditText alreadyRegisteredFlatEditText;
    Button continueWithOldFlatButton;
    Button registerWithOldFlatButton;
    Button registerNewFlatButton;

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
        boolean alreadyRegisteredFlat = intent.getBooleanExtra("FLAT_REGISTERED", false);
        if (!alreadyRegisteredFlat) {
            alreadyTextView.setVisibility(View.INVISIBLE);
            chooseFlatSpinner.setVisibility(View.INVISIBLE);
            continueWithOldFlatButton.setVisibility(View.INVISIBLE);
        }

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.type, android.R.layout.simple_spinner_dropdown_item);
        chooseFlatSpinner.setAdapter(adapter);
        chooseFlatSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG,"inside onClick");

        switch (v.getId()) {

            case R.id.continueWithOldFlatButton:
                Toast.makeText(this, "retrieving ExpenseData list", Toast.LENGTH_SHORT).show();
                new GetExpenseDataListAsyncTask(this, true).execute();
                break;

            case R.id.registerWithOldFlatButton:
                Toast.makeText(this, "registering with old flat", Toast.LENGTH_LONG).show();
                new RegisterWithOldFlatAsyncTask(this, this,alreadyRegisteredFlatEditText.getText().toString()).execute();
                break;

            case R.id.registerNewFlatButton:
                Intent intent = new Intent(this, RegisterFlatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                this.startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView textView= (TextView)view;
        String queryType = textView.getText().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
