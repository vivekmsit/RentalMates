package com.example.vivek.rentalmates.backend.entities;

import com.google.appengine.api.blobstore.BlobKey;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Objectify object model for device registrations we are persisting
 */
@Entity
public class FlatInfo {

    public enum FlatType {
        NOT_AVAILABLE,
        SINGLE_BHK,
        DOUBLE_BHK,
        TRIPLE_BHK,
        VILLA
    }

    @Id
    Long id;

    private String adminName;

    @Index
    private String flatName; //Should be unique

    @Index
    private boolean available;

    @Index
    private FlatType type; //1bhk/2bhk/3bhk/villa

    @Index
    private String city;

    private Date date;

    private double[][] vertices;

    @Index
    private String ownerEmailId;

    private Long userProfileId;

    private String createFlatResult;

    private List<Long> userIds = new ArrayList<>();

    private List<BlobKey> flatPicturesBlobKeys;

    private Long expenseGroupId;

    private int numberOfUsers;

    // you can add more fields...

    public FlatInfo() {
        date = new Date();
        numberOfUsers = 0;
    }

    public Long getFlatId(){
        return this.id;
    }

    public String getFlatName() {
        return flatName;
    }

    public void setFlatName(String name) {
        this.flatName = name;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String cityName) {
        this.city = cityName;
    }

    public String getOwnerEmailId() {
        return ownerEmailId;
    }

    public void setOwnerEmailId(String emailId) {
        this.ownerEmailId = emailId;
    }

    public Long getExpenseGroupId() {
        return expenseGroupId;
    }

    public void setExpenseGroupId(Long expenseGroupId) {
        this.expenseGroupId = expenseGroupId;
    }

    public Long getUserProfileId() {
        return userProfileId;
    }

    public String getCreateFlatResult() {
        return createFlatResult;
    }

    public void setCreateFlatResult(String createFlatResult) {
        this.createFlatResult = createFlatResult;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public void setUserProfileId(Long userProfileId) {
        this.userProfileId = userProfileId;
    }

    public void addUserId(Long id) {
        userIds.add(id);
        numberOfUsers++;
    }

    public void removeUserId(Long id) {
        userIds.remove(id);
        numberOfUsers--;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }

}