package com.example.vivek.rentalmates.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.tasks.QueryUserProfilesAsyncTask;
import com.example.vivek.rentalmates.R;


public class QueryActivity extends ActionBarActivity  implements AdapterView.OnItemSelectedListener{
    Spinner spinner;
    EditText editText = null;
    String queryType = null;
    String queryValue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        editText = (EditText) findViewById(R.id.editText);
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.type, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void onUserListButtonClick(View view){
        if (queryType == null){
            Toast.makeText(this, "query type not selected", Toast.LENGTH_LONG).show();
            return;
        }
        queryValue = editText.getText().toString();
        new QueryUserProfilesAsyncTask(this, queryType, queryValue).execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView textView= (TextView)view;
        queryType = textView.getText().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
