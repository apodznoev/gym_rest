package de.egym.recruiting.codingtask.jpa.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import de.egym.recruiting.codingtask.jpa.domain.User;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.util.Collections;
import java.util.List;

/**
 * For JPA query reference see:
 * https://docs.oracle.com/html/E13946_03/ejb3_langref.html
 */
@Transactional
public class UserDaoImpl extends AbstractBaseDao<User> implements UserDao {

	@Inject
	public UserDaoImpl(final Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider, User.class);
	}

	@Nullable
	@Override
	public User findByEmail(@Nullable String email) {
		if (StringUtils.isEmpty(email)) {
			return null;
		}

		email = email.toLowerCase();

		try {
			return (User) getEntityManager()
					.createQuery("SELECT u FROM User u WHERE LOWER(u.email) = :email")
					.setParameter("email", email)
					.getSingleResult();
		} catch (NoResultException | NonUniqueResultException e) {
			return null;
		}
	}

	@Override
	public List<User> findByLastNamePrefix(@Nullable String lastNamePrefix) {
		if(StringUtils.isEmpty(lastNamePrefix))
			return findAll();

		lastNamePrefix = lastNamePrefix.toLowerCase();

		try {
			//noinspection unchecked
			return (List<User>) getEntityManager()
					.createQuery("SELECT u FROM User u WHERE LOWER(u.lastName) LIKE :lastNamePrefix")
					.setParameter("lastNamePrefix", lastNamePrefix + "%")
					.getResultList();
		} catch (NoResultException | NonUniqueResultException e) {
			return Collections.emptyList();
		}
	}
}
