package com.example.vivek.rentalmates.interfaces;

public interface OnDeleteExpenseReceiver {
    void onExpenseDeleteSuccessful(int position);

    void onExpenseDeleteFailed();
}
