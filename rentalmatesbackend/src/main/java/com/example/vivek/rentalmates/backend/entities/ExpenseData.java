package com.example.vivek.rentalmates.backend.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class ExpenseData implements Serializable {

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
    private List<Long> memberIds = new ArrayList<>();

    ExpenseData() {
        date = new Date();
        amount = 0;
        description = "Description";
        ownerEmailId = "EmailId";
        currencyType = 0;
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

    public List<Long> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<Long> memberIds) {
        this.memberIds = memberIds;
    }

    public void addMemberId(Long memberId) {
        memberIds.add(memberId);
        numberOfMembers++;
    }

    public void deleteMemberId(Long memberId) {
        memberIds.remove(memberId);
        numberOfMembers--;
    }

}
