package de.egym.recruiting.codingtask.service;

import de.egym.recruiting.codingtask.TestTiming;
import de.egym.recruiting.codingtask.jpa.dao.ExerciseDao;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import de.egym.recruiting.codingtask.jpa.domain.User;
import de.egym.recruiting.codingtask.rest.impl.AchievementsServiceImpl;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by apodznoev
 * date 18.12.2016.
 */
public class AchievementServiceMockedTest {
    private final static long FOUR_WEEKS = TimeUnit.DAYS.toMillis(28);
    private final static long TIME_NOW = TimeUnit.DAYS.toMillis(60);
    @InjectMocks
    private AchievementsServiceImpl achievementsService;

    @Mock
    private ExerciseDao exerciseDao;

    @BeforeClass
    public static void setUp() throws Exception {
        TestTiming.useTestTime(TIME_NOW);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        TestTiming.useRealTime();
    }

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testNoExercises() {
        assertEquals(0, achievementsService.getLastPoints(1));
        assertEquals(0, achievementsService.calculatePoints(1, null, null));
    }

    @Test
    public void testCalculate() throws Exception {
        final Exercise exercise1 = new Exercise();
        User user = new User();
        user.setId(1L);
        exercise1.setUser(user);
        exercise1.setCaloriesBurned(1000);
        exercise1.setDurationSecs(122);//2 seconds will test rounding

        final Exercise exercise2 = new Exercise();
        exercise2.setUser(user);
        exercise2.setCaloriesBurned(123);
        exercise2.setDurationSecs(60);

        final Exercise exercise3 = new Exercise();
        User user2 = new User();
        user.setId(2L);
        exercise3.setUser(user2);
        exercise3.setCaloriesBurned(99999);
        exercise3.setDurationSecs(99999);

        when(exerciseDao.findForUser(eq(1L), any(Exercise.Type.class), any(), any())).thenReturn(Arrays.asList(exercise1, exercise2));
        when(exerciseDao.findForUser(eq(2L), any(Exercise.Type.class), any(), any())).thenReturn(Collections.singletonList(exercise3));

        long points = achievementsService.calculatePoints(1L, null, null);
        assertEquals(1000 + 2 + 123 + 1, points);
        verify(exerciseDao, times(1)).findForUser(1L, (Exercise.Type) null, 0L, TIME_NOW);

        points = achievementsService.getLastPoints(1L);
        assertEquals(1000 + 2 + 123 + 1, points);
        verify(exerciseDao, times(1)).findForUser(1L, (Exercise.Type) null, TIME_NOW - FOUR_WEEKS, TIME_NOW);
    }

    @Test
    public void testCaching() throws Exception {
        final Exercise exercise1 = new Exercise();
        User user = new User();
        user.setId(1L);
        exercise1.setStartTimestamp(TIME_NOW - FOUR_WEEKS + 1);
        exercise1.setUser(user);
        exercise1.setCaloriesBurned(999);
        exercise1.setDurationSecs(45);

        when(exerciseDao.findForUser(eq(1L), any(Exercise.Type.class), any(), any())).thenReturn(Collections.singletonList(exercise1));
        long points = achievementsService.getLastPoints(1L);
        assertEquals(999, points);
        verify(exerciseDao, times(1)).findForUser(1L, (Exercise.Type) null, TIME_NOW - FOUR_WEEKS, TIME_NOW);

        points = achievementsService.getLastPoints(1L);
        assertEquals(999, points);
        verify(exerciseDao, times(1)).findForUser(1L, (Exercise.Type) null, TIME_NOW - FOUR_WEEKS, TIME_NOW);

        try {
            long fiveMinutes = TimeUnit.MINUTES.toMillis(5);
            TestTiming.useTestTime(TIME_NOW + fiveMinutes);
            points = achievementsService.getLastPoints(1L);
            assertEquals(999, points);
            //cache must be expired after 5 minutes
            verify(exerciseDao, times(1)).findForUser(1L, (Exercise.Type) null, TIME_NOW - FOUR_WEEKS + fiveMinutes, TIME_NOW + fiveMinutes);
        } finally {
            TestTiming.useTestTime(TIME_NOW);
        }
    }

