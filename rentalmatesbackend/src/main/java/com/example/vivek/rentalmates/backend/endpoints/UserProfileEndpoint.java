package com.example.vivek.rentalmates.backend.endpoints;

import com.example.vivek.rentalmates.backend.entities.ExpenseData;
import com.example.vivek.rentalmates.backend.entities.FlatInfo;
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

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(UserProfile.class);
    }


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
        Query query = ofy().load().type(UserProfile.class);
        query = query.filter("emailId" + " = ", emailId);
        List<UserProfile> profiles =  query.list();
        UserProfile finalUserProfile;
        if (profiles.size() == 0) {
            logger.info("Created UserProfile.");
            ofy().save().entity(userProfile).now();
            finalUserProfile = ofy().load().entity(userProfile).now();
            finalUserProfile.setCreateProfileResult("NEW_USER_PROFILE");
        } else {
            finalUserProfile = profiles.get(0);
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
        List<UserProfile> userProfileList = new ArrayList<UserProfile>(limit);
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
    public List<UserProfile> queryUserProfiles(@Named("type") String type,@Named("value") String value ) {
        Query query = ofy().load().type(UserProfile.class);
        query = query.filter(type + " = ", value);
        List<UserProfile> profiles =  query.list();
        return profiles;
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
        if (userProfile == null ){
            logger.info("No UserProfile exists with ID: " + id);
            return null;
        } else if (userProfile.getNumberOfFlats()==0) {
            logger.info("No FlatInfo registered for UserProfile with ID: " + id);
            return null;
        }
        else {
            logger.info("Added a new ExpenseData for FlatInfo with ID: " + id);
            List<Long> flatIds = userProfile.getFlatIds();
            for (Long flatId: flatIds) {
                FlatInfo flatInfo = ofy().load().type(FlatInfo.class).id(flatId).now();
                flats.add(flatInfo);
            }
            return flats;
        }
    }
}