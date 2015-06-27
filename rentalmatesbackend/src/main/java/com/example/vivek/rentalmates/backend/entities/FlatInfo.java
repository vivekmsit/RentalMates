package com.example.vivek.rentalmates.backend.entities;

import com.example.vivek.rentalmates.backend.others.LongStringifier;
import com.google.appengine.api.blobstore.BlobKey;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Serialize;
import com.googlecode.objectify.annotation.Stringify;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Index
    private String flatName; //Should be unique

    @Index
    private boolean available;

    @Index
    private FlatType type; //1bhk/2bhk/3bhk/villa

    @Index
    private String city;

    @Index
    private String ownerEmailId;

    private Date date;
    private Long userProfileId;
    private Long expenseGroupId;
    private int numberOfUsers;
    private int updateCount;
    private int securityAmount;
    private int rentAmount;
    private String createFlatResult;
    private String flatAddress;
    private double[][] vertices;
    private List<Long> memberIds = new ArrayList<>();
    private List<BlobKey> flatPicturesBlobKeys;

    @Serialize
    @Stringify(LongStringifier.class)
    private Map<Long, Long> securityAmountValues = new HashMap<>();

    @Serialize
    @Stringify(LongStringifier.class)
    private Map<Long, Long> rentAmountValues = new HashMap<>();

    public FlatInfo() {
        date = new Date();
        numberOfUsers = 0;
        updateCount = -1;
    }

    public Long getFlatId() {
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

    public List<Long> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<Long> memberIds) {
        this.memberIds = memberIds;
    }

    public void setUserProfileId(Long userProfileId) {
        this.userProfileId = userProfileId;
    }

    public void addMemberId(Long id) {
        memberIds.add(id);
        numberOfUsers++;
    }

    public void removeMemberId(Long id) {
        memberIds.remove(id);
        numberOfUsers--;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
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


    public List<BlobKey> getFlatPicturesBlobKeys() {
        return flatPicturesBlobKeys;
    }

    public void setFlatPicturesBlobKeys(List<BlobKey> flatPicturesBlobKeys) {
        this.flatPicturesBlobKeys = flatPicturesBlobKeys;
    }

    public Map<Long, Long> getSecurityAmountValues() {
        return securityAmountValues;
    }

    public void setSecurityAmountValues(Map<Long, Long> securityAmountValues) {
        this.securityAmountValues = securityAmountValues;
    }

    public Map<Long, Long> getRentAmountValues() {
        return rentAmountValues;
    }

    public void setRentAmountValues(Map<Long, Long> rentAmountValues) {
        this.rentAmountValues = rentAmountValues;
    }

    public int getRentAmount() {
        return rentAmount;
    }

    public void setRentAmount(int rentAmount) {
        this.rentAmount = rentAmount;
    }

    public int getSecurityAmount() {
        return securityAmount;
    }

    public void setSecurityAmount(int securityAmount) {
        this.securityAmount = securityAmount;
    }

    public String getFlatAddress() {
        return flatAddress;
    }

    public void setFlatAddress(String flatAddress) {
        this.flatAddress = flatAddress;
    }

    public FlatType getType() {
        return type;
    }

    public void setType(FlatType type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    public double[][] getVertices() {
        return vertices;
    }

    public void setVertices(double[][] vertices) {
        this.vertices = vertices;
    }
}