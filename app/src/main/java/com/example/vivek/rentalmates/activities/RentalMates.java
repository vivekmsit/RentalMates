package com.example.vivek.rentalmates.activities;

import com.firebase.client.Firebase;

/**
 * Created by vivek on 7/30/2016.
 */
public class RentalMates extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
