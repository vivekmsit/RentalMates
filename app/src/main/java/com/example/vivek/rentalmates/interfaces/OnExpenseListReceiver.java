package com.example.vivek.rentalmates.interfaces;

import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;

import java.util.List;

public interface OnExpenseListReceiver {
    void onExpenseDataListLoadSuccessful(List<ExpenseData> expenses);

    void onExpenseDataListLoadFailed();
}
