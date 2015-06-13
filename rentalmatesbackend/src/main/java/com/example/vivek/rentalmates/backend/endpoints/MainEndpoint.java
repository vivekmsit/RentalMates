package com.example.vivek.rentalmates.backend.endpoints;

import com.example.vivek.rentalmates.backend.entities.AggregateData;
import com.example.vivek.rentalmates.backend.entities.ExpenseData;
import com.example.vivek.rentalmates.backend.entities.ExpenseGroup;
import com.example.vivek.rentalmates.backend.entities.FlatInfo;
import com.example.vivek.rentalmates.backend.entities.Request;
import com.example.vivek.rentalmates.backend.entities.UserProfile;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.NotFoundException;
import com.googlecode.objectify.ObjectifyService;

import java.util.logging.Logger;

import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Api(
        name = "mainApi",
        version = "v1",
        resource = "main",
        namespace = @ApiNamespace(
                ownerDomain = "backend.rentalmates.vivek.example.com",
                ownerName = "backend.rentalmates.vivek.example.com",
                packagePath = ""
        )
)

public class MainEndpoint {

    private static final Logger logger = Logger.getLogger(MainEndpoint.class.getName());

    static {
        ObjectifyService.register(FlatInfo.class);
        ObjectifyService.register(ExpenseData.class);
        ObjectifyService.register(ExpenseGroup.class);
        ObjectifyService.register(UserProfile.class);
    }

    /**
     * Returns the {@link AggregateData} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return AggregateData for the corresponding ID
     * @throws NotFoundException if there is no {@code UserProfile} with the provided ID.
     */
    @ApiMethod(
            name = "getAggregateData",
            path = "aggregateData/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public AggregateData getAggregateData(@Named("id") Long id) throws NotFoundException {

        AggregateData data = new AggregateData();

        UserProfile userProfile = ofy().load().type(UserProfile.class).id(id).now();
        if (userProfile == null) {
            throw new NotFoundException("Could not find UserProfile with ID: " + id);
        }

        if (userProfile.getNumberOfFlats() > 0) {
            for (Long flatId : userProfile.getFlatIds()) {
                FlatInfo flatInfo = ofy().load().type(FlatInfo.class).id(flatId).now();
                if (flatInfo.getUpdateCount() > 0) {
                    data.addFlat(flatInfo);
                    flatInfo.resetUpdateCount();
                    ofy().save().entity(flatInfo).now();
                }
            }
        }

        if (userProfile.getNumberOfExpenseGroups() > 0) {
            for (Long expenseGroupId : userProfile.getExpenseGroupIds()) {
                ExpenseGroup expenseGroup = ofy().load().type(ExpenseGroup.class).id(expenseGroupId).now();
                if (expenseGroup.getUpdateCount() > 0) {
                    data.addExpenseGroup(expenseGroup);
                    expenseGroup.resetUpdateCount();
                    ofy().save().entity(expenseGroup).now();
                }
            }
        }

        if (userProfile.getNumberOfRequests() > 0) {
            for (Long requestId : userProfile.getRequestIds()) {
                Request request = ofy().load().type(Request.class).id(requestId).now();
                if (request.getUpdateCount() > 0) {
                    data.addRequest(request);
                    request.resetUpdateCount();
                    ofy().save().entity(request).now();
                }
            }
        }

        return data;
    }
}