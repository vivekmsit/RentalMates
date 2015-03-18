package com.example.vivek.rentalmates.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.tasks.RegisterFlatAsyncTask;

public class RegisterFlatActivity extends ActionBarActivity {

    private static final String TAG = "RegisterFlat_Debug";

    boolean registerButtonClicked;
    TextView textView1;
    TextView textView2;
    TextView textView3;
    EditText editText1;
    Button button1;
    Button registerButton;
    SharedPreferences prefs;

    public void setRegisterButtonClicked(boolean value){
        registerButtonClicked = value;
    }

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
        prefs = this.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    void updateView(boolean isFlatAlreadyRegistered){
        if (isFlatAlreadyRegistered){
            textView1.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
            editText1.setVisibility(View.GONE);
            button1.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);
            registerButton.setText("NEXT");
        }
        else {
            textView1.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            textView3.setVisibility(View.VISIBLE);
            editText1.setVisibility(View.VISIBLE);
            button1.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
        }
    }

    public void onRegisterFlatButtonClick(View view){
        if (registerButtonClicked == true || verifyFlatInfoData() == false) {
            return;
        }
        registerButtonClicked = true;
        FlatInfo flatInfo = new FlatInfo();
        flatInfo.setFlatName(editText1.getText().toString());
        flatInfo.setOwnerEmailId(prefs.getString(AppConstants.EMAIL_ID, "no_email_id"));
        flatInfo.setUserProfileId(prefs.getLong(AppConstants.USER_PROFILE_ID, 0));
        new RegisterFlatAsyncTask(this, this, flatInfo).execute();
    }

    public boolean verifyFlatInfoData() {
        if (editText1.getText().toString().trim().matches("")) {
            Toast.makeText(this, "No Flat Name entered", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}
