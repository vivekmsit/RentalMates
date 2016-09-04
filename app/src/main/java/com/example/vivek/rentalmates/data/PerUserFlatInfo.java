package com.example.vivek.rentalmates.data;

public class PerUserFlatInfo {
    private Long flatId;
    private Long ownerId;
    private String flatKey;
    private String flatName; //Should be unique
    private String city;
    private String ownerEmailId;
    private int numberOfExpenses;
    private int numberOfUsers;
    private String address;
    private int rentAmount;
    private int securityAmount;
    private double latitude;
    private double longitude;

    public PerUserFlatInfo(FlatInfo flatInfo) {
        ownerEmailId = flatInfo.getOwnerEmailId();
        city = flatInfo.getCity();
        flatName = flatInfo.getFlatName();
        rentAmount = flatInfo.getRentAmount();
        securityAmount = flatInfo.getSecurityAmount();
        latitude = flatInfo.getLatitude();
        longitude = flatInfo.getLongitude();
    }

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
}
