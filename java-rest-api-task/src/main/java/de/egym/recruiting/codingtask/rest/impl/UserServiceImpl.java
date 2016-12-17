package de.egym.recruiting.codingtask.rest.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.egym.recruiting.codingtask.Timing;
import de.egym.recruiting.codingtask.jpa.dao.UserDao;
import de.egym.recruiting.codingtask.jpa.domain.User;
import de.egym.recruiting.codingtask.rest.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Singleton
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    //usually validate the email with regexp is a REALLY bad idea
    private static final Pattern EMAIL_PATTER = Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}");

    private final UserDao userDao;

    @Inject
    UserServiceImpl(final UserDao userDao) {
        this.userDao = userDao;
    }


    @Nonnull
    @Override
    public List<User> indexUsers(@Nullable String lastNamePrefix) {
        log.debug("Get all users having the last name: {}.", lastNamePrefix);
        return userDao.findByLastNamePrefix(lastNamePrefix);
    }

    @Nullable
    @Override
    public User getUserById(@Nonnull final Long userId) {
        log.debug("Get user by id.");
        final User user = userDao.findById(userId);
        if (user == null) {
            throw new NotFoundException("User with such id not found");
        }
        return user;
    }

    @Override
    public User createUser(User newUser) {
        log.debug("Trying to create new user {}.", newUser);
        newUser.setId(null);
        validateUser(newUser);
        newUser = userDao.create(newUser);
        log.debug("New user created, id {}.", newUser.getId());
        return newUser;
    }

    private void validateUser(User newUser) throws BadRequestException {
        if (StringUtils.isEmpty(newUser.getFirstName()))
            throw new BadRequestException("First name is empty");

        if (StringUtils.isEmpty(newUser.getLastName()))
            throw new BadRequestException("Last name is empty");

        if (StringUtils.isEmpty(newUser.getEmail()))
            throw new BadRequestException("Email is empty");

        validateBirthday(newUser.getBirthday());
        validateEmail(newUser.getEmail());
    }

    private void validateEmail(String email) throws BadRequestException {
        if (!EMAIL_PATTER.matcher(email.toUpperCase()).matches())
            throw new BadRequestException("Email cannot have illegal characters and domain must be at least 2 symbols");

        User existing = userDao.findByEmail(email);
        if (existing != null)
            throw new ClientErrorException("Given email already exists", Response.Status.CONFLICT);

    }

    private void validateBirthday(Date birthday) throws BadRequestException {
        if (birthday == null)
            throw new BadRequestException("Birthday is not provided");

        if (birthday.after(new Date(Timing.getMillis())))
            throw new BadRequestException("Looks like this user is not born yet, check the birthday field");
    }
}
