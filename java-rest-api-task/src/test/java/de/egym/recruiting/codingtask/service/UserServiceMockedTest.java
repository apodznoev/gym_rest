package de.egym.recruiting.codingtask.service;

import de.egym.recruiting.codingtask.TestTiming;
import de.egym.recruiting.codingtask.Timing;
import de.egym.recruiting.codingtask.jpa.dao.UserDao;
import de.egym.recruiting.codingtask.jpa.domain.User;
import de.egym.recruiting.codingtask.rest.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static de.egym.recruiting.codingtask.TestsHelper.checkException;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
		assertNotNull(returnedUser);
		assertEquals(user, returnedUser);

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

		when(userDao.findByLastNamePrefix(null)).thenReturn(Arrays.asList(user1, user2));

		final List<User> returnedUsers = userService.indexUsers(null);
		assertNotNull(returnedUsers);
		assertFalse(returnedUsers.isEmpty());
		assertEquals(2, returnedUsers.size());
		assertEquals(user1, returnedUsers.get(0));
		assertEquals(user2, returnedUsers.get(1));

		verify(userDao, times(1)).findByLastNamePrefix(null);
	}

	@Test
	public void testIndexUsersWithLastName() {
		final User user1 = new User();
		user1.setId(21L);
		user1.setLastName("Big strong guy");
		user1.setEmail("mocked1@email.com");

		final User user2 = new User();
		user1.setId(22L);
		user1.setLastName("Small strong guy");
		user1.setEmail("mocked2@email.com");

		when(userDao.findByLastNamePrefix("")).thenReturn(Arrays.asList(user1, user2));
		when(userDao.findByLastNamePrefix("AnyStringHere1")).thenReturn(Collections.singletonList(user1));
		when(userDao.findByLastNamePrefix("AnyStringHere2")).thenReturn(Collections.singletonList(user2));

		final List<User> allReturnedUsers = userService.indexUsers("");
		assertNotNull(allReturnedUsers);
		assertFalse(allReturnedUsers.isEmpty());
		assertEquals(2, allReturnedUsers.size());
		assertEquals(user1, allReturnedUsers.get(0));
		assertEquals(user2, allReturnedUsers.get(1));
		verify(userDao, times(1)).findByLastNamePrefix("");

		final List<User> returnedUser1 = userService.indexUsers("AnyStringHere1");
		assertNotNull(returnedUser1);
		assertFalse(returnedUser1.isEmpty());
		assertEquals(1, returnedUser1.size());
		assertEquals(user1, returnedUser1.get(0));
		verify(userDao, times(1)).findByLastNamePrefix("AnyStringHere1");

		final List<User> returnedUser2 = userService.indexUsers("AnyStringHere2");
		assertNotNull(returnedUser2);
		assertFalse(returnedUser2.isEmpty());
		assertEquals(1, returnedUser2.size());
		assertEquals(user2, returnedUser2.get(0));
		verify(userDao, times(1)).findByLastNamePrefix("AnyStringHere2");

		final List<User> noUser = userService.indexUsers("NotExpected");
		assertNotNull(noUser);
		assertTrue(noUser.isEmpty());
		verify(userDao, times(1)).findByLastNamePrefix("NotExpected");
	}

	@Test
	public void testCreateNewUserSuccess() throws Exception {
		final User newUser = new User();
		newUser.setFirstName("Any one");
		newUser.setLastName("Any last name");
		newUser.setEmail("mocked1@email.com");
		newUser.setBirthday(Timing.getMillis());

		when(userDao.findByEmail("mocked1@email.com")).thenReturn(null);
		User userWithId = new User();
		userWithId.setId(13L);
		when(userDao.create(newUser)).thenReturn(userWithId);

		final User created = userService.createUser(newUser);
		assertNotNull(created);
		assertEquals(13L, (long) created.getId());
		verify(userDao, times(1)).findByEmail("mocked1@email.com");
		verify(userDao, times(1)).create(newUser);
	}

	@Test
	public void testCreateNewUserNullFieldsValidation() throws Exception {
		final User newUser = new User();
		newUser.setFirstName("Valid first name");
		newUser.setLastName("Valid last name");
		newUser.setBirthday(Timing.getMillis());
		newUser.setEmail("valid@egym.de");

		when(userDao.create(newUser)).thenReturn(newUser);
		assertEquals(newUser, userService.createUser(newUser));
		verify(userDao, times(1)).create(newUser);

		newUser.setFirstName(null);
		checkException(() -> userService.createUser(newUser), BadRequestException.class);
		newUser.setFirstName("");
		checkException(() -> userService.createUser(newUser), BadRequestException.class);
		newUser.setFirstName("Valid");
		assertEquals(newUser, userService.createUser(newUser));
		verify(userDao, times(2)).create(newUser);

		newUser.setLastName(null);
		checkException(() -> userService.createUser(newUser), BadRequestException.class);
		newUser.setLastName("");
		checkException(() -> userService.createUser(newUser), BadRequestException.class);
		newUser.setLastName("Valid");
		assertEquals(newUser, userService.createUser(newUser));
		verify(userDao, times(3)).create(newUser);

		newUser.setEmail(null);
		checkException(() -> userService.createUser(newUser), BadRequestException.class);
		newUser.setEmail("");
		checkException(() -> userService.createUser(newUser), BadRequestException.class);
		newUser.setEmail("valid@egym.de");
		assertEquals(newUser, userService.createUser(newUser));
		verify(userDao, times(4)).create(newUser);

		newUser.setBirthday(null);
		checkException(() -> userService.createUser(newUser), BadRequestException.class);
	}

	@Test
	public void testCreateNewUserBirthdayValidation() throws Exception {
		final User newUser = new User();
		newUser.setFirstName("Valid first name");
		newUser.setLastName("Valid last name");
		newUser.setEmail("valid@egym.de");
		newUser.setBirthday(Timing.getMillis());

		when(userDao.create(newUser)).thenReturn(newUser);
		assertEquals(newUser, userService.createUser(newUser));
		verify(userDao, times(1)).create(newUser);

		//no lower bound
		newUser.setBirthday(Timing.getMillis() - TimeUnit.DAYS.toMillis(365 * 200));
		assertEquals(newUser, userService.createUser(newUser));
		verify(userDao, times(2)).create(newUser);

		long now = 5000;
		try (TestTiming ignored = TestTiming.useTestTime(now)) {
			newUser.setBirthday(now + 1);
			checkException(() -> userService.createUser(newUser), BadRequestException.class);

			newUser.setBirthday(now);
			assertEquals(newUser, userService.createUser(newUser));
			verify(userDao, times(3)).create(newUser);

			newUser.setBirthday(now - 1);
			assertEquals(newUser, userService.createUser(newUser));
			verify(userDao, times(4)).create(newUser);
		}
	}

	@Test
	public void testCreateUserValidateEmail() throws Exception {
		final User newUser = new User();
		newUser.setFirstName("Valid first name");
		newUser.setLastName("Valid last name");
		newUser.setBirthday(Timing.getMillis());
		newUser.setEmail("valid@egym.de");

		User duplicateUser = new User();
		duplicateUser.setEmail("valid+but_copy@egym.de");
		when(userDao.findByEmail("valid+but_copy@egym.de")).thenReturn(duplicateUser);

		when(userDao.create(newUser)).thenReturn(newUser);
		when(userDao.findByEmail(Mockito.argThat(org.hamcrest.Matchers.not("valid+but_copy@egym.de")))).thenReturn(null);

		assertEquals(newUser, userService.createUser(newUser));
		verify(userDao, times(1)).create(newUser);
		verify(userDao, times(1)).findByEmail("valid@egym.de");

		newUser.setEmail("///@egym.de");
		checkException(() -> userService.createUser(newUser), BadRequestException.class);
		newUser.setEmail("invalid@egym.d");
		checkException(() -> userService.createUser(newUser), BadRequestException.class);
		newUser.setEmail("invalidegym.de");
		checkException(() -> userService.createUser(newUser), BadRequestException.class);
		newUser.setEmail("invalid@egymde");
		checkException(() -> userService.createUser(newUser), BadRequestException.class);
		newUser.setEmail("valid+but_copy@egym.de");
		checkException(() -> userService.createUser(newUser), ClientErrorException.class);
		verify(userDao, times(1)).findByEmail("valid+but_copy@egym.de");
	}

}
