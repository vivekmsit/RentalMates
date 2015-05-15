package com.example.vivek.rentalmates.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.others.AppData;

public class DeveloperModeActivity extends ActionBarActivity {
    private AppData appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_mode);
        appData = AppData.getInstance();
    }

    public void onClearAppDataButtonClick(View view) {
        SharedPreferences prefs = this.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        appData.clearAppData(getApplicationContext());
        Intent intent = new Intent(this, FirstActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void onTestButtonClick(View view) {
        Toast.makeText(this, "Test Button Clicked", Toast.LENGTH_SHORT).show();
    }
}
