package com.example.vivek.rentalmates.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.AppData;

public class FirstActivity extends AppCompatActivity {
    private static final String TAG = "FirstActivity_Debug";
    AppData appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        appData = AppData.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final Class<? extends Activity> activityClass;
        SharedPreferences prefs = this.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs.contains(AppConstants.SIGN_IN_COMPLETED) && prefs.getBoolean(AppConstants.SIGN_IN_COMPLETED, true)) {
            appData.restoreAppData(this);
            activityClass = MainTabActivity.class;
        } else
            activityClass = MyLoginActivity.class;

        Intent newActivity = new Intent(this, activityClass);
        Intent pendingIntent = getIntent();

        if (pendingIntent.getBooleanExtra("notification", false) && pendingIntent.getBooleanExtra("newExpenseAvailable", false)) {
            newActivity.putExtra("notification", true);
            newActivity.putExtra("newExpenseAvailable", true);
            Log.d(TAG, "new expense available");
        }
        newActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(newActivity);
        finish();
    }
}
