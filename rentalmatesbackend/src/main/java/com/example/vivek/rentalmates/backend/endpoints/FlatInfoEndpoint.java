package com.example.vivek.rentalmates.backend.endpoints;

import com.example.vivek.rentalmates.backend.entities.ExpenseGroup;
import com.example.vivek.rentalmates.backend.entities.FlatInfo;
import com.example.vivek.rentalmates.backend.entities.Request;
import com.example.vivek.rentalmates.backend.entities.UserProfile;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.cmd.Query;

import java.io.IOException;
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
        name = "flatInfoApi",
        version = "v1",
        resource = "flatInfo",
        namespace = @ApiNamespace(
                ownerDomain = "backend.rentalmates.vivek.example.com",
                ownerName = "backend.rentalmates.vivek.example.com",
                packagePath = ""
        )
)
public class FlatInfoEndpoint {

    private static final Logger logger = Logger.getLogger(FlatInfoEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    /**
     * Returns the {@link FlatInfo} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code FlatInfo} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "flatInfo/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public FlatInfo get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting FlatInfo with ID: " + id);
        FlatInfo flatInfo = ofy().load().type(FlatInfo.class).id(id).now();
        if (flatInfo == null) {
            throw new NotFoundException("Could not find FlatInfo with ID: " + id);
        }
        return flatInfo;
    }

    /**
     * Inserts a new {@code FlatInfo}.
     */
    @ApiMethod(
            name = "registerNewFlat",
            path = "registerNewFlat",
            httpMethod = ApiMethod.HttpMethod.POST)
    public FlatInfo registerNewFlat(FlatInfo flatInfo) {
        String flatName = flatInfo.getFlatName();
        FlatInfo finalFlatInfo = ofy().load().type(FlatInfo.class).filter("flatName", flatName).first().now();
        if (finalFlatInfo == null) {
            //create a new ExpenseGroup for given flat
            ExpenseGroup expenseGroup = new ExpenseGroup();
            expenseGroup.setName(flatName);
            ExpenseGroup finalExpenseGroup = ofy().load().type(ExpenseGroup.class).filter("name", expenseGroup.getName()).first().now();
            if (finalExpenseGroup == null) {
                ofy().save().entity(expenseGroup).now();
                finalExpenseGroup = ofy().load().entity(expenseGroup).now();
            }
            //update flatInfo here
            flatInfo.setExpenseGroupId(finalExpenseGroup.getId());
            flatInfo.addMemberId(flatInfo.getUserProfileId());
            ofy().save().entity(flatInfo).now();

            //Add created FlatInfo flatId to corresponding UserProfile flatIds list
            finalFlatInfo = ofy().load().entity(flatInfo).now();
            Long userProfileId = finalFlatInfo.getUserProfileId();
            UserProfile relatedUserProfile = ofy().load().type(UserProfile.class).id(userProfileId).now();
            relatedUserProfile.addFlatId(finalFlatInfo.getFlatId());
            relatedUserProfile.setPrimaryFlatId(finalFlatInfo.getFlatId());
            relatedUserProfile.addExpenseGroupId(finalExpenseGroup.getId());
            relatedUserProfile.setFlatExpenseGroupId(finalExpenseGroup.getId());
            ofy().save().entity(relatedUserProfile).now();

            Long l = new Long(0);//need to be changed later
            if (!finalExpenseGroup.getMembersData().keySet().contains(userProfileId)) {
                finalExpenseGroup.addMemberData(userProfileId, l);
                ofy().save().entity(finalExpenseGroup).now();
            }

            finalFlatInfo.setCreateFlatResult("NEW_FLAT_INFO");
        } else {
            finalFlatInfo.setCreateFlatResult("OLD_FLAT_INFO");
        }
        logger.info("Created FlatInfo.");
        return finalFlatInfo;
    }

    /**
     * Inserts a new {@code FlatInfo}.
     */
    @ApiMethod(
            name = "requestRegisterWithOtherFlat",
            path = "requestRegisterWithOtherFlat",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Request requestRegisterWithOtherFlat(@Named("flatName") String flatName, @Named("userProfileId") Long userProfileId) throws IOException {
        FlatInfo flatInfo = ofy().load().type(FlatInfo.class).filter("flatName", flatName).first().now();
        UserProfile requesterUserProfile = ofy().load().type(UserProfile.class).id(userProfileId).now();
        Request request = new Request();
        request.setRequesterId(userProfileId);
        request.setRequestedEntity(flatInfo.getFlatId());
        request.setRequestProviderId(flatInfo.getUserProfileId());
        request.setStatus("PENDING");
        request.setEntityType("FlatInfo");
        request.setRequestedEntityName(flatName);
        request.setRequesterName(requesterUserProfile.getUserName());
        ofy().save().entity(request).now();

        //Add request Id to requestProvider's requestIds list
        UserProfile userProfile = ofy().load().type(UserProfile.class).id(request.getRequestProviderId()).now();
        userProfile.addRequestId(request.getId());
        ofy().save().entity(userProfile).now();

        //send notification to requestProvider
        List<Long> userIds = new ArrayList<>();
        userIds.add(request.getRequestProviderId());
        String message = "NEW_REQUEST";
        ExpenseGroupEndpoint.sendMessage(userIds, message);

        return request;
    }

    /**
     * Inserts a new {@code FlatInfo}.
     */
    @ApiMethod(
            name = "acceptRequestRegisterWithOtherFlat",
            path = "acceptRequestRegisterWithOtherFlat",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Request acceptRequestRegisterWithOtherFlat(@Named("requestId") Long requestId) throws IOException {
        Request request = ofy().load().type(Request.class).id(requestId).now();
        FlatInfo finalFlatInfo = ofy().load().type(FlatInfo.class).id(request.getRequestedEntity()).now();
        Long userProfileId = request.getRequesterId();
        UserProfile userProfile;
        if (finalFlatInfo == null) {
            logger.info("Created FlatInfo.");
            return null;
        } else {
            //Add flatId of flat to UserProfile flatIds List
            userProfile = ofy().load().type(UserProfile.class).id(userProfileId).now();
            ExpenseGroup flatExpenseGroup = ofy().load().type(ExpenseGroup.class).id(finalFlatInfo.getExpenseGroupId()).now();
            if (!userProfile.getFlatIds().contains(finalFlatInfo.getFlatId())) {
                userProfile.addFlatId(finalFlatInfo.getFlatId());
            }
            userProfile.setPrimaryFlatId(finalFlatInfo.getFlatId());
            if (!userProfile.getExpenseGroupIds().contains(flatExpenseGroup.getId())) {
                userProfile.addExpenseGroupId(flatExpenseGroup.getId());
            }
            userProfile.setFlatExpenseGroupId(flatExpenseGroup.getId());
            ofy().save().entity(userProfile).now();

            //Add userProfileId to FlatInfo userIds List
            if (!finalFlatInfo.getMemberIds().contains(userProfileId)) {
                finalFlatInfo.addMemberId(userProfileId);
                ofy().save().entity(finalFlatInfo).now();
            }
            Long l = new Long(0);//need to be changed later
            if (!flatExpenseGroup.getMembersData().keySet().contains(userProfileId)) {
                flatExpenseGroup.addMemberData(userProfileId, l);
                ofy().save().entity(flatExpenseGroup).now();
            }
            finalFlatInfo.setCreateFlatResult("OLD_FLAT_INFO");
        }

        request.setStatus("APPROVED");
        ofy().save().entity(request).now();
        request = ofy().load().entity(request).now();

        //send notification to requester
        List<Long> userIds = new ArrayList<>();
        userIds.add(request.getRequesterId());
        String message = "Your request to join flat " + finalFlatInfo.getFlatName() + " has been approved";
        ExpenseGroupEndpoint.sendMessage(userIds, message);
        return request;
    }

    /**
     * Updates an existing {@code FlatInfo}.
     *
     * @param id       the ID of the entity to be updated
     * @param flatInfo the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code FlatInfo}
     */
    @ApiMethod(
            name = "update",
            path = "flatInfo/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public FlatInfo update(@Named("id") Long id, FlatInfo flatInfo) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(flatInfo).now();
        logger.info("Updated FlatInfo: " + flatInfo);
        return ofy().load().entity(flatInfo).now();
    }

    /**
     * Deletes the specified {@code FlatInfo}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code FlatInfo}
     */
    @ApiMethod(
            name = "remove",
            path = "flatInfo/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(FlatInfo.class).id(id).now();
        logger.info("Deleted FlatInfo with ID: " + id);
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
            path = "flatInfo",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<FlatInfo> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<FlatInfo> query = ofy().load().type(FlatInfo.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<FlatInfo> queryIterator = query.iterator();
        List<FlatInfo> flatInfoList = new ArrayList<>(limit);
        while (queryIterator.hasNext()) {
            flatInfoList.add(queryIterator.next());
        }
        return CollectionResponse.<FlatInfo>builder().setItems(flatInfoList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(FlatInfo.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find FlatInfo with ID: " + id);
        }
    }
}