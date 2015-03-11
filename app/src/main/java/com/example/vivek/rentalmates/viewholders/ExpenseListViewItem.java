package com.example.vivek.rentalmates.viewholders;

/**
 * Created by vivek on 3/10/2015.
 */
public class ExpenseListViewItem {
    public final int amount;       // the drawable for the ListView item ImageView
    public final String description;  // the text for the ListView item description
    public final String ownerEmailId;

    public ExpenseListViewItem(int amount, String description, String ownerEmailId) {
        this.amount = amount;
        this.description = description;
        this.ownerEmailId = ownerEmailId;
    }
}
