package com.example.vivek.rentalmates.backend.endpoints;

import com.example.vivek.rentalmates.backend.entities.Contact;
import com.example.vivek.rentalmates.backend.entities.ExpenseData;
import com.example.vivek.rentalmates.backend.entities.ExpenseGroup;
import com.example.vivek.rentalmates.backend.entities.FlatInfo;
import com.example.vivek.rentalmates.backend.entities.RegistrationRecord;
import com.example.vivek.rentalmates.backend.entities.Request;
import com.example.vivek.rentalmates.backend.entities.UserProfile;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.GetRequest;
import com.google.appengine.api.search.GetResponse;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchException;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.StatusCode;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
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
        name = "userProfileApi",
        version = "v1",
        resource = "userProfile",
        namespace = @ApiNamespace(
                ownerDomain = "backend.rentalmates.vivek.example.com",
                ownerName = "backend.rentalmates.vivek.example.com",
                packagePath = ""
        )
)
public class UserProfileEndpoint {

    private static final Logger logger = Logger.getLogger(UserProfileEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    /**
     * Returns the {@link UserProfile} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code UserProfile} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "userProfile/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public UserProfile get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting UserProfile with ID: " + id);
        UserProfile userProfile = ofy().load().type(UserProfile.class).id(id).now();
        if (userProfile == null) {
            throw new NotFoundException("Could not find UserProfile with ID: " + id);
        }
        return userProfile;
    }

    /**
     * Inserts a new {@code UserProfile}.
     */
    @ApiMethod(
            name = "insert",
            path = "userProfile",
            httpMethod = ApiMethod.HttpMethod.POST)
    public UserProfile insert(UserProfile userProfile) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that userProfile.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        String emailId = userProfile.getEmailId();
        String currentGcmId = userProfile.getCurrentGcmId();

        UserProfile finalUserProfile;
        finalUserProfile = ofy().load().type(UserProfile.class).filter("emailId", emailId).first().now();

        if (finalUserProfile == null) {
            logger.info("Created UserProfile.");
            userProfile.setCurrentGcmId(currentGcmId);
            userProfile.addGcmId(currentGcmId);
            ofy().save().entity(userProfile).now();
            finalUserProfile = ofy().load().entity(userProfile).now();
            finalUserProfile.setCreateProfileResult("NEW_USER_PROFILE");
        } else {
            //Below statement will be removed later to support multiple devices
            finalUserProfile.clearGcmIds();
            if (!finalUserProfile.getGcmIds().contains(currentGcmId)) {
                finalUserProfile.setCurrentGcmId(currentGcmId);
                finalUserProfile.addGcmId(currentGcmId);
            }
            ofy().save().entity(finalUserProfile).now();
            finalUserProfile.setCreateProfileResult("OLD_USER_PROFILE");
        }
        return finalUserProfile;
    }

    /**
     * Updates an existing {@code UserProfile}.
     *
     * @param id          the ID of the entity to be updated
     * @param userProfile the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code UserProfile}
     */
    @ApiMethod(
            name = "update",
            path = "userProfile/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public UserProfile update(@Named("id") Long id, UserProfile userProfile) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(userProfile).now();
        logger.info("Updated UserProfile: " + userProfile);
        return ofy().load().entity(userProfile).now();
    }

