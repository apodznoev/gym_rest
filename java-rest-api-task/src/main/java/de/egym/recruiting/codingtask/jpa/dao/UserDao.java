package de.egym.recruiting.codingtask.jpa.dao;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import de.egym.recruiting.codingtask.jpa.domain.User;

import java.util.List;

public interface UserDao extends BaseDao<User> {

	@Nullable
	User findByEmail(@Nullable String email);

	List<User> findByLastNamePrefix(@Nullable String lastNamePrefix);
}
