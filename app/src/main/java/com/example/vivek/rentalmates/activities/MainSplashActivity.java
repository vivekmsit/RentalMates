package com.example.vivek.rentalmates.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.AppData;

public class MainSplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity_Debug";
    private static final int SPLASH_SHOW_TIME = 0;
    private Context context;
    private AppData appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_splash);
        context = getApplicationContext();
        new BackgroundSplashTask().execute();
    }

    private class BackgroundSplashTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                Thread.sleep(SPLASH_SHOW_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            appData = AppData.getInstance();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            final Class<? extends Activity> activityClass;
            SharedPreferences prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
            if (prefs.contains(AppConstants.SIGN_IN_COMPLETED) && prefs.getBoolean(AppConstants.SIGN_IN_COMPLETED, true)) {
                appData.restoreAppData(context);
                activityClass = MainTabActivity.class;
            } else
                activityClass = MyLoginActivity.class;

            Intent newActivity = new Intent(context, activityClass);
            Intent pendingIntent = getIntent();

            if (pendingIntent.getBooleanExtra("notification", false) && pendingIntent.getBooleanExtra("newExpenseAvailable", false)) {
                newActivity.putExtra("notification", true);
                newActivity.putExtra("newExpenseAvailable", true);
                Log.d(TAG, "new expense available");
            }
            newActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newActivity);
            finish();
        }
    }
}
