package com.example.vivek.rentalmates.activities;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.vivek.rentalmates.backend.ExpenseData;
import com.example.vivek.rentalmates.services.BackendApiService;
import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.InputStream;


public class MyLoginActivity extends ActionBarActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Logcat tag
    private static final String TAG = "RentalMatesDebug";
    private static final int RC_SIGN_IN = 0;
    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;
    private static final String USER_PROFILE_UPDATED = "user_profile_updated";
    private static final String SIGN_IN_COMPLETED = "sign_in_completed";
    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */

    public static BackendApiService backendService;
    boolean mBound = false;

    public static GoogleApiClient mClient = null;
    private SignInButton btnSignIn;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private Button btnSignOut, btnRevokeAccess, continueButton;
    private ImageView imgProfilePic;
    private TextView txtName, txtEmail;
    private LinearLayout llProfileLayout;
    private ProgressDialog progressDialog;

    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_login);

        btnSignIn = (SignInButton) findViewById(R.id.my_sign_in_button);
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);
        continueButton = (Button)findViewById(R.id.continue_button);

        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);

        prefs = this.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        if (prefs.contains(SIGN_IN_COMPLETED) && prefs.getBoolean(SIGN_IN_COMPLETED, true)) {
            btnSignIn.setVisibility(View.INVISIBLE);
            Log.d(TAG, "User is already sign in");
        }
        else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(SIGN_IN_COMPLETED, false);
            editor.commit();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing In :) ");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        btnSignIn.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
        btnRevokeAccess.setOnClickListener(this);
        continueButton.setOnClickListener(this);

        // Initializing google plus api client
        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "inside onStart");
        super.onStart();
        mClient.connect();
        Intent intent = new Intent(this, BackendApiService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "inside onStop");
        super.onStop();
        if (mClient.isConnected()) {
            mClient.disconnect();
        }

        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        Log.d(TAG, "inside resolveSignInError");
        if (mConnectionResult.hasResolution()) {
            try {
                Log.d(TAG, "entering startResolutionForResult");
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                Log.d(TAG, "inside SendIntentException");
                mIntentInProgress = false;
                mClient.connect();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_login, menu);
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

    @Override
    public void onConnected(Bundle bundle) {
        progressDialog.cancel();
        Log.d(TAG, "inside onConnected");
        mSignInClicked = false;
        // Get user's information
        getProfileInformation();

        // Update the UI after signin
        updateUI(true);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(SIGN_IN_COMPLETED, true);
        editor.commit();
        Log.d(TAG, "User sign in completed");
        Intent intent = new Intent(this, MainTabActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "inside onConnectionSuspended");
        mClient.connect();
        updateUI(false);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        progressDialog.cancel();
        Log.d(TAG, "inside onConnectionFailed");
        Log.d(TAG, "Failure Reason: "+ connectionResult.toString());
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = connectionResult;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    /**
     * Updating the UI, showing/hiding buttons and profile layout
     * */
    private void updateUI(boolean isSignedIn) {
        Log.d(TAG, "inside updateUI");
        if (isSignedIn) {
            btnSignIn.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
            btnRevokeAccess.setVisibility(View.VISIBLE);
            llProfileLayout.setVisibility(View.VISIBLE);
            continueButton.setVisibility(View.VISIBLE);
        } else {
            btnSignIn.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
            btnRevokeAccess.setVisibility(View.GONE);
            llProfileLayout.setVisibility(View.GONE);
            continueButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "inside onClick");
        switch (v.getId()) {
            case R.id.my_sign_in_button:
                // Signin button clicked
                signInWithGplus();
                break;
            case R.id.btn_sign_out:
                // Signout button clicked
                signOutFromGplus();
                break;
            case R.id.btn_revoke_access:
                // Revoke access button clicked
                revokeGplusAccess();
                break;
            case R.id.continue_button:
                //Go to user profile page
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
        }
    }

    /**
     * Sign-in into google
     * */
    private void signInWithGplus() {
        Log.d(TAG, "inside signInWithGplus");
        if (mClient.isConnected()){
            updateUI(true);
        }
        if (!mClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Sign-out from google
     * */
    private void signOutFromGplus() {
        Log.d(TAG, "inside signOutFromGplus");
        if (mClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mClient);
            mClient.disconnect();
            mClient.connect();
            updateUI(false);
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(SIGN_IN_COMPLETED, false);
        editor.commit();
    }


    /**
     * Revoking access from google
     * */
    private void revokeGplusAccess() {
        Log.d(TAG, "inside revokeGplusAccess");
        if (mClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e(TAG, "User access revoked!");
                            mClient.connect();
                            updateUI(false);
                        }

                    });
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(SIGN_IN_COMPLETED, false);
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "inside onActivityResult");
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK){
            if (!mClient.isConnected() && !mClient.isConnecting()) {
                Log.d(TAG, "connecting again");
                mClient.connect();
            }
        }
    }


    /**
     * Fetching user's information name, email, profile pic
     * */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mClient);
                String personName = currentPerson.getDisplayName();
                String location = currentPerson.getCurrentLocation();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mClient);

                if (prefs.contains(USER_PROFILE_UPDATED)) {
                    Log.d(TAG, "User Profile is already updated");
                }
                else {
                    UserProfile userProfile = new UserProfile();
                    userProfile.setUserName(personName);
                    userProfile.setEmailId(email);
                    userProfile.setCity(location);
                    backendService.uploadUserProfile(this, userProfile);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt(USER_PROFILE_UPDATED, 1);
                    editor.commit();
                }

                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);

                txtName.setText(personName);
                txtEmail.setText(email);

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);


            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Background Async task to load user profile picture from url
     * */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
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
