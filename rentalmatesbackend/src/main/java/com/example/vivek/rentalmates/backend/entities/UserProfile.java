package com.example.vivek.rentalmates.backend.entities;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServicePb;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
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
    private Long primaryFlatId;
    private Long flatExpenseGroupId;
    private Long payback;
    private int numberOfFlats;
    private int numberOfExpenseGroups;
    private String currentGcmId;
    private int numberOfGcmIds;
    private List<Long> requestIds = new ArrayList<>();
    private List<Long> flatIds = new ArrayList<>();
    private List<Long> expenseGroupIds = new ArrayList<>();
    private List<String> gcmIds = new ArrayList<>();

    public UserProfile() {
        payback = new Long(0);
        numberOfFlats = 0;
        numberOfExpenseGroups = 0;
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

    public List<Long> getRequestIds() {
        return requestIds;
    }

    public void setRequestIds(List<Long> requestIds) {
        this.requestIds = requestIds;
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
}