package com.example.vivek.rentalmates.interfaces;

public interface OnDeleteExpenseReceiver {
    public void onExpenseDeleteSuccessful(int position);

    public void onExpenseDeleteFailed();
}
