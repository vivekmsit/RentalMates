package com.example.vivek.rentalmates.data;

import com.example.vivek.rentalmates.backend.userProfileApi.model.ExpenseGroup;
import com.example.vivek.rentalmates.backend.userProfileApi.model.JsonMap;
import com.google.api.client.util.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LocalExpenseGroup implements Serializable {

    private static final String TAG = "LocalExpenseData_Debug";

    private Long id;
    private DateTime date;
    private String name;
    private String description;
    private String ownerEmailId;
    private int numberOfExpenses;
    private int numberOfMembers;
    private String operationResult;
    private List<Long> expenseDataIds = new ArrayList<>();
    private Map<Long, Long> membersData = new HashMap<>();

    public LocalExpenseGroup() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setOwnerEmailId(String ownerEmailId) {
        this.ownerEmailId = ownerEmailId;
    }

    public int getNumberOfExpenses() {
        return numberOfExpenses;
    }

    public void setNumberOfExpenses(int numberOfExpenses) {
        this.numberOfExpenses = numberOfExpenses;
    }

    public int getNumberOfMembers() {
        return numberOfMembers;
    }

    public void setNumberOfMembers(int numberOfMembers) {
        this.numberOfMembers = numberOfMembers;
    }

    public String getOperationResult() {
        return operationResult;
    }

    public void setOperationResult(String operationResult) {
        this.operationResult = operationResult;
    }

    public List<Long> getExpenseDataIds() {
        return expenseDataIds;
    }

    public void setExpenseDataIds(List<Long> expenseDataIds) {
        this.expenseDataIds = expenseDataIds;
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

    public void updateMemberData(Long memberId, Long payback) {
        membersData.put(memberId, payback);
    }

    public static List<ExpenseGroup> convertLocalEGroupToEGroup(List<LocalExpenseGroup> localExpenseGroups) {
        if (localExpenseGroups == null) {
            return null;
        }
        List<ExpenseGroup> expenseGroups = new ArrayList<>();
        for (LocalExpenseGroup localExpenseGroup : localExpenseGroups) {
            ExpenseGroup data = new ExpenseGroup();

            data.setId(localExpenseGroup.getId());
            data.setDate(localExpenseGroup.getDate());
            data.setName(localExpenseGroup.getName());
            data.setDescription(localExpenseGroup.getDescription());
            data.setOwnerEmailId(localExpenseGroup.getOwnerEmailId());
            data.setNumberOfExpenses(localExpenseGroup.getNumberOfExpenses());
            data.setNumberOfMembers(localExpenseGroup.getNumberOfMembers());
            data.setOperationResult(localExpenseGroup.getOperationResult());
            data.setExpenseDataIds(localExpenseGroup.getExpenseDataIds());
            JsonMap membersData = new JsonMap();
            Set<Long> memberIds = localExpenseGroup.getMembersData().keySet();
            for (Long memberId : memberIds) {
                membersData.put(memberId.toString(), localExpenseGroup.getMembersData().get(memberId));
            }
            data.setMembersData(membersData);
            expenseGroups.add(data);
        }
        return expenseGroups;
    }

    public static List<LocalExpenseGroup> convertEGroupToLocalEGroup(List<ExpenseGroup> expenseGroups) {
        if (expenseGroups == null) {
            return null;
        }
        List<LocalExpenseGroup> localExpenseGroups = new ArrayList<>();
        for (ExpenseGroup expenseGroup : expenseGroups) {
            LocalExpenseGroup data = new LocalExpenseGroup();

            data.setId(expenseGroup.getId());
            data.setDate(expenseGroup.getDate());
            data.setName(expenseGroup.getName());
            data.setDescription(expenseGroup.getDescription());
            data.setOwnerEmailId(expenseGroup.getOwnerEmailId());
            data.setNumberOfExpenses(expenseGroup.getNumberOfExpenses());
            data.setNumberOfMembers(expenseGroup.getNumberOfMembers());
            data.setOperationResult(expenseGroup.getOperationResult());
            data.setExpenseDataIds(expenseGroup.getExpenseDataIds());
            Map<Long, Long> membersData = new HashMap<>();
            Set<String> memberIds = expenseGroup.getMembersData().keySet();
            for (String memberId : memberIds) {
                membersData.put(Long.parseLong(memberId), Long.parseLong(expenseGroup.getMembersData().get(memberId).toString()));
            }
            data.setMembersData(membersData);

            localExpenseGroups.add(data);
        }
        return localExpenseGroups;
    }
}
