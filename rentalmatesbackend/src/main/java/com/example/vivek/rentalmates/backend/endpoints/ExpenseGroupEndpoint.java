package com.example.vivek.rentalmates.backend.endpoints;

import com.example.vivek.rentalmates.backend.entities.ExpenseData;
import com.example.vivek.rentalmates.backend.entities.ExpenseGroup;
import com.example.vivek.rentalmates.backend.entities.RegistrationRecord;
import com.example.vivek.rentalmates.backend.entities.UserProfile;
import com.example.vivek.rentalmates.backend.ofy.OfyService;
import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.cmd.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "expenseGroupApi",
        version = "v1",
        resource = "expenseGroup",
        namespace = @ApiNamespace(
                ownerDomain = "entities.backend.rentalmates.vivek.example.com",
                ownerName = "entities.backend.rentalmates.vivek.example.com",
                packagePath = ""
        )
)
public class ExpenseGroupEndpoint {

    private static final Logger logger = Logger.getLogger(ExpenseGroupEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    /**
     * Returns the {@link ExpenseGroup} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code ExpenseGroup} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "expenseGroup/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public ExpenseGroup get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting ExpenseGroup with ID: " + id);
        ExpenseGroup expenseGroup = ofy().load().type(ExpenseGroup.class).id(id).now();
        if (expenseGroup == null) {
            throw new NotFoundException("Could not find ExpenseGroup with ID: " + id);
        }
        return expenseGroup;
    }

    /**
     * Inserts a new {@code ExpenseGroup}.
     */
    @ApiMethod(
            name = "createExpenseGroup",
            path = "expenseGroup",
            httpMethod = ApiMethod.HttpMethod.POST)
    public ExpenseGroup createExpenseGroup(ExpenseGroup expenseGroup) {
        UserProfile userProfile = ofy().load().type(UserProfile.class).id(expenseGroup.getOwnerId()).now();
        ExpenseGroup finalExpenseGroup = ofy().load().type(ExpenseGroup.class)
                .filter("name", expenseGroup.getName())
                .filter("ownerEmailId", userProfile.getEmailId())
                .first().now();
        if (finalExpenseGroup == null) {
            Long l = (long) 0;//need to be changed later
            expenseGroup.addMemberData(userProfile.getId(), l);
            ofy().save().entity(expenseGroup).now();
            finalExpenseGroup = ofy().load().entity(expenseGroup).now();
            finalExpenseGroup.setOperationResult("NEW_EXPENSE_GROUP");
            userProfile.addExpenseGroupId(finalExpenseGroup.getId());
            ofy().save().entity(userProfile).now();
        } else {
            finalExpenseGroup.setOperationResult("OLD_EXPENSE_GROUP");
        }
        logger.info("Created ExpenseGroup.");
        return finalExpenseGroup;
    }

    /**
     * Updates an existing {@code ExpenseGroup}.
     *
     * @param id           the ID of the entity to be updated
     * @param expenseGroup the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code ExpenseGroup}
     */
    @ApiMethod(
            name = "update",
            path = "expenseGroup/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public ExpenseGroup update(@Named("id") Long id, ExpenseGroup expenseGroup) throws NotFoundException {
        checkExpenseGroupExists(id);
        ofy().save().entity(expenseGroup).now();
        logger.info("Updated ExpenseGroup: " + expenseGroup);
        return ofy().load().entity(expenseGroup).now();
    }

    /**
     * Deletes the specified {@code ExpenseGroup}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code ExpenseGroup}
     */
    @ApiMethod(
            name = "remove",
            path = "expenseGroup/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExpenseGroupExists(id);
        ofy().delete().type(ExpenseGroup.class).id(id).now();
        logger.info("Deleted ExpenseGroup with ID: " + id);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "expenseGroup",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<ExpenseGroup> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<ExpenseGroup> query = ofy().load().type(ExpenseGroup.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<ExpenseGroup> queryIterator = query.iterator();
        List<ExpenseGroup> expenseGroupList = new ArrayList<>(limit);
        while (queryIterator.hasNext()) {
            expenseGroupList.add(queryIterator.next());
        }
        return CollectionResponse.<ExpenseGroup>builder().setItems(expenseGroupList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExpenseGroupExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(ExpenseGroup.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find ExpenseGroup with ID: " + id);
        }
    }

    private void checkExpenseExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(ExpenseData.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find ExpenseData with ID: " + id);
        }
    }

    /**
     * Inserts a new {@code ExpenseData} into {@code ExpenseGroup}.
     */
    @ApiMethod(
            name = "addExpenseData",
            path = "expenseGroupAdd",
            httpMethod = ApiMethod.HttpMethod.POST)
    public ExpenseData addExpenseData(ExpenseData expenseData) throws NotFoundException, IOException {
        Long expenseGroupId = expenseData.getExpenseGroupId();
        checkExpenseGroupExists(expenseGroupId);
        ExpenseGroup expenseGroup = ofy().load().type(ExpenseGroup.class).id(expenseGroupId).now();
        expenseData.setExpenseGroupId(expenseGroupId);
        ofy().save().entity(expenseData).now();
        logger.info("Created ExpenseData.");
        expenseGroup.addExpenseId(expenseData.getId());

        UserProfile memberUserProfile;
        List<Long> gcmMemberIds = new ArrayList<>();
        for (Long memberId : expenseData.getMemberIds()) {
            memberUserProfile = ofy().load().type(UserProfile.class).id(memberId).now();

            Long totalProfileShare = memberUserProfile.getPayback();
            Long expenseGroupShare = expenseGroup.getMembersData().get(memberId);
            Long expenseDataShare = expenseData.getExpenseValues().get(memberId);

            //Update user share of expense data inside expense group as well as in user profile
            if (memberId.equals(expenseData.getPayerId())) {
                expenseGroup.updateMemberData(memberId, expenseGroupShare + (expenseData.getAmount() - expenseDataShare));
                memberUserProfile.setPayback(totalProfileShare + (expenseData.getAmount() - expenseDataShare));
            } else {
                expenseGroup.updateMemberData(memberId, expenseGroupShare - expenseDataShare);
                memberUserProfile.setPayback(totalProfileShare - expenseDataShare);
            }
            ofy().save().entity(memberUserProfile).now();
            ofy().save().entity(expenseGroup).now();

            //Add memberIds to which notification is to be sent
            if (!memberId.equals(expenseData.getSubmitterId())) {
                gcmMemberIds.add(memberId);
            }
        }

        //Send notification to all group members except submitter using GCM.
        if (gcmMemberIds.size() != 0) {
            UserProfile userProfile = ofy().load().type(UserProfile.class).id(expenseData.getSubmitterId()).now();
            String message = userProfile.getUserName() + " added a new expense of Rs. " +
                    expenseData.getAmount() + " for " + expenseData.getDescription();
            sendMessage(gcmMemberIds, message);
        }
        return ofy().load().entity(expenseData).now();
    }

    /**
     * Returns List of {@code ExpenseData} present inside given {@code ExpenseGroup}.
     */
    @ApiMethod(
            name = "getExpenses",
            path = "expenseGroupGetExpenses",
            httpMethod = ApiMethod.HttpMethod.POST)
    public List<ExpenseData> getExpenses(@Named("id") Long expenseGroupId) throws NotFoundException {
        checkExpenseGroupExists(expenseGroupId);
        ExpenseGroup expenseGroup = ofy().load().type(ExpenseGroup.class).id(expenseGroupId).now();
        List<ExpenseData> expenses = new ArrayList<>();
        for (Long expenseId : expenseGroup.getExpenseDataIds()) {
            ExpenseData currentData = ofy().load().type(ExpenseData.class).id(expenseId).now();
            expenses.add(currentData);
        }
        return expenses;
    }


    /**
     * Returns List of all {@code ExpenseData} for a given {@code UserProfile}
     */
    @ApiMethod(
            name = "getAllExpensesList",
            path = "getAllExpensesList/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public List<ExpenseData> getAllExpensesList(@Named("id") Long id) throws NotFoundException {
        List<ExpenseData> expenses = new ArrayList<>();
        List<Long> tempIds = new ArrayList<>();

        UserProfile currentUserProfile = ofy().load().type(UserProfile.class).id(id).now();

        if (currentUserProfile.getNumberOfExpenseGroups() == 0) {
            return null;
        }
        for (Long expenseGroupId : currentUserProfile.getExpenseGroupIds()) {
            ExpenseGroup expenseGroup = ofy().load().type(ExpenseGroup.class).id(expenseGroupId).now();
            if (expenseGroup.getNumberOfExpenses() == 0) {
                continue;
            }
            for (Long expenseId : expenseGroup.getExpenseDataIds()) {
                if (!tempIds.contains(expenseId)) {
                    ExpenseData expenseData = ofy().load().type(ExpenseData.class).id(expenseId).now();
                    if (expenseData.getMemberIds().contains(currentUserProfile.getId())) {
                        expenses.add(expenseData);
                        tempIds.add(expenseId);
                    }
                }
            }
        }
        if (expenses.size() == 0) {
            return null;
        }
        Collections.sort(expenses); //sort by date
        Collections.reverse(expenses); //first expense should be latest one
        return expenses;
    }

    /**
     * Deletes the specified {@code ExpenseData}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code ExpenseGroup}
     */
    @ApiMethod(
            name = "deleteExpense",
            path = "expenseGroup1/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void deleteExpense(@Named("id") Long id) throws NotFoundException {
        checkExpenseExists(id);
        ExpenseData expenseData = ofy().load().type(ExpenseData.class).id(id).now();
        ExpenseGroup expenseGroup = ofy().load().type(ExpenseGroup.class).id(expenseData.getExpenseGroupId()).now();

        UserProfile memberUserProfile;
        for (Long memberId : expenseData.getMemberIds()) {
            memberUserProfile = ofy().load().type(UserProfile.class).id(memberId).now();

            Long totalProfileShare = memberUserProfile.getPayback();
            Long expenseGroupShare = expenseGroup.getMembersData().get(memberId);
            Long expenseDataShare = expenseData.getExpenseValues().get(memberId);

            //Update user share of expense data inside expense group as well as in user profile
            if (memberId.equals(expenseData.getPayerId())) {
                expenseGroup.updateMemberData(memberId, expenseGroupShare - (expenseData.getAmount() - expenseDataShare));
                memberUserProfile.setPayback(totalProfileShare - (expenseData.getAmount() - expenseDataShare));
            } else {
                expenseGroup.updateMemberData(memberId, expenseGroupShare + expenseDataShare);
                memberUserProfile.setPayback(totalProfileShare + expenseDataShare);
            }
            ofy().save().entity(memberUserProfile).now();
        }

        expenseGroup.deleteExpenseId(expenseData.getId());
        ofy().save().entity(expenseGroup).now();
        ofy().delete().type(ExpenseData.class).id(id).now();
        logger.info("Deleted ExpenseData with ID: " + id);
    }


    /**
     * Function to send message to multiple devices using GCM
     */
    @ApiMethod(
            name = "sendMessage",
            path = "sendMessage",
            httpMethod = ApiMethod.HttpMethod.GET)
    public static void sendMessage(@Named("userIds") final List<Long> userIds, @Named("message") final String message) throws IOException {
        if (userIds.size() == 0) {
            logger.info("userIds list empty");
            return;
        }
        ThreadFactory f = ThreadManager.currentRequestThreadFactory();
        f.newThread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendMessageThread(userIds, message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).run();
    }

    //Thread for sending message to multiple devices using GCM
    public static void sendMessageThread(@Named("userIds") List<Long> userIds, @Named("message") String message) throws IOException {
        String GCM_API_KEY = System.getProperty("gcm.api.key");
        Sender sender = new Sender(GCM_API_KEY);
        Message msg = new Message.Builder().addData("message", message).build();
        for (Long userId : userIds) {
            UserProfile userProfile = ofy().load().type(UserProfile.class).id(userId).now();
            List<String> gcmIds = userProfile.getGcmIds();
            for (String gcmId : gcmIds) {
                Result result = sender.send(msg, gcmId, 3);
                if (result.getMessageId() != null) {
                    logger.info("Message sent to " + gcmId);
                    String canonicalRegId = result.getCanonicalRegistrationId();
                    if (canonicalRegId != null) {
                        // regId changed for the device, update the DataStore
                        logger.info("Gcm Id changed for " + gcmId + " updating to " + canonicalRegId);

                        //Update gcmId inside UserProfile
                        userProfile.removeGcmId(gcmId);
                        userProfile.addGcmId(canonicalRegId);
                        OfyService.ofy().save().entity(userProfile).now();

                        //Update corresponding RegistrationRecord entity
                        RegistrationRecord record = OfyService.ofy().load().type(RegistrationRecord.class).filter("regId", gcmId).first().now();
                        record.setRegId(canonicalRegId);
                        OfyService.ofy().save().entity(record).now();
                    }
                } else {
                    String error = result.getErrorCodeName();
                    if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                        // Device is no longer registered with Gcm, remove it from the DataStore
                        logger.warning("GCM Id " + gcmId + " no longer registered with GCM, removing from DataStore");

                        //Remove gcmId from UserProfile
                        userProfile.removeGcmId(gcmId);
                        OfyService.ofy().save().entity(userProfile).now();

                        //Delete corresponding RegistrationRecord entity
                        RegistrationRecord record = OfyService.ofy().load().type(RegistrationRecord.class).filter("regId", gcmId).first().now();
                        OfyService.ofy().delete().entity(record).now();
                    } else {
                        logger.warning("Error when sending message : " + error);
                    }
                }
            }
        }
    }


}