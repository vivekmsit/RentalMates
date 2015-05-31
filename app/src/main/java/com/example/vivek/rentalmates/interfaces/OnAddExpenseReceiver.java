package com.example.vivek.rentalmates.interfaces;

import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;

public interface OnAddExpenseReceiver {
    void onAddExpenseSuccessful(ExpenseData expenseData);

    void onAddExpenseFailed();
}
