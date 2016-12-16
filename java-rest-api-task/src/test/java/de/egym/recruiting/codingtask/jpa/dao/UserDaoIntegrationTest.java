package de.egym.recruiting.codingtask.jpa.dao;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Inject;

import de.egym.recruiting.codingtask.AbstractIntegrationTest;
import de.egym.recruiting.codingtask.jpa.domain.User;

public class UserDaoIntegrationTest extends AbstractIntegrationTest {

	@Inject
	private UserDao userDao;

	@Test
	public void testFindByEmail() throws Exception {
		final User user = userDao.findByEmail("heinz@egym.de");

		Assert.assertNotNull(user);
		Assert.assertNotNull(user.getId());
		Assert.assertEquals("heinz@egym.de", user.getEmail());
		Assert.assertEquals("Heinz", user.getFirstName());
		Assert.assertEquals("Mueller", user.getLastName());
		final Date expectedBirthday = DateUtils.parseDate("1983-02-01", "yyyy-MM-dd");
		Assert.assertEquals(expectedBirthday, user.getBirthday());
	}

	@Test
	public void testFindByEmailNotFound() {
		final User user = userDao.findByEmail("notfound@egym.de");
		Assert.assertNull(user);
	}

	@Test
	public void testFindByEmailWithNullInput() {
		final User user = userDao.findByEmail(null);
		Assert.assertNull(user);
	}
}
