package com.example.vivek.rentalmates.backend.entities;

import com.example.vivek.rentalmates.backend.others.LongStringifier;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Serialize;
import com.googlecode.objectify.annotation.Stringify;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class ExpenseData implements Serializable, Comparable<ExpenseData> {

    @Id
    Long id;

    private int amount;
    private Date date;
    private String description;
    private String ownerEmailId;
    private Long submitterId;
    private Long payerId;
    private String userName;
    private Long expenseGroupId;
    private String expenseGroupName;
    private int currencyType;
    private int numberOfMembers;
    private int totalShare;
    private int updateCount;
    List<Long> memberIds;

    @Serialize
    @Stringify(LongStringifier.class)
    private Map<Long, Long> expenseRatios = new HashMap<>();

    @Serialize
    @Stringify(LongStringifier.class)
    private Map<Long, Long> expenseValues = new HashMap<>();

    ExpenseData() {
        date = new Date();
        amount = 0;
        description = "Description";
        ownerEmailId = "EmailId";
        currencyType = 0;
        updateCount = -1;
    }

    public Long getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public Long getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(Long submitterId) {
        this.submitterId = submitterId;
    }

    public Long getPayerId() {
        return payerId;
    }

    public void setPayerId(Long payerId) {
        this.payerId = payerId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getExpenseGroupId() {
        return expenseGroupId;
    }

    public void setExpenseGroupId(Long expenseGroupId) {
        this.expenseGroupId = expenseGroupId;
    }

    public String getExpenseGroupName() {
        return expenseGroupName;
    }

    public void setExpenseGroupName(String expenseGroupName) {
        this.expenseGroupName = expenseGroupName;
    }

    public int getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(int currencyType) {
        this.currencyType = currencyType;
    }

    public int getNumberOfMembers() {
        return numberOfMembers;
    }

    public void setNumberOfMembers(int numberOfMembers) {
        this.numberOfMembers = numberOfMembers;
    }

    public int getTotalShare() {
        return totalShare;
    }

    public void setTotalShare(int totalShare) {
        this.totalShare = totalShare;
    }

    public List<Long> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<Long> memberIds) {
        this.memberIds = memberIds;
    }


    public Map<Long, Long> getExpenseRatios() {
        return expenseRatios;
    }

    public void setExpenseRatios(Map<Long, Long> expenseRatios) {
        this.expenseRatios = expenseRatios;
    }

    public Map<Long, Long> getExpenseValues() {
        return expenseValues;
    }

    public void setExpenseValues(Map<Long, Long> expenseValues) {
        this.expenseValues = expenseValues;
    }

    public void addExpenseMember(Long memberId, Long share, Long amount) {
        expenseRatios.put(memberId, share);
        expenseValues.put(memberId, amount);
        numberOfMembers++;
    }

    public void deleteExpenseMember(Long memberId) {
        expenseRatios.remove(memberId);
        expenseValues.remove(memberId);
        numberOfMembers--;
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

    @Override
    public int compareTo(ExpenseData otherExpenseData) {
        return date.compareTo(otherExpenseData.date);
    }
}
