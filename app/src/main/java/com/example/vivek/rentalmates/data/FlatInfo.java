package com.example.vivek.rentalmates.data;

import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vivek on 8/14/2016.
 */
public class FlatInfo {
    private Long flatId;
    private Long ownerId;
    private String flatKey;
    //private String adminName;
    private String flatName; //Should be unique
    //boolean available;
    //private FlatType type; //1bhk/2bhk/3bhk/villa
    private String city;
    //private Date date;
    private String ownerEmailId;
    //private String createFlatResult;
    //private List<Long> userIds = new ArrayList<>();
    //private List<ExpenseData> expenses = new ArrayList<ExpenseData>();
    private int numberOfExpenses;
    private int numberOfUsers;
    //private Long flatExpenseGroupId;
    private String address;
    private int rentAmount;
    private int securityAmount;
    private double latitude;
    private double longitude;

    public Long getFlatId() {
        return flatId;
    }

    public void setFlatId(Long flatId) {
        this.flatId = flatId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getFlatKey() {
        return flatKey;
    }

    public void setFlatKey(String flatKey) {
        this.flatKey = flatKey;
    }

    public String getFlatName() {
        return flatName;
    }

    public void setFlatName(String flatName) {
        this.flatName = flatName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOwnerEmailId() {
        return ownerEmailId;
    }

    public void setOwnerEmailId(String ownerEmailId) {
        this.ownerEmailId = ownerEmailId;
    }

    public int getNumberOfExpenses() {
        return numberOfExpenses;
    }

    public void setNumberOfExpenses(int numberOfExpenses) {
        this.numberOfExpenses = numberOfExpenses;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    //private float zoom;
}
