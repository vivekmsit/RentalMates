package com.example.vivek.rentalmates.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.vivek.rentalmates.services.BackendApiService;
import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfile;


public class UserProfileActivity extends ActionBarActivity {

    EditText nameText = null;
    EditText cityText = null;
    EditText emailIdText = null;

    BackendApiService backendService;
    boolean mBound = false;

    UserProfile userProfile = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        nameText = (EditText) findViewById(R.id.editTextName);
        cityText = (EditText) findViewById(R.id.editTextCity);
        emailIdText = (EditText) findViewById(R.id.editTextEmailId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, BackendApiService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    public void onRegisterButtonClick(View v){
        userProfile = new UserProfile();

        userProfile.setUserName(nameText.getText().toString());
        userProfile.setCurrentPlace(cityText.getText().toString());
        userProfile.setEmailId(emailIdText.getText().toString());

        backendService.uploadUserProfile(this, userProfile);
        //new UploadUserProfileAsyncTask(this, userProfile).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
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

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BackendApiService.LocalBinder binder = (BackendApiService.LocalBinder) service;
            backendService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
