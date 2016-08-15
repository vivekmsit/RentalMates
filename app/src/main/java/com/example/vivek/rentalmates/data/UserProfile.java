package com.example.vivek.rentalmates.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by vivek on 8/14/2016.
 */
public class UserProfile {
    private String userName;
    private String currentPlace;
    private String emailId;

    //private Date date;
    private String profileURL;
    private String profilePhotoURL;
    //private String createProfileResult;

    //private String currentGcmId;
    //private Long primaryFlatId;
    //private Long flatExpenseGroupId;
    //private Long payback;

    private int numberOfFlats;
    private int numberOfExpenseGroups;
    private int numberOfGcmIds;
    private int numberOfRequests;

    private int updateCount;

    /*private List<Long> requestIds = new ArrayList<>();
    private List<Long> flatIds = new ArrayList<>();
    private List<Long> expenseGroupIds = new ArrayList<>();
    private List<String> gcmIds = new ArrayList<>();
    private List<Long> chatReceiverIds = new ArrayList<>();
    private HashMap<String, String> chats = new HashMap<>();*/

    private HashMap<String, String> flats = new HashMap<>();

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public int getNumberOfFlats() {
        return numberOfFlats;
    }

    public void setNumberOfFlats(int numberOfFlats) {
        this.numberOfFlats = numberOfFlats;
    }

    public int getNumberOfExpenseGroups() {
        return numberOfExpenseGroups;
    }

    public void setNumberOfExpenseGroups(int numberOfExpenseGroups) {
        this.numberOfExpenseGroups = numberOfExpenseGroups;
    }

    public int getNumberOfGcmIds() {
        return numberOfGcmIds;
    }

    public void setNumberOfGcmIds(int numberOfGcmIds) {
        this.numberOfGcmIds = numberOfGcmIds;
    }

    public int getNumberOfRequests() {
        return numberOfRequests;
    }

    public void setNumberOfRequests(int numberOfRequests) {
        this.numberOfRequests = numberOfRequests;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    public HashMap<String, String> getFlats() {
        return flats;
    }

    public void setFlats(HashMap<String, String> flats) {
        this.flats = flats;
    }
}
