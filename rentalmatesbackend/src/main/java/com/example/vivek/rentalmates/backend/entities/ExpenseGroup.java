package com.example.vivek.rentalmates.backend.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vivek on 2/9/2015.
 */
@Entity
public class ExpenseGroup implements Serializable {

    @Id
    Long id;

    private Date date;

    @Index
    private String name;

    private String description;
    private String ownerEmailId;
    private int numberOfExpenses;
    private String operationResult;
    private List<Long> expenseDataIds = new ArrayList<>();

    public ExpenseGroup() {
        date = new Date();
        description = "Description";
        ownerEmailId = "EmailId";
        numberOfExpenses = 0;
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
}
