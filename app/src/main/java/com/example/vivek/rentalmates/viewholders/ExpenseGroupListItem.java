package com.example.vivek.rentalmates.viewholders;

import com.example.vivek.rentalmates.data.LocalExpenseGroup;

public class ExpenseGroupListItem {
    public final String flatName;
    public final String ownerName;
    public final String location;
    public final String members;
    public final String date;

    public ExpenseGroupListItem(LocalExpenseGroup expenseGroup) {
        this.flatName = expenseGroup.getName();
        this.ownerName ="ownerName";
        this.location = "bangalore";
        this.members = "vivek, ashish";
        this.date = "date";
    }
}
