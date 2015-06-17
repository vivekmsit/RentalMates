package com.example.vivek.rentalmates.backend.entities;

import com.example.vivek.rentalmates.backend.others.LongStringifier;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Serialize;
import com.googlecode.objectify.annotation.Stringify;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class ExpenseGroup implements Serializable {

    @Id
    Long id;

    @Index
    private String name;

    private Date date;
    private String description;
    private String ownerEmailId;
    private int numberOfExpenses;
    private int numberOfMembers;
    private int updateCount;
    private String operationResult;
    private List<Long> expenseDataIds = new ArrayList<>();

    @Serialize
    @Stringify(LongStringifier.class)
    private Map<Long, Long> membersData = new HashMap<>();

    public ExpenseGroup() {
        date = new Date();
        description = "Description";
        ownerEmailId = "EmailId";
        numberOfExpenses = 0;
        numberOfMembers = 0;
        updateCount = -1;
    }

    public Long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getOwnerEmailId() {
        return ownerEmailId;
    }

    public void setOwnerEmailId(String emailId) {
        this.ownerEmailId = emailId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumberOfExpenses() {
        return numberOfExpenses;
    }

    public int getNumberOfMembers() {
        return numberOfMembers;
    }

    public void setNumberOfMembers(int numberOfMembers) {
        this.numberOfMembers = numberOfMembers;
    }

    public void setNumberOfExpenses(int numberOfExpenses) {
        this.numberOfExpenses = numberOfExpenses;
    }

    public String getOperationResult() {
        return operationResult;
    }

    public void setOperationResult(String operationResult) {
        this.operationResult = operationResult;
    }

    public List<Long> getExpenseDataIds() {
        return expenseDataIds;
    }

    public void setExpenseDataIds(List<Long> expenseDataIds) {
        this.expenseDataIds = expenseDataIds;
    }

    public void addExpenseId(Long expenseId) {
        expenseDataIds.add(expenseId);
        numberOfExpenses++;
    }

    public void deleteExpenseId(Long expenseId) {
        expenseDataIds.remove(expenseId);
        numberOfExpenses--;
    }

    public Map<Long, Long> getMembersData() {
        return membersData;
    }

    public void setMembersData(Map<Long, Long> membersData) {
        this.membersData = membersData;
    }

    public void addMemberData(Long memberId, Long payback) {
        membersData.put(memberId, payback);
        numberOfMembers++;
    }

    public void deleteMemberData(Long memberId) {
        membersData.remove(memberId);
        numberOfMembers--;
    }

    public void updateMemberData(Long memberId, Long payback) {
        membersData.put(memberId, payback);
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
}
