package com.example.vivek.rentalmates.backend.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;

@Entity
public class FlatSearchCriteria implements Serializable {
    @Id
    Long id;
    private boolean sharing;
    private int minRentAmountPerPerson;
    private int maxRentAmountPerPerson;
    private int minSecurityAmountPerPerson;
    private int maxSecurityAmountPerPerson;
    private int areaRange; // in metres
    private Long gender;
    private Long requesterId;
    private boolean notifyIfAvailable;
    private boolean publicPost; //Your requirement will appear in search roommates section
    private double locationLatitude;
    private double locationLongitude;
    private String message; //Only applicable if @publicPost is true
    private String requesterName;
    private String requesterProfilePicture;

    public FlatSearchCriteria() {
        locationLatitude = 12.8486324;
        locationLongitude = 77.6570782;
        areaRange = 10000;
        minRentAmountPerPerson = 0;
        maxRentAmountPerPerson = 100000;
        minSecurityAmountPerPerson = 0;
        maxSecurityAmountPerPerson = 200000;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isSharing() {
        return sharing;
    }

    public void setSharing(boolean sharing) {
        this.sharing = sharing;
    }

    public int getMaxRentAmountPerPerson() {
        return maxRentAmountPerPerson;
    }

    public void setMaxRentAmountPerPerson(int maxRentAmountPerPerson) {
        this.maxRentAmountPerPerson = maxRentAmountPerPerson;
    }

    public int getMaxSecurityAmountPerPerson() {
        return maxSecurityAmountPerPerson;
    }

    public void setMaxSecurityAmountPerPerson(int maxSecurityAmountPerPerson) {
        this.maxSecurityAmountPerPerson = maxSecurityAmountPerPerson;
    }

    public int getAreaRange() {
        return areaRange;
    }

    public void setAreaRange(int areaRange) {
        this.areaRange = areaRange;
    }

    public Long getGender() {
        return gender;
    }

    public void setGender(Long gender) {
        this.gender = gender;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
    }

    public boolean isNotifyIfAvailable() {
        return notifyIfAvailable;
    }

    public void setNotifyIfAvailable(boolean notifyIfAvailable) {
        this.notifyIfAvailable = notifyIfAvailable;
    }

    public boolean isPublicPost() {
        return publicPost;
    }

    public void setPublicPost(boolean publicPost) {
        this.publicPost = publicPost;
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterProfilePicture() {
        return requesterProfilePicture;
    }

    public void setRequesterProfilePicture(String requesterProfilePicture) {
        this.requesterProfilePicture = requesterProfilePicture;
    }

    public int getMinRentAmountPerPerson() {
        return minRentAmountPerPerson;
    }

    public void setMinRentAmountPerPerson(int minRentAmountPerPerson) {
        this.minRentAmountPerPerson = minRentAmountPerPerson;
    }

    public int getMinSecurityAmountPerPerson() {
        return minSecurityAmountPerPerson;
    }

    public void setMinSecurityAmountPerPerson(int minSecurityAmountPerPerson) {
        this.minSecurityAmountPerPerson = minSecurityAmountPerPerson;
    }
}
