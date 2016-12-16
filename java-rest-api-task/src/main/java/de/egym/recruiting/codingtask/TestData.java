package de.egym.recruiting.codingtask;

import java.text.ParseException;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import de.egym.recruiting.codingtask.jpa.dao.UserDao;
import de.egym.recruiting.codingtask.jpa.domain.User;

@Transactional
public class TestData {

	private static final Logger log = LoggerFactory.getLogger(TestData.class);

	private final UserDao userDao;

	@Inject
	public TestData(final UserDao userDao) {
		this.userDao = userDao;

		insertTestData();
	}

	public void insertTestData() {
		// For convenience set the default time zone to UTC
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		// Create the test user accounts
		insertTestUsers();
	}

	private void insertTestUsers() {
		log.debug("Inserting test users");
		if (userDao.findByEmail("heinz@egym.de") == null) {
			User testUser1 = new User();
			testUser1.setEmail("heinz@egym.de");
			testUser1.setFirstName("Heinz");
			testUser1.setLastName("Mueller");
			try {
				testUser1.setBirthday(DateUtils.parseDate("1983-02-01", "yyyy-MM-dd"));
			} catch (ParseException e) {
				// ignoring
			}

			userDao.create(testUser1);
		}

		if (userDao.findByEmail("007@mi6.co.uk") == null) {
			User testUser2 = new User();
			testUser2.setEmail("007@mi6.co.uk");
			testUser2.setFirstName("James");
			testUser2.setLastName("Bond");
			try {
				testUser2.setBirthday(DateUtils.parseDate("1968-03-02", "yyyy-MM-dd"));
			} catch (ParseException e) {
				// ignoring
			}

			userDao.create(testUser2);
		}
	}

}
