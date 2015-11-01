package com.example.vivek.rentalmates.interfaces;

import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseGroup;


public interface OnCreateExpenseGroupReceiver {
    void onCreateExpenseGroupSuccessful(ExpenseGroup expenseGroup);

    void onCreateExpenseGroupFailed();
}
