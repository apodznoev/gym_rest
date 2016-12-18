package de.egym.recruiting.codingtask.rest.domain;

import de.egym.recruiting.codingtask.Timing;
import de.egym.recruiting.codingtask.jpa.dao.ExerciseDao;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import de.egym.recruiting.codingtask.jpa.domain.User;
import de.egym.recruiting.codingtask.rest.domain.achievements.AchievementType;
import de.egym.recruiting.codingtask.rest.domain.achievements.AchievementsFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.Collections;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by apodznoev
 * date 18.12.2016.
 */
public class AchievementFactoryTest {

    @InjectMocks
    private AchievementsFactory achievementsFactory;

    @Mock
    private ExerciseDao exerciseDao;

    @BeforeClass
    public static void setUp() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterClass
    public static void tearDown() throws Exception {
    }

    @Test
    public void testLoadExercisesOnFirstCallForEveryAchievement() throws Exception {
        final Exercise existingExercise = new Exercise();
        User user = new User();
        user.setBirthday(Timing.getMillis() - TimeUnit.DAYS.toMillis(365) * 60);
        user.setId(1L);
        existingExercise.setUser(user);
        existingExercise.setType(Exercise.Type.CYCLING);
        existingExercise.setStartTimestamp(1000);
        existingExercise.setDurationSecs(20);
        existingExercise.setCaloriesBurned(2000);

        final Exercise newExercise = new Exercise();
        user.setId(1L);
        newExercise.setUser(user);
        newExercise.setType(Exercise.Type.OTHER);
        newExercise.setStartTimestamp(5000);
        newExercise.setDurationSecs(10);
        newExercise.setCaloriesBurned(1000);

        when(exerciseDao.findForUser(eq(1L), any(Exercise.Type.class), any(), any())).thenReturn(Collections.singletonList(existingExercise));
        achievementsFactory.consume(newExercise);
        verify(exerciseDao, times(AchievementType.values().length - 1)).findForUser(eq(1L), any(Exercise.Type.class), any(), any());
        verify(exerciseDao, times(1)).findForUser(eq(1L), any(Collection.class), any(), any());

        assertTrue(achievementsFactory.getAchievements(1L).isEmpty());
        assertTrue(achievementsFactory.getAchievements(2L).isEmpty());
        verify(exerciseDao, times(AchievementType.values().length - 1)).findForUser(eq(1L), any(Exercise.Type.class), any(), any());
        verify(exerciseDao, times(1)).findForUser(eq(1L), any(Collection.class), any(), any());

    }
}
