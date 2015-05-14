package com.example.vivek.rentalmates.viewholders;

import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;

public class ExpenseListItem {
    public final int amount;
    public final String description;
    public final String ownerEmailId;
    public final String userName;
    public final String groupName;
    public final String date;

    public ExpenseListItem(ExpenseData expenseData) {
        this.amount = expenseData.getAmount();
        this.description = expenseData.getDescription();
        this.ownerEmailId = expenseData.getOwnerEmailId();
        this.userName = expenseData.getUserName();
        this.groupName = expenseData.getExpenseGroupName();
        this.date = expenseData.getDate().toString();
    }
}
