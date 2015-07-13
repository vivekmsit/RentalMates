package com.example.vivek.rentalmates.interfaces;

import com.example.vivek.rentalmates.backend.userProfileApi.model.ExpenseGroup;

import java.util.List;

public interface OnExpenseGroupListReceiver {

    void onExpenseGroupListLoadSuccessful(List<ExpenseGroup> expenseGroups);

    void onExpenseGroupListLoadFailed();
}
