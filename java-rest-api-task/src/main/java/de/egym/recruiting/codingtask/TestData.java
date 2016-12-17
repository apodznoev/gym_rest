package de.egym.recruiting.codingtask;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import de.egym.recruiting.codingtask.jpa.dao.UserDao;
import de.egym.recruiting.codingtask.jpa.domain.User;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.TimeZone;

@Transactional
public class TestData {
	public static User USER_1;
	public static User USER_2;
	public static User USER_3;

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
			USER_1 = new User();
			USER_1.setEmail("heinz@egym.de");
			USER_1.setFirstName("Heinz");
			USER_1.setLastName("Mueller");
			try {
				USER_1.setBirthday(DateUtils.parseDate("1983-02-01", "yyyy-MM-dd"));
			} catch (ParseException e) {
				// ignoring
			}

			USER_1 = userDao.create(USER_1);
		}

		if (userDao.findByEmail("007@mi6.co.uk") == null) {
			USER_2 = new User();
			USER_2.setEmail("007@mi6.co.uk");
			USER_2.setFirstName("James");
			USER_2.setLastName("Bond");
			try {
				USER_2.setBirthday(DateUtils.parseDate("1968-03-02", "yyyy-MM-dd"));
			} catch (ParseException e) {
				// ignoring
			}

			USER_2 = userDao.create(USER_2);
		}

		if (userDao.findByEmail("bon@bonjovi.com") == null) {
			USER_3 = new User();
			USER_3.setEmail("bon@bonjovi.com");
			USER_3.setFirstName("John");
			USER_3.setLastName("Bongiovi");
			try {
				USER_3.setBirthday(DateUtils.parseDate("1962-03-02", "yyyy-MM-dd"));
			} catch (ParseException e) {
				// ignoring
			}

			USER_3 = userDao.create(USER_3);
		}
	}

}
