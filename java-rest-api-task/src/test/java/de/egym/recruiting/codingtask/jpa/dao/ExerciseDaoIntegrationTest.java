package de.egym.recruiting.codingtask.jpa.dao;

import com.google.inject.Inject;
import de.egym.recruiting.codingtask.*;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import de.egym.recruiting.codingtask.jpa.domain.User;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ExerciseDaoIntegrationTest extends AbstractIntegrationTest {

    @Inject
    private ExerciseDao exerciseDao;

    private static final long TIME_NOW = TimeUnit.HOURS.toMillis(24);

    @BeforeClass
    public static void setUp() throws Exception {
        TestTiming.useTestTime();
        TestTiming.INSTANCE.setTime(TIME_NOW);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        TestTiming.useRealTime();
    }

    @Test
    public void testFindAll() throws Exception {
        List<Exercise> exercises = exerciseDao.findAll();

        assertNotNull(exercises);
        assertEquals(3, exercises.size());
        assertThat(exercises, Matchers.hasItems(
                ExerciseMatcher.copy(TestData.USER_1_EXERCISE_1),
                ExerciseMatcher.copy(TestData.USER_1_EXERCISE_2),
                ExerciseMatcher.copy(TestData.USER_2_EXERCISE_1)
        ));
    }

    @Test
    public void testFindById() {
        assertNull(exerciseDao.findById(4L));
        Exercise found = exerciseDao.findById(TestData.USER_2_EXERCISE_1.getId());
        assertNotNull(found);
        assertThat(TestData.USER_2_EXERCISE_1, ExerciseMatcher.copy(found));
    }

    @Test
    public void testFindForUser() {
        List<Exercise> found = exerciseDao.findForUser(TestData.USER_1.getId(), null, null, null);
        assertEquals(2, found.size());
        assertThat(found, Matchers.hasItems(
                ExerciseMatcher.copy(TestData.USER_1_EXERCISE_1),
                ExerciseMatcher.copy(TestData.USER_1_EXERCISE_2)
        ));

        found = exerciseDao.findForUser(TestData.USER_2.getId(), null, null, null);
        assertEquals(1, found.size());
        assertThat(found, Matchers.hasItems(
                ExerciseMatcher.copy(TestData.USER_2_EXERCISE_1)
        ));

        found = exerciseDao.findForUser(TestData.USER_3.getId(), null, null, null);
        assertTrue(found.isEmpty());
    }

    @Test
    public void testFoundCriteria() throws Exception {
        List<Exercise> found = exerciseDao.findForUser(TestData.USER_1.getId(), Exercise.Type.RUNNING, null, null);
        assertEquals(1, found.size());
        assertThat(found, Matchers.hasItems(ExerciseMatcher.copy(TestData.USER_1_EXERCISE_2)));

        found = exerciseDao.findForUser(TestData.USER_1.getId(), Exercise.Type.OTHER, null, null);
        assertTrue(found.isEmpty());

        found = exerciseDao.findForUser(TestData.USER_1.getId(), null, TIME_NOW, null);
        assertTrue(found.isEmpty());

        found = exerciseDao.findForUser(TestData.USER_1.getId(), null, TIME_NOW - TimeUnit.HOURS.toMillis(1), null);
        assertEquals(1, found.size());
        assertThat(found, Matchers.hasItems(ExerciseMatcher.copy(TestData.USER_1_EXERCISE_2)));

        found = exerciseDao.findForUser(TestData.USER_1.getId(), null, TIME_NOW - TimeUnit.HOURS.toMillis(1) + 1, null);
        assertTrue(found.isEmpty());

        found = exerciseDao.findForUser(TestData.USER_1.getId(), null, TIME_NOW - TimeUnit.HOURS.toMillis(1) - 1, null);
        assertEquals(1, found.size());
        assertThat(found, Matchers.hasItems(ExerciseMatcher.copy(TestData.USER_1_EXERCISE_2)));

        found = exerciseDao.findForUser(TestData.USER_1.getId(), null, TIME_NOW - TimeUnit.HOURS.toMillis(3), null);
        assertEquals(2, found.size());
        assertThat(found, Matchers.hasItems(
                ExerciseMatcher.copy(TestData.USER_1_EXERCISE_1),
                ExerciseMatcher.copy(TestData.USER_1_EXERCISE_2))
        );

        found = exerciseDao.findForUser(TestData.USER_1.getId(), null, TIME_NOW - TimeUnit.HOURS.toMillis(3) + 1, null);
        assertEquals(1, found.size());
        assertThat(found, Matchers.hasItems(
                ExerciseMatcher.copy(TestData.USER_1_EXERCISE_2))
        );

        found = exerciseDao.findForUser(TestData.USER_1.getId(), null, null, TIME_NOW);
        assertEquals(2, found.size());
        assertThat(found, Matchers.hasItems(
                ExerciseMatcher.copy(TestData.USER_1_EXERCISE_1),
                ExerciseMatcher.copy(TestData.USER_1_EXERCISE_2))
        );

        found = exerciseDao.findForUser(TestData.USER_1.getId(), null, null, TIME_NOW - TimeUnit.HOURS.toMillis(1) + 1);
        assertEquals(2, found.size());
        assertThat(found, Matchers.hasItems(
                ExerciseMatcher.copy(TestData.USER_1_EXERCISE_1),
                ExerciseMatcher.copy(TestData.USER_1_EXERCISE_2))
        );

        found = exerciseDao.findForUser(TestData.USER_1.getId(), null, null, TIME_NOW - TimeUnit.HOURS.toMillis(1));
        assertEquals(1, found.size());
        assertThat(found, Matchers.hasItems(
                ExerciseMatcher.copy(TestData.USER_1_EXERCISE_1)));

        found = exerciseDao.findForUser(TestData.USER_1.getId(), null, null, TIME_NOW - TimeUnit.HOURS.toMillis(3));
        assertEquals(0, found.size());

        found = exerciseDao.findForUser(TestData.USER_1.getId(), null,
                TIME_NOW - TimeUnit.HOURS.toMillis(3), TIME_NOW - TimeUnit.HOURS.toMillis(1));
        assertEquals(1, found.size());
        assertThat(found, Matchers.hasItems(ExerciseMatcher.copy(TestData.USER_1_EXERCISE_1)));
    }

    @Test
    public void testCreateExercise() throws Exception {
        Exercise exercise = new Exercise();
        try {
            User user = new User();
            user.setId(TestData.USER_1.getId());
            exercise.setUser(user);
            exercise.setCaloriesBurned(199);
            exercise.setDurationSecs((int) TimeUnit.HOURS.toSeconds(4));
            exercise.setStartTimestamp(Timing.getMillis() - TimeUnit.HOURS.toMillis(2));
            exercise.setType(Exercise.Type.FITNESS_COURSE);
            Exercise created = exerciseDao.create(exercise);
            assertNotNull(created.getId());
            assertEquals(4, exerciseDao.findAll().size());
        } finally {
            exerciseDao.delete(exercise);
        }

        exercise.setId(null);
        User user = new User();
        user.setId(123L);
        exercise.setUser(user);
        TestsHelper.checkException(() -> exerciseDao.create(exercise), PersistenceException.class);
        exerciseDao.delete(exercise);
    }
}
