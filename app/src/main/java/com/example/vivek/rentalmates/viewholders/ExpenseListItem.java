package com.example.vivek.rentalmates.viewholders;

import com.example.vivek.rentalmates.backend.flatInfoApi.model.ExpenseData;

/**
 * Created by vivek on 3/10/2015.
 */
public class ExpenseListItem {
    public final int amount;       // the drawable for the ListView item ImageView
    public final String description;  // the text for the ListView item description
    public final String ownerEmailId;

    public ExpenseListItem(ExpenseData expenseData) {
        this.amount = expenseData.getAmount();
        this.description = expenseData.getDescription();
        this.ownerEmailId = expenseData.getOwnerEmailId();
    }
}
