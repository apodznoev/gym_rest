package de.egym.recruiting.codingtask.rest;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.egym.recruiting.codingtask.jpa.dao.UserDao;
import de.egym.recruiting.codingtask.jpa.domain.User;

public class UserServiceMockedTest {

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private UserDao userDao;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}


	@Test
	public void testGetUserById() {
		final User user = new User();
		user.setId(23L);
		user.setEmail("mocked@email.com");

		when(userDao.findById(23L)).thenReturn(user);

		final User returnedUser = userService.getUserById(23L);
		Assert.assertNotNull(returnedUser);
		Assert.assertEquals(user, returnedUser);

		verify(userDao, times(1)).findById(23L);
	}

	@Test
	public void testIndexUsers() {
		final User user1 = new User();
		user1.setId(21L);
		user1.setEmail("mocked1@email.com");

		final User user2 = new User();
		user1.setId(22L);
		user1.setEmail("mocked2@email.com");

		when(userDao.findAll()).thenReturn(Arrays.asList(user1, user2));

		final List<User> returnedUsers = userService.indexUsers();
		Assert.assertNotNull(returnedUsers);
		Assert.assertFalse(returnedUsers.isEmpty());
		Assert.assertEquals(2, returnedUsers.size());
		Assert.assertEquals(user1, returnedUsers.get(0));
		Assert.assertEquals(user2, returnedUsers.get(1));

		verify(userDao, times(1)).findAll();
	}
}
