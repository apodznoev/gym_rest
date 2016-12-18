package de.egym.recruiting.codingtask.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;

import javax.annotation.Nullable;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by apodznoev
 * date 17.12.2016.
 */
@Path("/api/v1/users/{userId}/achievements")
@Api(value = "Achievements Service")
public interface AchievementsService {


    /**
     * Returns amount of points achieved by given user in a total.
     * Timestamp bound can be passed to include only those exercises which were performed within them.
     *
     * @param userId        id of the user
     * @param fromInclusive timestamp from which to include exercises
     * @param toExclusive   timestamp till which to include exercises
     * @return sum of points
     */
    @GET
    @Path("/calculatePoint")
    @Produces(MediaType.TEXT_PLAIN)
    long calculatePoints(@ApiParam(value = "id of the user")
                         @PathParam("userId") long userId,

                       @Nullable
                         @ApiParam(value = "optional timestamp since which to count the exercises, inclusive")
                         @QueryParam("from") Long fromInclusive,

                       @Nullable
                         @ApiParam(value = "optional timestamp till which to count the exercises, exclusive")
                         @QueryParam("to") Long toExclusive);


    /**
     * Returns amount of points achieved by given user for the last 4 weeks.
     * Timestamp bound can be passed to include only those exercises which were performed within them.
     *
     * @param userId id of the user
     * @return sum of points fro the last 4 weeks
     */
    @GET
    @Path("/lastPoints")
    @Produces(MediaType.TEXT_PLAIN)
    long getLastPoints(@ApiParam(value = "id of the user")
                         @PathParam("userId") long userId);

}
