package de.egym.recruiting.codingtask.rest;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.google.inject.persist.Transactional;

import com.google.inject.servlet.RequestParameters;
import de.egym.recruiting.codingtask.jpa.domain.User;
import io.swagger.annotations.Api;

@Transactional
@Path("/api/v1/users")
@Api(value = "User Service")
public interface UserService {

    /**
     * This method index users from the database.
     *
     * @param lastNamePrefix optional parameter, can be used to return only those users whose
     *                       {@link User#getLastName()} starts from the given prefix
     * @return a list of user objects.
     */
    @GET
    @Path("/")
    @Nonnull
    @Produces(MediaType.APPLICATION_JSON)
    List<User> indexUsers(@Nullable @QueryParam(value = "lastNamePrefix") String lastNamePrefix);

    /**
     * This method gets a single user from the database based on the users's id.
     *
     * @param userId the database user id. If a user with such id is not found in the database then an HTTP 404 error is returned.
     * @return a single user object.
     */
    @GET
    @Path("/{userId}")
    @Nullable
    @Produces(MediaType.APPLICATION_JSON)
    User getUserById(@Nonnull @PathParam("userId") Long userId);
}
