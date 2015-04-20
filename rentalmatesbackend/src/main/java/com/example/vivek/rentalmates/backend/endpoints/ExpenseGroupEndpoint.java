package com.example.vivek.rentalmates.backend.endpoints;

import com.example.vivek.rentalmates.backend.entities.ExpenseData;
import com.example.vivek.rentalmates.backend.entities.ExpenseGroup;
import com.example.vivek.rentalmates.backend.entities.UserProfile;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
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

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(ExpenseGroup.class);
    }

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
        String name = expenseGroup.getName();
        Query query = ofy().load().type(ExpenseGroup.class);
        query = query.filter("name" + " = ", name);
        List<ExpenseGroup> groups = query.list();
        ExpenseGroup finalExpenseGroup;
        if (groups.size() == 0) {
            ofy().save().entity(expenseGroup).now();
            finalExpenseGroup = ofy().load().entity(expenseGroup).now();
            finalExpenseGroup.setOperationResult("NEW_EXPENSE_GROUP");
        } else {
            finalExpenseGroup = groups.get(0);
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
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
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
        checkExists(id);
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
        List<ExpenseGroup> expenseGroupList = new ArrayList<ExpenseGroup>(limit);
        while (queryIterator.hasNext()) {
            expenseGroupList.add(queryIterator.next());
        }
        return CollectionResponse.<ExpenseGroup>builder().setItems(expenseGroupList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(ExpenseGroup.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find ExpenseGroup with ID: " + id);
        }
    }


    /**
     * Inserts a new {@code ExpenseData} into {@code ExpenseGroup}.
     */
    @ApiMethod(
            name = "addExpenseData",
            path = "expenseGroupAdd",
            httpMethod = ApiMethod.HttpMethod.POST)
    public ExpenseData addExpenseData(ExpenseData expenseData) throws NotFoundException {
        Long expenseGroupId = expenseData.getExpenseGroupId();
        checkExists(expenseGroupId);
        ExpenseGroup expenseGroup = ofy().load().type(ExpenseGroup.class).id(expenseGroupId).now();
        expenseData.setExpenseGroupId(expenseGroupId);
        ofy().save().entity(expenseData).now();
        logger.info("Created ExpenseData.");
        expenseGroup.addExpenseId(expenseData.getId());
        ofy().save().entity(expenseGroup).now();
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
        checkExists(expenseGroupId);
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
                    expenses.add(expenseData);
                    tempIds.add(expenseId);
                }
            }
        }
        if (expenses.size() == 0) {
            return null;
        }
        return expenses;
    }
}