package de.egym.recruiting.codingtask;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import de.egym.recruiting.codingtask.jpa.dao.ExerciseDao;
import de.egym.recruiting.codingtask.jpa.dao.UserDao;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import de.egym.recruiting.codingtask.jpa.domain.User;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Transactional
public class TestData {
	public static User USER_1;
	public static Exercise USER_1_EXERCISE_1;
	public static Exercise USER_1_EXERCISE_2;

	public static User USER_2;
	public static Exercise USER_2_EXERCISE_1;

	public static User USER_3;

	private static final Logger log = LoggerFactory.getLogger(TestData.class);

	private final UserDao userDao;
	private final ExerciseDao exerciseDao;

	@Inject
	public TestData(final UserDao userDao, final ExerciseDao exerciseDao) {
		this.userDao = userDao;
		this.exerciseDao = exerciseDao;

		insertTestData();
	}

	private void insertTestData() {
		// For convenience set the default time zone to UTC
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		// Create the test user accounts
		insertTestUsers();
		initExercises();
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

	private void initExercises() {
		log.debug("Inserting exercises");
		if (USER_1_EXERCISE_1 == null) {
			USER_1_EXERCISE_1 = new Exercise();
			USER_1_EXERCISE_1.setUser(USER_1);
			USER_1_EXERCISE_1.setCaloriesBurned(733);
			USER_1_EXERCISE_1.setDurationSecs((int) TimeUnit.HOURS.toSeconds(3));
			USER_1_EXERCISE_1.setStartTimestamp(Timing.getMillis() - TimeUnit.HOURS.toMillis(3));
			USER_1_EXERCISE_1.setType(Exercise.Type.STRENGTH_TRAINING);

			USER_1_EXERCISE_1 = exerciseDao.create(USER_1_EXERCISE_1);
		}

		if(USER_1_EXERCISE_2 == null) {
			USER_1_EXERCISE_2 = new Exercise();
			USER_1_EXERCISE_2.setUser(USER_1);
			USER_1_EXERCISE_2.setCaloriesBurned(899);
			USER_1_EXERCISE_2.setDurationSecs((int) TimeUnit.HOURS.toSeconds(1));
			USER_1_EXERCISE_2.setStartTimestamp(Timing.getMillis() - TimeUnit.HOURS.toMillis(1));
			USER_1_EXERCISE_2.setType(Exercise.Type.RUNNING);
			USER_1_EXERCISE_2.setDistanceMeters(11235);

			USER_1_EXERCISE_2 = exerciseDao.create(USER_1_EXERCISE_2);
		}

		if(USER_2_EXERCISE_1 == null) {
			USER_2_EXERCISE_1 = new Exercise();
			USER_2_EXERCISE_1.setUser(USER_2);
			USER_2_EXERCISE_1.setCaloriesBurned(221);
			USER_2_EXERCISE_1.setDurationSecs((int) TimeUnit.MINUTES.toSeconds(32));
			USER_2_EXERCISE_1.setStartTimestamp(Timing.getMillis() - TimeUnit.MINUTES.toMillis(32));
			USER_2_EXERCISE_1.setType(Exercise.Type.CIRCUIT_TRAINING);

			USER_2_EXERCISE_1 = exerciseDao.create(USER_2_EXERCISE_1);
		}
	}

}