    @Test
    public void testIntervalFetching() throws Exception {
        final Exercise exercise1 = new Exercise();
        User user = new User();
        user.setId(1L);
        exercise1.setStartTimestamp(TIME_NOW - FOUR_WEEKS - 1);
        exercise1.setUser(user);
        exercise1.setCaloriesBurned(1000);
        exercise1.setDurationSecs(90);

        final Exercise exercise2 = new Exercise();
        exercise2.setStartTimestamp(TIME_NOW - FOUR_WEEKS + 1);
        exercise2.setUser(user);
        exercise2.setCaloriesBurned(123);
        exercise2.setDurationSecs(60);

        final Exercise exercise3 = new Exercise();
        exercise3.setStartTimestamp(TIME_NOW - TimeUnit.DAYS.toMillis(14));
        exercise3.setUser(user);
        exercise3.setCaloriesBurned(240);
        exercise3.setDurationSecs(600);

        when(exerciseDao.findForUser(
                eq(1L),
                any(Exercise.Type.class),
                AdditionalMatchers.leq(TIME_NOW - FOUR_WEEKS - 1),
                eq(TIME_NOW - TimeUnit.DAYS.toMillis(14)))
        ).thenReturn(Arrays.asList(exercise1, exercise2));

        when(exerciseDao.findForUser(
                eq(1L),
                any(Exercise.Type.class),
                AdditionalMatchers.leq(TIME_NOW - FOUR_WEEKS - 1),
                AdditionalMatchers.geq(TIME_NOW - TimeUnit.DAYS.toMillis(14) + 1))
        ).thenReturn(Arrays.asList(exercise1, exercise2, exercise3));

        when(exerciseDao.findForUser(
                eq(1L),
                any(Exercise.Type.class),
                AdditionalMatchers.leq(TIME_NOW - FOUR_WEEKS + 1),
                AdditionalMatchers.geq(TIME_NOW - TimeUnit.DAYS.toMillis(14) + 1))
        ).thenReturn(Arrays.asList(exercise2, exercise3));

        when(exerciseDao.findForUser(
                eq(1L),
                any(Exercise.Type.class),
                AdditionalMatchers.leq(TIME_NOW - TimeUnit.DAYS.toMillis(14)),
                AdditionalMatchers.geq(TIME_NOW - TimeUnit.DAYS.toMillis(14) + 1))
        ).thenReturn(Collections.singletonList(exercise3));

        when(exerciseDao.findForUser(
                eq(1L),
                any(Exercise.Type.class),
                AdditionalMatchers.leq(TIME_NOW - FOUR_WEEKS - 1),
                AdditionalMatchers.geq(TIME_NOW - TimeUnit.DAYS.toMillis(14) + 1))
        ).thenReturn(Arrays.asList(exercise1, exercise2, exercise3));

        when(exerciseDao.findForUser(
                eq(1L),
                any(Exercise.Type.class),
                eq(TIME_NOW - FOUR_WEEKS),
                AdditionalMatchers.geq(TIME_NOW))
        ).thenReturn(Arrays.asList(exercise2, exercise3));

        long points = achievementsService.getLastPoints(1L);
        assertEquals(240 + 10 + 123 + 1, points);
        verify(exerciseDao, times(1)).findForUser(1L, (Exercise.Type) null, TIME_NOW - FOUR_WEEKS, TIME_NOW);

        //check caching
        points = achievementsService.getLastPoints(1L);
        assertEquals(240 + 10 + 123 + 1, points);
        verify(exerciseDao, times(1)).findForUser(eq(1L), any((Exercise.Type.class)), any(), any());

        points = achievementsService.calculatePoints(1L, TIME_NOW - TimeUnit.DAYS.toMillis(14), TIME_NOW - 1000);
        assertEquals(240 + 10, points);
        verify(exerciseDao, times(1)).findForUser(1L, (Exercise.Type) null, TIME_NOW - TimeUnit.DAYS.toMillis(14), TIME_NOW - 1000);

        points = achievementsService.calculatePoints(1L, TIME_NOW - FOUR_WEEKS - 1, TIME_NOW - TimeUnit.DAYS.toMillis(14));
        assertEquals(1000 + 1 + 123 + 1, points);
        verify(exerciseDao, times(1)).findForUser(1L, (Exercise.Type) null, TIME_NOW - FOUR_WEEKS - 1, TIME_NOW - TimeUnit.DAYS.toMillis(14));
    }
}
