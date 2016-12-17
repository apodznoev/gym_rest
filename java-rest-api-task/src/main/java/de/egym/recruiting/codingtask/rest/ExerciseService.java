package de.egym.recruiting.codingtask.rest;

import com.google.inject.persist.Transactional;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by apodznoev
 * date 17.12.2016.
 */
@Transactional
@Path("/api/v1/exercises")
@Api(value = "Exercise Service")
public interface ExerciseService {

    /**
     * This method index all exercises from the database.
     *
     * @return a list of exercise objects.
     */
    @GET
    @Path("/")
    @Nonnull
    @Produces(MediaType.APPLICATION_JSON)
    List<Exercise> indexExercises();

    /**
     * TODO: I decided not to add a parameter 'úserId' because of it has no sense. Exercise id is unique and it's enough.
     * This method lookups the exercise using given exercise id.
     *
     * @return an exercise with given id
     */
    @GET
    @Path("/{exerciseId}")
    @Nullable
    @Produces(MediaType.APPLICATION_JSON)
    Exercise findExercise(@ApiParam(value = "unique identifier of the exercise")
                            @PathParam("exerciseId") long exerciseId);

    /**
     * This method index all exercises for the given user.
     *
     * @return a list of exercise objects which given user completed.
     */
    @GET
    @Path("/user/{userId}")
    @Nonnull
    @Produces(MediaType.APPLICATION_JSON)
    List<Exercise> indexExercisesForUser(@PathParam("userId") long userId,

                                         @Nullable
                                         @ApiParam(value = "optional type of the exercise")
                                         @QueryParam(value = "exerciseType") Exercise.Type exerciseType,

                                         @Nullable
                                         @ApiParam(value = "optional timestamp since which to select the exercises, inclusive")
                                         @QueryParam(value = "from") Long timestampFrom,

                                         @Nullable
                                         @ApiParam(value = "optional timestamp till which to select the exercises, exclusive")
                                         @QueryParam(value = "to") Long timestampTo);

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Exercise createExercise(Exercise newExercise);
}
