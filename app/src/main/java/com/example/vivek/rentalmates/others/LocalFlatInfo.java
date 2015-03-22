package com.example.vivek.rentalmates.others;

import com.example.vivek.rentalmates.backend.flatInfoApi.model.ExpenseData;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vivek on 3/20/2015.
 */
public class LocalFlatInfo {

    public enum FlatType {
        NOT_AVAILABLE,
        SINGLE_BHK,
        DOUBLE_BHK,
        TRIPLE_BHK,
        VILLA
    }

    Long flatId;

    private String adminName;

    private String flatName; //Should be unique

    private boolean available;

    private FlatType type; //1bhk/2bhk/3bhk/villa

    private String city;

    private Date date;

    private String ownerEmailId;

    private Long userProfileId;

    private String createFlatResult;

    private List<Long> userIds = new ArrayList<>();

    private List<ExpenseData> expenses = new ArrayList<ExpenseData>();

    private int numberOfExpenses;

    private int numberOfUsers;


    public Long getFlatId() {
        return flatId;
    }

    public void setFlatId(Long flatId) {
        this.flatId = flatId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getFlatName() {
        return flatName;
    }

    public void setFlatName(String flatName) {
        this.flatName = flatName;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public FlatType getType() {
        return type;
    }

    public void setType(FlatType type) {
        this.type = type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOwnerEmailId() {
        return ownerEmailId;
    }

    public void setOwnerEmailId(String ownerEmailId) {
        this.ownerEmailId = ownerEmailId;
    }

    public Long getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(Long userProfileId) {
        this.userProfileId = userProfileId;
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

    public List<ExpenseData> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ExpenseData> expenses) {
        this.expenses = expenses;
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


    public LocalFlatInfo() {

    }

    public static List<FlatInfo> convertLocalFlatInfoToFlatInfo(List<LocalFlatInfo> localFlats) {
        if (localFlats == null) {
            return null;
        }
        List<FlatInfo> flats = new ArrayList<>();
        for (LocalFlatInfo localFlat : localFlats) {
            FlatInfo data = new FlatInfo();

            data.setUserProfileId(localFlat.getUserProfileId());
            data.setFlatId(localFlat.getFlatId());
            data.setOwnerEmailId(localFlat.getOwnerEmailId());
            data.setFlatName(localFlat.getFlatName());
            data.setOwnerEmailId(localFlat.getOwnerEmailId());
            data.setExpenses(localFlat.getExpenses());
            data.setNumberOfUsers(localFlat.getNumberOfUsers());
            data.setUserIds(localFlat.getUserIds());

            flats.add(data);
        }
        return flats;
    }

    public static List<LocalFlatInfo> convertFlatInfoToLocalFlatInfo(List<FlatInfo> flats) {
        if (flats == null) {
            return null;
        }
        List<LocalFlatInfo> localFlats = new ArrayList<>();
        for (FlatInfo flat : flats) {
            LocalFlatInfo data = new LocalFlatInfo();

            data.setUserProfileId(flat.getUserProfileId());
            data.setAvailable(flat.getAvailable());
            data.setCity(flat.getCity());
            data.setFlatId(flat.getFlatId());
            data.setFlatName(flat.getFlatName());

            localFlats.add(data);
        }
        return localFlats;
    }
}
