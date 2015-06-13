package com.example.vivek.rentalmates.backend.entities;

import java.io.Serializable;
import java.util.List;

public class AggregateData implements Serializable {

    private int numberOfExpenses;
    private int numberOfExpenseGroups;
    private int numberOfFlats;
    private int numberOfProfiles;
    private int numberOfRequests;

    private List<ExpenseData> expenses;
    private List<ExpenseGroup> expenseGroups;
    private List<FlatInfo> flats;
    private List<UserProfile> userProfiles;
    private List<Request> requests;

    public AggregateData() {
        numberOfExpenses = 0;
        numberOfExpenseGroups = 0;
        numberOfFlats = 0;
        numberOfProfiles = 0;
        numberOfRequests = 0;
    }

    public int getNumberOfExpenses() {
        return numberOfExpenses;
    }

    public void setNumberOfExpenses(int numberOfExpenses) {
        this.numberOfExpenses = numberOfExpenses;
    }

    public int getNumberOfExpenseGroups() {
        return numberOfExpenseGroups;
    }

    public void setNumberOfExpenseGroups(int numberOfExpenseGroups) {
        this.numberOfExpenseGroups = numberOfExpenseGroups;
    }

    public int getNumberOfFlats() {
        return numberOfFlats;
    }

    public void setNumberOfFlats(int numberOfFlats) {
        this.numberOfFlats = numberOfFlats;
    }

    public int getNumberOfProfiles() {
        return numberOfProfiles;
    }

    public void setNumberOfProfiles(int numberOfProfiles) {
        this.numberOfProfiles = numberOfProfiles;
    }

    public int getNumberOfRequests() {
        return numberOfRequests;
    }

    public void setNumberOfRequests(int numberOfRequests) {
        this.numberOfRequests = numberOfRequests;
    }

    public List<ExpenseData> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ExpenseData> expenses) {
        this.expenses = expenses;
    }

    public void addExpense(ExpenseData expenseData) {
        this.expenses.add(expenseData);
        numberOfExpenses++;
    }

    public void deleteExpense(ExpenseData expenseData) {
        this.expenses.remove(expenseData);
        numberOfExpenses--;
    }

    public List<ExpenseGroup> getExpenseGroups() {
        return expenseGroups;
    }

    public void setExpenseGroups(List<ExpenseGroup> expenseGroups) {
        this.expenseGroups = expenseGroups;
    }

    public void addExpenseGroup(ExpenseGroup expenseGroup) {
        this.expenseGroups.add(expenseGroup);
        numberOfExpenseGroups++;
    }

    public void deleteExpenseGroup(ExpenseGroup expenseGroup) {
        this.expenseGroups.remove(expenseGroup);
        numberOfExpenseGroups--;
    }

    public List<FlatInfo> getFlats() {
        return flats;
    }

    public void setFlats(List<FlatInfo> flats) {
        this.flats = flats;
    }

    public void addFlat(FlatInfo flatInfo) {
        this.flats.add(flatInfo);
        numberOfFlats++;
    }

    public void deleteFlat(FlatInfo flatInfo) {
        this.flats.remove(flatInfo);
        numberOfFlats--;
    }

    public List<UserProfile> getUserProfiles() {
        return userProfiles;
    }

    public void setUserProfiles(List<UserProfile> userProfiles) {
        this.userProfiles = userProfiles;
    }

    public void addUserProfile(UserProfile userProfile) {
        this.userProfiles.add(userProfile);
        numberOfProfiles++;
    }

    public void deleteUserProfile(UserProfile userProfile) {
        this.userProfiles.remove(userProfile);
        numberOfProfiles--;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }

    public void addRequest(Request request) {
        this.requests.add(request);
        numberOfRequests++;
    }

    public void deleteRequest(Request request) {
        this.requests.remove(request);
        numberOfRequests--;
    }
}
