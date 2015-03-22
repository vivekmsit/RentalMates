package com.example.vivek.rentalmates.others;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.vivek.rentalmates.backend.flatInfoApi.model.ExpenseData;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vivek on 3/11/2015.
 */
public class LocalExpenseData implements Serializable {

    private static final String TAG = "LocalExpenseData_Debug";

    private int amount;
    private String description;
    private String ownerEmailId;

    public LocalExpenseData() {
        this.amount = 0;
        this.description = "description";
        this.ownerEmailId = "emailId";
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
            localExpenses.add(data);
        }
        return localExpenses;
    }
}
