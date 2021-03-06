package com.example.vivek.rentalmates.interfaces;

import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfile;

import java.util.List;

public interface OnUserProfileListReceiver {
    void onUserProfileListLoadSuccessful(List<UserProfile> profiles);

    void onUserProfileListLoadFailed();
}
