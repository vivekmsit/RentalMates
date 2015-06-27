package com.example.vivek.rentalmates.others;

import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.JsonMap;
import com.google.api.client.util.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LocalExpenseData implements Serializable {

    private static final String TAG = "LocalExpenseData_Debug";

    private int amount;
    private String description;
    private String ownerEmailId;
    private Long submitterId;
    private Long payerId;
    private String userName;
    private String expenseGroupName;
    private DateTime date;
    private Long expenseId;
    private int currencyType;
    private int numberOfMembers;
    private Map<Long, Long> membersData = new HashMap<>();

    public LocalExpenseData() {
        this.amount = 0;
        this.description = "description";
        this.ownerEmailId = "emailId";
        this.expenseGroupName = "GROUP NAME";
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerEmailId() {
        return ownerEmailId;
    }

    public void setOwnerEmailId(String emailId) {
        this.ownerEmailId = emailId;
    }

    public Long getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(Long submitterId) {
        this.submitterId = submitterId;
    }

    public Long getPayerId() {
        return payerId;
    }

    public void setPayerId(Long payerId) {
        this.payerId = payerId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getExpenseGroupName() {
        return expenseGroupName;
    }

    public void setExpenseGroupName(String expenseGroupName) {
        this.expenseGroupName = expenseGroupName;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public Long getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(Long expenseId) {
        this.expenseId = expenseId;
    }

    public int getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(int currencyType) {
        this.currencyType = currencyType;
    }

    public int getNumberOfMembers() {
        return numberOfMembers;
    }

    public void setNumberOfMembers(int numberOfMembers) {
        this.numberOfMembers = numberOfMembers;
    }

    public Map<Long, Long> getMembersData() {
        return membersData;
    }

    public void setMembersData(Map<Long, Long> membersData) {
        this.membersData = membersData;
    }

    public void addMemberData(Long memberId, Long share) {
        membersData.put(memberId, share);
        numberOfMembers++;
    }

    public void deleteMemberData(Long memberId) {
        membersData.remove(memberId);
        numberOfMembers--;
    }

    public static List<ExpenseData> convertLocalExpenseToExpense(List<LocalExpenseData> expenses) {
        if (expenses == null) {
            return null;
        }
        List<ExpenseData> localExpenses = new ArrayList<>();
        for (LocalExpenseData expenseData : expenses) {
            ExpenseData data = new ExpenseData();
            data.setAmount(expenseData.getAmount());
            data.setDescription(expenseData.getDescription());
            data.setOwnerEmailId(expenseData.getOwnerEmailId());
            data.setSubmitterId(expenseData.getSubmitterId());
            data.setPayerId(expenseData.getPayerId());
            data.setExpenseGroupName(expenseData.getExpenseGroupName());
            data.setDate(expenseData.getDate());
            data.setUserName(expenseData.getUserName());
            data.setId(expenseData.getExpenseId());
            data.setCurrencyType(expenseData.getCurrencyType());
            data.setNumberOfMembers(expenseData.getNumberOfMembers());

            JsonMap expenseRatios = new JsonMap();
            Set<Long> memberIds = expenseData.getMembersData().keySet();
            for (Long memberId : memberIds) {
                expenseRatios.put(memberId.toString(), 1);
            }
            data.setExpenseRatios(expenseRatios);

            localExpenses.add(data);
        }
        return localExpenses;
    }

    public static List<LocalExpenseData> convertExpenseToLocalExpense(List<ExpenseData> expenses) {
        if (expenses == null) {
            return null;
        }
        List<LocalExpenseData> localExpenses = new ArrayList<>();
        for (ExpenseData expenseData : expenses) {
            LocalExpenseData data = new LocalExpenseData();
            data.setAmount(expenseData.getAmount());
            data.setDescription(expenseData.getDescription());
            data.setOwnerEmailId(expenseData.getOwnerEmailId());
            data.setSubmitterId(expenseData.getSubmitterId());
            data.setPayerId(expenseData.getPayerId());
            data.setExpenseGroupName(expenseData.getExpenseGroupName());
            data.setDate(expenseData.getDate());
            data.setUserName(expenseData.getUserName());
            data.setExpenseId(expenseData.getId());
            data.setCurrencyType(expenseData.getCurrencyType());
            data.setNumberOfMembers(expenseData.getNumberOfMembers());

            Map<Long, Long> expenseRatios = new HashMap<>();
            for (Long memberId : expenseData.getMemberIds()) {
                expenseRatios.put(memberId, new Long(1));
            }
            data.setMembersData(expenseRatios);

            localExpenses.add(data);
        }
        return localExpenses;
    }
}
