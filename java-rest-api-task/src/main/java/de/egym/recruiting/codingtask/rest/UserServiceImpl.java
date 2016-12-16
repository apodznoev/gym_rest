package de.egym.recruiting.codingtask.rest;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.hsqldb.lib.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.egym.recruiting.codingtask.jpa.dao.UserDao;
import de.egym.recruiting.codingtask.jpa.domain.User;

@Singleton
public class UserServiceImpl implements UserService {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	private final UserDao userDao;

	@Inject
	UserServiceImpl(final UserDao userDao) {
		this.userDao = userDao;
	}


	@Nonnull
	@Override
	public List<User> indexUsers(@Nullable String lastNamePrefix) {
		log.debug("Get all users having the last name: {}", lastNamePrefix);
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
}
