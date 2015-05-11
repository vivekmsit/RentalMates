package com.example.vivek.rentalmates.activities;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfo;
import com.example.vivek.rentalmates.interfaces.OnFlatInfoListReceiver;
import com.example.vivek.rentalmates.interfaces.OnGcmRegistrationReceiver;
import com.example.vivek.rentalmates.interfaces.OnUploadUserProfileReceiver;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.others.AppData;
import com.example.vivek.rentalmates.services.BackendApiService;
import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfile;
import com.example.vivek.rentalmates.tasks.GcmRegistrationAsyncTask;
import com.example.vivek.rentalmates.tasks.GetFlatInfoListAsyncTask;
import com.example.vivek.rentalmates.tasks.UploadUserProfileAsyncTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.util.List;

public class MyLoginActivity extends ActionBarActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MyLoginActivity_Debug";
    private static final int RC_SIGN_IN = 0;

    public static BackendApiService backendService;

    boolean mBound = false;
    private AppData appData;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    public static GoogleApiClient mClient = null;
    private ConnectionResult mConnectionResult;
    private SignInButton btnSignIn;
    private Button btnSignOut, btnRevokeAccess, continueButton;
    private ImageView imgProfilePic;
    private TextView txtName, txtEmail;
    private LinearLayout llProfileLayout;
    private ProgressDialog progressDialog;
    private Context context;
    private SharedPreferences prefs;


    public void setSignInClicked(boolean value) {
        mSignInClicked = value;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_login);

        mSignInClicked = false;
        context = getApplicationContext();

        btnSignIn = (SignInButton) findViewById(R.id.my_sign_in_button);
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);
        continueButton = (Button) findViewById(R.id.continue_button);

        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);

        prefs = this.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        if (prefs.contains(AppConstants.SIGN_IN_COMPLETED) && prefs.getBoolean(AppConstants.SIGN_IN_COMPLETED, true)) {
            btnSignIn.setVisibility(View.INVISIBLE);
            Log.d(TAG, "User is already sign in");
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing In :) ");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);

        btnSignIn.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
        btnRevokeAccess.setOnClickListener(this);
        continueButton.setOnClickListener(this);

        // Initializing google plus api client
        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
        appData = AppData.getInstance();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "inside onStart");
        progressDialog.show();
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
     */
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
    public void onConnected(Bundle bundle) {
        progressDialog.cancel();
        Log.d(TAG, "inside onConnected");

        if (prefs.contains(AppConstants.FIRST_TIME_LOGIN) && prefs.getBoolean(AppConstants.FIRST_TIME_LOGIN, true)) {
            Log.d(TAG, "FIRST_TIME_LOGIN already set to true");
            if (mSignInClicked) {
                mSignInClicked = false;
                Intent intent = new Intent(this, MainTabActivity.class);
                startActivity(intent);
                finish();
            } else {
                updateUI(true);
            }
        } else if (mSignInClicked) {
            firstTimeLogin();
        } else {
            Log.d(TAG, "Login Required state");
            updateUI(false);
        }
        Log.d(TAG, "User sign in completed");
    }

    //First time login related procedure
    public void firstTimeLogin() {
        Log.d(TAG, "first time login");
        // Get UserProfile information
        final UserProfile userProfile = getProfileInformation();
        txtName.setText(userProfile.getUserName());
        txtEmail.setText(userProfile.getEmailId());
        setProfilePicture(userProfile);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(AppConstants.SIGN_IN_COMPLETED, false);
        editor.putString(AppConstants.EMAIL_ID, userProfile.getEmailId());
        editor.putString(AppConstants.USER_NAME, userProfile.getUserName());
        editor.apply();
        GcmRegistrationAsyncTask task = new GcmRegistrationAsyncTask(context);
        task.setOnGcmRegistrationReceiver(new OnGcmRegistrationReceiver() {
            @Override
            public void onGcmRegisterSuccessful(String regId) {
                userProfile.setCurrentGcmId(regId);
                uploadUserProfile(userProfile);
            }

            @Override
            public void onGcmRegisterFailed() {

            }
        });
        task.execute();
    }

    public void uploadUserProfile(UserProfile userProfile) {
        UploadUserProfileAsyncTask task = new UploadUserProfileAsyncTask(this, this, userProfile);
        task.setOnUploadUserProfileReceiver(new OnUploadUserProfileReceiver() {
            @Override
            public void onUploadUserProfileSuccessful(String message) {
                setSignInClicked(false);
                if (message.equals("SUCCESS_NO_FLAT_REGISTERED")) {
                    Toast.makeText(context, "UserProfile uploaded", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, DetermineFlatActivity.class);
                    intent.putExtra("FLAT_REGISTERED", false);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                } else if (message.equals("SUCCESS_FLAT_REGISTERED")) {
                    getFlatInfoList();
                }
            }

            @Override
            public void onUploadUserProfileFailed() {
            }
        });
        task.execute();
    }

    public void getFlatInfoList() {
        GetFlatInfoListAsyncTask flatTask = new GetFlatInfoListAsyncTask(context);
        flatTask.setOnFlatInfoListReceiver(new OnFlatInfoListReceiver() {
            @Override
            public void onFlatInfoListLoadSuccessful(List<FlatInfo> flats) {
                if (flats == null) {
                    Toast.makeText(context, "No flat registered for given user", Toast.LENGTH_LONG).show();
                    return;
                }
                appData.storeFlatInfoList(context, flats);
                Toast.makeText(context, "FlatInfo List retrieved successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, DetermineFlatActivity.class);
                intent.putExtra("FLAT_REGISTERED", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }

            @Override
            public void onFlatInfoListLoadFailed() {
            }
        });
        flatTask.execute();
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
        Log.d(TAG, "Failure Reason: " + connectionResult.toString());
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
     */
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
     */
    private void signInWithGplus() {
        Log.d(TAG, "inside signInWithGplus");
        if (mSignInClicked) {
            //multiple sign in button click
            return;
        }
        mSignInClicked = true;
        if (mClient.isConnected()) {
            mClient.disconnect();
            mClient.connect();
        } else {
            mClient.connect();
        }
    }

    /**
     * Sign-out from google
     */
    private void signOutFromGplus() {
        Log.d(TAG, "inside signOutFromGplus");
        if (mClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mClient);
            mClient.disconnect();
            mClient.connect();
            updateUI(false);
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(AppConstants.SIGN_IN_COMPLETED, false);
        editor.apply();
    }


    /**
     * Revoking access from google
     */
    private void revokeGplusAccess() {
        Log.d(TAG, "inside revokeGplusAccess");
        if (mClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e(TAG, "User access revoked!");
                            mClient.disconnect();
                            mClient.connect();
                            updateUI(false);
                        }

                    });
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(AppConstants.SIGN_IN_COMPLETED, false);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "inside onActivityResult");
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            if (!mClient.isConnected() && !mClient.isConnecting()) {
                Log.d(TAG, "connecting again");
                mClient.connect();
            }
        }
    }


    /**
     * Fetching user's information name, email, profile pic
     */
    private UserProfile getProfileInformation() {
        UserProfile userProfile = null;
        try {
            if (Plus.PeopleApi.getCurrentPerson(mClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mClient);
                userProfile = new UserProfile();
                userProfile.setUserName(currentPerson.getDisplayName());
                userProfile.setCurrentPlace(currentPerson.getCurrentLocation());
                userProfile.setProfilePhotoURL(currentPerson.getImage().getUrl());
                userProfile.setProfileURL(currentPerson.getUrl());
                userProfile.setEmailId(Plus.AccountApi.getAccountName(mClient));
            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userProfile;
    }


    /**
     * Function to set profilePicture View
     */
    public void setProfilePicture(UserProfile userProfile) {
        String emailId = userProfile.getEmailId();
        Bitmap bm = appData.getProfilePictureBitmap(this, emailId);
        if (bm == null) {
            Toast.makeText(this, "no profile picture found", Toast.LENGTH_LONG).show();
            Log.d(TAG, "no profile picture found");
            return;
        }
        imgProfilePic.setImageBitmap(bm);
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
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
