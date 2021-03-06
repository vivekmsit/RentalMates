package com.example.vivek.rentalmates.data;

import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LocalUserProfile implements Serializable {

    private static final String TAG = "LocalUserProfile_Debug";

    private Long userProfileId;
    private String userName;
    private String profileURL;
    private String profilePhotoURL;
    private String currentPlace;
    private String emailId;
    private String createProfileResult;
    private Long primaryFlatId;
    private int numberOfFlats;
    private Long payback;
    private List<Long> flatIds = new ArrayList<>();

    //Default Constructor
    public LocalUserProfile() {

    }

    public Long getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(Long userProfileId) {
        this.userProfileId = userProfileId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public String getProfilePhotoURL() {
        return profilePhotoURL;
    }

    public void setProfilePhotoURL(String profilePhotoURL) {
        this.profilePhotoURL = profilePhotoURL;
    }

    public String getCurrentPlace() {
        return currentPlace;
    }

    public void setCurrentPlace(String currentPlace) {
        this.currentPlace = currentPlace;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getCreateProfileResult() {
        return createProfileResult;
    }

    public void setCreateProfileResult(String createProfileResult) {
        this.createProfileResult = createProfileResult;
    }

    public List<Long> getFlatIds() {
        return flatIds;
    }

    public void setFlatIds(List<Long> flatIds) {
        this.flatIds = flatIds;
    }

    public Long getPrimaryFlatId() {
        return primaryFlatId;
    }

    public void setPrimaryFlatId(Long primaryFlatId) {
        this.primaryFlatId = primaryFlatId;
    }

    public int getNumberOfFlats() {
        return numberOfFlats;
    }

    public void setNumberOfFlats(int numberOfFlats) {
        this.numberOfFlats = numberOfFlats;
    }

    public Long getPayback() {
        return payback;
    }

    public void setPayback(Long payback) {
        this.payback = payback;
    }

    public static List<UserProfile> convertLocalUserProfileToUserProfile(List<LocalUserProfile> profiles) {
        if (profiles == null) {
            return null;
        }
        List<UserProfile> localUserProfiles = new ArrayList<>();
        for (LocalUserProfile profile : profiles) {
            UserProfile data = new UserProfile();

            data.setId(profile.getUserProfileId());
            data.setEmailId(profile.getEmailId());
            data.setUserName(profile.getUserName());
            data.setCurrentPlace(profile.getCurrentPlace());
            data.setNumberOfFlats(profile.getNumberOfFlats());
            data.setPayback(profile.getPayback());

            localUserProfiles.add(data);
        }
        return localUserProfiles;
    }

    public static List<LocalUserProfile> convertUserProfileToLocalUserProfile(List<UserProfile> profiles) {
        if (profiles == null) {
            return null;
        }
        List<LocalUserProfile> localUserProfiles = new ArrayList<>();
        for (UserProfile profile : profiles) {
            LocalUserProfile data = new LocalUserProfile();

            data.setUserProfileId(profile.getId());
            data.setEmailId(profile.getEmailId());
            data.setUserName(profile.getUserName());
            data.setCurrentPlace(profile.getCurrentPlace());
            data.setNumberOfFlats(profile.getNumberOfFlats());
            data.setPayback(profile.getPayback());

            localUserProfiles.add(data);
        }
        return localUserProfiles;
    }
}
