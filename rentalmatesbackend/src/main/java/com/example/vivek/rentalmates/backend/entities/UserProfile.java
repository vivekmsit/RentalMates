package com.example.vivek.rentalmates.backend.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Stringify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;

/**
 * The Objectify object model for device registrations we are persisting
 */
@Entity
public class UserProfile {

    @Id
    Long id;

    @Index
    private String userName;

    @Index
    private String currentPlace;

    @Index
    private String emailId;

    private Date date;
    private String profileURL;
    private String profilePhotoURL;
    private String createProfileResult;

    private String currentGcmId;
    private Long primaryFlatId;
    private Long flatExpenseGroupId;
    private Long payback;

    private int numberOfFlats;
    private int numberOfExpenseGroups;
    private int numberOfGcmIds;
    private int numberOfRequests;

    private int updateCount;

    private List<Long> requestIds = new ArrayList<>();
    private List<Long> flatIds = new ArrayList<>();
    private List<Long> expenseGroupIds = new ArrayList<>();
    private List<String> gcmIds = new ArrayList<>();
    private HashMap<String, String> chats;

    public UserProfile() {
        payback = new Long(0);
        numberOfFlats = 0;
        numberOfExpenseGroups = 0;
        numberOfRequests = 0;
        numberOfGcmIds = 0;
        updateCount = -1;
        date = new Date();
    }

    public Long getId() {
        return this.id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String urlName) {
        this.profileURL = urlName;
    }

    public String getProfilePhotoURL() {
        return profilePhotoURL;
    }

    public void setProfilePhotoURL(String urlName) {
        this.profilePhotoURL = urlName;
    }

    public String getCurrentPlace() {
        return currentPlace;
    }

    public void setCurrentPlace(String placeName) {
        this.currentPlace = placeName;
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

    public List<Long> getExpenseGroupIds() {
        return expenseGroupIds;
    }

    public void setExpenseGroupIds(List<Long> expenseGroupIds) {
        this.expenseGroupIds = expenseGroupIds;
    }

    public void addExpenseGroupId(Long expenseGroupId) {
        expenseGroupIds.add(expenseGroupId);
        numberOfExpenseGroups++;
    }

    public void deleteExpenseGroupId(Long expenseGroupId) {
        expenseGroupIds.remove(expenseGroupId);
        numberOfExpenseGroups--;
    }

    public void addFlatId(Long flatId) {
        flatIds.add(flatId);
        numberOfFlats++;
    }

    public void removeFlatId(Long flatId) {
        flatIds.remove(flatId);
        numberOfFlats--;
    }

    public Long getPrimaryFlatId() {
        return primaryFlatId;
    }

    public void setPrimaryFlatId(Long primaryFlatId) {
        this.primaryFlatId = primaryFlatId;
    }

    public Long getFlatExpenseGroupId() {
        return flatExpenseGroupId;
    }

    public void setFlatExpenseGroupId(Long flatExpenseGroupId) {
        this.flatExpenseGroupId = flatExpenseGroupId;
    }

    public Long getPayback() {
        return payback;
    }

    public void setPayback(Long payback) {
        this.payback = payback;
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

    public String getCurrentGcmId() {
        return currentGcmId;
    }

    public void setCurrentGcmId(String currentGcmId) {
        this.currentGcmId = currentGcmId;
    }

    public int getNumberOfRequests() {
        return numberOfRequests;
    }

    public void setNumberOfRequests(int numberOfRequests) {
        this.numberOfRequests = numberOfRequests;
    }

    public List<Long> getRequestIds() {
        return requestIds;
    }

    public void setRequestIds(List<Long> requestIds) {
        this.requestIds = requestIds;
    }

    public void addRequestId(Long id) {
        this.requestIds.add(id);
        numberOfRequests++;
    }

    public void removeRequestId(Long id) {
        this.requestIds.remove(id);
        numberOfRequests--;
    }

    public List<String> getGcmIds() {
        return gcmIds;
    }

    public void setGcmIds(List<String> gcmIds) {
        this.gcmIds = gcmIds;
    }

    public void addGcmId(String id) {
        this.gcmIds.add(id);
        numberOfGcmIds++;
    }

    public void removeGcmId(String id) {
        this.gcmIds.remove(id);
        numberOfGcmIds--;
    }

    public void clearGcmIds() {
        this.gcmIds.clear();
        numberOfGcmIds = 0;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    @OnSave
    public void incrementUpdateCount() {
        updateCount++;
    }

    public void resetUpdateCount() {
        updateCount = -1;
    }

    public HashMap<String, String> getChats() {
        return chats;
    }

    public void setChats(HashMap<String, String> chats) {
        this.chats = chats;
    }
}