    /**
     * Deletes the specified {@code UserProfile}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code UserProfile}
     */
    @ApiMethod(
            name = "remove",
            path = "userProfile/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(UserProfile.class).id(id).now();
        logger.info("Deleted UserProfile with ID: " + id);
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
            path = "userProfile",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<UserProfile> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<UserProfile> query = ofy().load().type(UserProfile.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<UserProfile> queryIterator = query.iterator();
        List<UserProfile> userProfileList = new ArrayList<>();
        while (queryIterator.hasNext()) {
            userProfileList.add(queryIterator.next());
        }
        return CollectionResponse.<UserProfile>builder().setItems(userProfileList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(UserProfile.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find UserProfile with ID: " + id);
        }
    }


    /**
     * List all User Profiles for a given location
     */
    @ApiMethod(
            name = "queryUserProfiles",
            path = "queryUserProfiles",
            httpMethod = ApiMethod.HttpMethod.POST)
    public List<UserProfile> queryUserProfiles(@Named("type") String type, @Named("value") String value) {
        Query query = ofy().load().type(UserProfile.class);
        query = query.filter(type + " = ", value);
        return (List<UserProfile>) query.list();
    }


    private boolean removeAllSearchDocuments() {
        IndexSpec indexSpec = IndexSpec.newBuilder().setName("FlatInfoIndex").build();
        Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
        try {
            // looping because getRange by default returns up to 100 documents at a time
            while (true) {
                List<String> docIds = new ArrayList<String>();
                // Return a set of doc_ids.
                GetRequest request = GetRequest.newBuilder().setReturningIdsOnly(true).build();
                GetResponse<Document> response = index.getRange(request);
                if (response.getResults().isEmpty()) {
                    break;
                }
                for (Document doc : response) {
                    docIds.add(doc.getId());
                }
                index.delete(docIds);
            }
        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }

    private void removeAllDataStoreData() {
        for (RegistrationRecord registrationRecord : ofy().load().type(RegistrationRecord.class).list()) {
            ofy().delete().entity(registrationRecord);
        }
        for (ExpenseData expenseData : ofy().load().type(ExpenseData.class).list()) {
            ofy().delete().entity(expenseData);
        }
        for (ExpenseGroup expenseGroup : ofy().load().type(ExpenseGroup.class).list()) {
            ofy().delete().entity(expenseGroup);
        }
        for (Request request : ofy().load().type(Request.class).list()) {
            ofy().delete().entity(request);
        }
        for (Contact contact : ofy().load().type(Contact.class).list()) {
            ofy().delete().entity(contact);
        }
        for (FlatInfo flatInfo : ofy().load().type(FlatInfo.class).list()) {
            ofy().delete().entity(flatInfo);
        }
        for (UserProfile userProfile1 : ofy().load().type(UserProfile.class).list()) {
            ofy().delete().entity(userProfile1);
        }
    }

    /**
     * Removes all DataStore data
     */
    @ApiMethod(
            name = "removeDataStoreData",
            path = "removeDataStoreData",
            httpMethod = ApiMethod.HttpMethod.POST)
    public void removeDataStoreData(@Named("requesterId") Long requesterId) {
        UserProfile userProfile = ofy().load().type(UserProfile.class).id(requesterId).now();
        if (userProfile == null) {
            return;
        }

        if (userProfile.getEmailId().equals("vivekmsit@gmail.com")) {
            ThreadFactory f = ThreadManager.currentRequestThreadFactory();
            f.newThread(new Runnable() {
                @Override
                public void run() {
                    removeAllDataStoreData();
                }
            }).run();
            f.newThread(new Runnable() {
                @Override
                public void run() {
                    removeAllSearchDocuments();
                }
            }).run();
        }
    }


    /**
     * Get ExpenseData list from specified {@code FlatInfo}.
     *
     * @param id the ID of the FlatInfo which contains ExpenseData list
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code FlatInfo}
     */
    @ApiMethod(
            name = "getFlatInfoList",
            path = "getFlatInfo/{id}",
            httpMethod = ApiMethod.HttpMethod.POST)
    public List<FlatInfo> getFlatInfoList(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        List<FlatInfo> flats = new ArrayList<>();
        UserProfile userProfile = ofy().load().type(UserProfile.class).id(id).now();
        if (userProfile == null) {
            logger.info("No UserProfile exists with ID: " + id);
            return null;
        } else if (userProfile.getNumberOfFlats() == 0) {
            logger.info("No FlatInfo registered for UserProfile with ID: " + id);
            return null;
        } else {
            logger.info("Added a new ExpenseData for FlatInfo with ID: " + id);
            List<Long> flatIds = userProfile.getFlatIds();
            for (Long flatId : flatIds) {
                FlatInfo flatInfo = ofy().load().type(FlatInfo.class).id(flatId).now();
                flats.add(flatInfo);
            }
            return flats;
        }
    }


    /**
     * Get available FlatInfo list.
     */
    @ApiMethod(
            name = "getAvailableFlatInfoList",
            path = "getAvailableFlatInfo",
            httpMethod = ApiMethod.HttpMethod.POST)
    public List<FlatInfo> getAvailableFlatInfoList() {
        return ofy().load().type(FlatInfo.class).filter("available", false).limit(10).list();
    }


    /**
     * Returns the {@link UserProfile} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code UserProfile} with the provided ID.
     */
    @ApiMethod(
            name = "getUserProfileList",
            path = "getUserProfileList/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public List<UserProfile> getUserProfileList(@Named("id") Long id) throws NotFoundException {
        List<UserProfile> profiles = new ArrayList<>();
        List<Long> tempIds = new ArrayList<>();

        UserProfile currentUserProfile = ofy().load().type(UserProfile.class).id(id).now();

        profiles.add(currentUserProfile);
        tempIds.add(currentUserProfile.getId());

        if (currentUserProfile.getNumberOfFlats() == 0) {
            return profiles;
        }
        for (Long flatId : currentUserProfile.getFlatIds()) {
            FlatInfo flatInfo = ofy().load().type(FlatInfo.class).id(flatId).now();

            for (Long userId : flatInfo.getMemberIds()) {
                if (!tempIds.contains(userId)) {
                    UserProfile userProfile = ofy().load().type(UserProfile.class).id(userId).now();
                    profiles.add(userProfile);
                    tempIds.add(userId);
                }
            }
        }
        return profiles;
    }


    /**
     * Returns List of {@code ExpenseGroup} for a given {@code UserProfile}.
     */
    @ApiMethod(
            name = "getExpenseGroupList",
            path = "expenseGroupGetExpenseGroupList",
            httpMethod = ApiMethod.HttpMethod.POST)
    public List<ExpenseGroup> getExpenseGroupList(@Named("id") Long userProfileId) throws NotFoundException {
        checkExists(userProfileId);
        UserProfile userProfile = ofy().load().type(UserProfile.class).id(userProfileId).now();
        List<ExpenseGroup> expenseGroups = new ArrayList<>();
        for (Long expenseGroupId : userProfile.getExpenseGroupIds()) {
            ExpenseGroup group = ofy().load().type(ExpenseGroup.class).id(expenseGroupId).now();
            expenseGroups.add(group);
        }
        return expenseGroups;
    }

    /**
     * Returns List of {@code Request} for a given {@code UserProfile}.
     */
    @ApiMethod(
            name = "getRequestList",
            path = "getRequestList",
            httpMethod = ApiMethod.HttpMethod.POST)
    public List<Request> getRequestList(@Named("id") Long userProfileId) throws NotFoundException {
        checkExists(userProfileId);
        UserProfile userProfile = ofy().load().type(UserProfile.class).id(userProfileId).now();
        List<Request> requests = new ArrayList<>();
        for (Long requestId : userProfile.getRequestIds()) {
            Request request = ofy().load().type(Request.class).id(requestId).now();
            if (request.getStatus().equals("PENDING")) {
                requests.add(request);
            }
        }
        return requests;
    }

    /**
     * Returns List of {@code Contact} for a given {@code FlatInfo}.
     */
    @ApiMethod(
            name = "searchFlatsForRent",
            path = "searchFlatsForRent",
            httpMethod = ApiMethod.HttpMethod.POST)
    public List<FlatInfo> searchFlatsForRent(@Named("latitude") double latitude, @Named("longitude") double longitude) throws NotFoundException {
        List<FlatInfo> flats = new ArrayList<>();
        IndexSpec indexSpec = IndexSpec.newBuilder().setName("FlatInfoIndex").build();
        Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
        try {
            String queryString = "distance(GeoPoint, geopoint(" + latitude + "," + longitude + ")) < 10000";
            Results<ScoredDocument> results = index.search(queryString);

            // Iterate over the documents in the results
            for (ScoredDocument document : results) {
                Long flatId = Long.valueOf(document.getOnlyField("Id").getText());
                FlatInfo flatInfo = ofy().load().type(FlatInfo.class).id(flatId).now();
                flats.add(flatInfo);
            }
        } catch (SearchException e) {
            if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())) {
                // retry
                flats = null;
            }
        }
        return flats;
    }
}