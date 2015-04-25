package com.example.vivek.rentalmates.others;

import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;
import com.google.api.client.util.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vivek on 3/11/2015.
 */
public class LocalExpenseData implements Serializable {

    private static final String TAG = "LocalExpenseData_Debug";

    private int amount;
    private String description;
    private String ownerEmailId;
    private String userName;
    private String expenseGroupName;
    private DateTime date;
    private Long expenseId;

    public LocalExpenseData() {
        this.amount = 0;
        this.description = "description";
        this.ownerEmailId = "emailId";
        this.expenseGroupName = "GROUP NAME";
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerEmailId() {
        return ownerEmailId;
    }

    public void setOwnerEmailId(String emailId) {
        this.ownerEmailId = emailId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getExpenseGroupName() {
        return expenseGroupName;
    }

    public void setExpenseGroupName(String expenseGroupName) {
        this.expenseGroupName = expenseGroupName;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public Long getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(Long expenseId) {
        this.expenseId = expenseId;
    }

    public static List<ExpenseData> convertLocalExpenseToExpense(List<LocalExpenseData> expenses) {
        if (expenses == null) {
            return null;
        }
        List<ExpenseData> localExpenses = new ArrayList<>();
        for (LocalExpenseData expenseData : expenses) {
            ExpenseData data = new ExpenseData();
            data.setAmount(expenseData.getAmount());
            data.setDescription(expenseData.getDescription());
            data.setOwnerEmailId(expenseData.getOwnerEmailId());
            data.setExpenseGroupName(expenseData.getExpenseGroupName());
            data.setDate(expenseData.getDate());
            data.setUserName(expenseData.getUserName());
            data.setId(expenseData.getExpenseId());
            localExpenses.add(data);
        }
        return localExpenses;
    }

    public static List<LocalExpenseData> convertExpenseToLocalExpense(List<ExpenseData> expenses) {
        if (expenses == null) {
            return null;
        }
        List<LocalExpenseData> localExpenses = new ArrayList<>();
        for (ExpenseData expenseData : expenses) {
            LocalExpenseData data = new LocalExpenseData();
            data.setAmount(expenseData.getAmount());
            data.setDescription(expenseData.getDescription());
            data.setOwnerEmailId(expenseData.getOwnerEmailId());
            data.setExpenseGroupName(expenseData.getExpenseGroupName());
            data.setDate(expenseData.getDate());
            data.setUserName(expenseData.getUserName());
            data.setExpenseId(expenseData.getId());
            localExpenses.add(data);
        }
        return localExpenses;
    }
}
