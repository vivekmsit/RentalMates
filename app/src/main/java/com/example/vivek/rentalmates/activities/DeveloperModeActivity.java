package com.example.vivek.rentalmates.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
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
        SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
        appData.clearAppData(getApplicationContext());
    }

    public void onTestButtonClick(View view) {
        Toast.makeText(this, "Test Button Clicked", Toast.LENGTH_SHORT).show();
    }
}
