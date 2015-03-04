package com.example.vivek.rentalmates.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.vivek.rentalmates.R;

public class FirstActivity extends ActionBarActivity {
    private static final String SIGN_IN_COMPLETED = "sign_in_completed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final Class<? extends Activity> activityClass;
        SharedPreferences prefs = this.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        if(prefs.contains(SIGN_IN_COMPLETED) && prefs.getBoolean(SIGN_IN_COMPLETED, true))
            activityClass = MainTabActivity.class;
        else
            activityClass = MyLoginActivity.class;

        Intent newActivity = new Intent(this, activityClass);
        newActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(newActivity);
        finish();
    }
}
