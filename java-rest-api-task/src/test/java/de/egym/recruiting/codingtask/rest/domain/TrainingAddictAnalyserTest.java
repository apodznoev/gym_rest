package de.egym.recruiting.codingtask.rest.domain;

import de.egym.recruiting.codingtask.TestTiming;
import de.egym.recruiting.codingtask.jpa.dao.ExerciseDao;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import de.egym.recruiting.codingtask.jpa.domain.User;
import de.egym.recruiting.codingtask.jpa.domain.UserAchievement;
import de.egym.recruiting.codingtask.rest.domain.achievements.TrainingAddictAnalyser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.DayOfWeek;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by apodznoev
 * date 18.12.2016.
 */
public class TrainingAddictAnalyserTest {
    private static final long NOW_TIME = TimeUnit.DAYS.toMillis(9);//2nd week, 0:00 saturday 1970
    private final Map<DayOfWeek, Exercise> longExercises = new EnumMap<>(DayOfWeek.class);
    private final Map<DayOfWeek, Exercise> shortExercises = new EnumMap<>(DayOfWeek.class);
    @InjectMocks
    private TrainingAddictAnalyser analyser;

    @Mock
    private ExerciseDao exerciseDao;

    @Mock
    private User user;

    @Before
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        TestTiming.useTestTime(NOW_TIME);
        MockitoAnnotations.initMocks(this);
        when(user.getId()).thenReturn(1L);
        for(DayOfWeek dayOfWeek : DayOfWeek.values()) {
            longExercises.put(dayOfWeek, createExercise(dayOfWeek, (int) (30 + Math.random() * 600)));
            shortExercises.put(dayOfWeek, createExercise(dayOfWeek, (int) (30 - Math.random() * 30)));
        }
    }

    @After
    public void tearDown() throws Exception {
        TestTiming.useTestTime(NOW_TIME);
    }


    @Test
    public void testAchievementLessThan4OnStart() throws Exception {
        when(exerciseDao.findForUser(eq(1L), any(Exercise.Type.class), any(), any())).thenReturn(
                Arrays.asList(
                        getLong(DayOfWeek.MONDAY),
                        getLong(DayOfWeek.TUESDAY),
                        getLong(DayOfWeek.FRIDAY)
                ));
        assertNoAchievements(analyser.loadInitialData());
        assertNoAchievements(analyser.observeAchievement());
        verify(exerciseDao, times(1)).findForUser(1L, (Exercise.Type) null, TimeUnit.DAYS.toMillis(3), NOW_TIME);
    }

    @Test
    public void testAchievementLessThan4LongOnStart() throws Exception {
        when(exerciseDao.findForUser(eq(1L), any(Exercise.Type.class), any(), any())).thenReturn(
                Arrays.asList(
                        getLong(DayOfWeek.MONDAY),
                        getLong(DayOfWeek.TUESDAY),
                        getShort(DayOfWeek.THURSDAY),
                        getLong(DayOfWeek.FRIDAY)
                ));
        assertNoAchievements(analyser.loadInitialData());
        assertNoAchievements(analyser.observeAchievement());
        verify(exerciseDao, times(1)).findForUser(1L, (Exercise.Type) null, TimeUnit.DAYS.toMillis(3), NOW_TIME);
    }


    @Test
    public void testExerciseOutOfWeek() throws Exception {
        final Exercise outOfWeek = createExercise(DayOfWeek.MONDAY, 6000);
        outOfWeek.setStartTimestamp(outOfWeek.getStartTimestamp() - TimeUnit.DAYS.toMillis(2));
        when(exerciseDao.findForUser(eq(1L), any(Exercise.Type.class), any(), any())).thenReturn(
                Arrays.asList(
                        outOfWeek,
                        getLong(DayOfWeek.MONDAY),
                        getLong(DayOfWeek.TUESDAY),
                        getLong(DayOfWeek.FRIDAY)
                ));
        when(exerciseDao.findForUser(eq(1L), any(Exercise.Type.class), eq(TimeUnit.DAYS.toMillis(3)), eq(NOW_TIME))).thenReturn(
                Arrays.asList(
                        getLong(DayOfWeek.MONDAY),
                        getLong(DayOfWeek.TUESDAY),
                        getLong(DayOfWeek.FRIDAY)
                ));
        assertNoAchievements(analyser.loadInitialData());
        assertNoAchievements(analyser.observeAchievement());
        verify(exerciseDao, times(1)).findForUser(1L,(Exercise.Type) null, TimeUnit.DAYS.toMillis(3), NOW_TIME);
    }

    @Test
    public void testExerciseInASameDay() throws Exception {
        final Exercise repeat = createExercise(DayOfWeek.MONDAY, 6000);
        when(exerciseDao.findForUser(eq(1L), any(Exercise.Type.class), any(), any())).thenReturn(
                Arrays.asList(
                        repeat,
                        getLong(DayOfWeek.MONDAY),
                        getLong(DayOfWeek.TUESDAY),
                        getLong(DayOfWeek.FRIDAY)
                ));
        assertNoAchievements(analyser.loadInitialData());
        assertNoAchievements(analyser.observeAchievement());
        verify(exerciseDao, times(1)).findForUser(1L,(Exercise.Type) null, TimeUnit.DAYS.toMillis(3), NOW_TIME);
    }

    @Test
    public void testAchievementReached4Days() throws Exception {
        when(exerciseDao.findForUser(eq(1L), any(Exercise.Type.class), any(), any())).thenReturn(
                Arrays.asList(
                        getLong(DayOfWeek.MONDAY),
                        getLong(DayOfWeek.TUESDAY),
                        getLong(DayOfWeek.FRIDAY),
                        getLong(DayOfWeek.SATURDAY)
                        ));
        assertAchievements(analyser.loadInitialData());
        assertAchievements(analyser.observeAchievement());
        verify(exerciseDao, times(1)).findForUser(1L,(Exercise.Type) null, TimeUnit.DAYS.toMillis(3), NOW_TIME);
    }

    @Test
    public void testAchievementReachedVariousDays() throws Exception {
        when(exerciseDao.findForUser(eq(1L), any(Exercise.Type.class), any(), any())).thenReturn(
                Arrays.asList(
                        getLong(DayOfWeek.MONDAY),
                        getLong(DayOfWeek.TUESDAY),
                        getLong(DayOfWeek.TUESDAY),
                        getLong(DayOfWeek.FRIDAY),
                        getLong(DayOfWeek.SATURDAY),
                        getShort(DayOfWeek.SATURDAY)
                ));
        assertAchievements(analyser.loadInitialData());
        assertAchievements(analyser.observeAchievement());
        verify(exerciseDao, times(1)).findForUser(1L,(Exercise.Type) null, TimeUnit.DAYS.toMillis(3), NOW_TIME);
    }

    @Test
    public void testEvictAchievementNextWeek() throws Exception {
        when(exerciseDao.findForUser(eq(1L), any(Exercise.Type.class), any(), any())).thenReturn(
                Arrays.asList(
                        getLong(DayOfWeek.MONDAY),
                        getLong(DayOfWeek.TUESDAY),
                        getLong(DayOfWeek.FRIDAY),
                        getLong(DayOfWeek.SATURDAY)
                        ));
        assertAchievements(analyser.loadInitialData());
        assertAchievements(analyser.observeAchievement());
        verify(exerciseDao, times(1)).findForUser(1L,(Exercise.Type) null, TimeUnit.DAYS.toMillis(3), NOW_TIME);

        TestTiming.useTestTime(NOW_TIME + TimeUnit.DAYS.toMillis(7));
        assertNoAchievements(analyser.observeAchievement());
    }

    @Test
    public void testAchievementsNotReachedOnNewShortExercise() throws Exception {
        when(exerciseDao.findForUser(eq(1L), any(Exercise.Type.class), any(), any())).thenReturn(
                Arrays.asList(
                        getLong(DayOfWeek.TUESDAY),
                        getLong(DayOfWeek.WEDNESDAY),
                        getLong(DayOfWeek.SATURDAY)
                ));
        assertNoAchievements(analyser.loadInitialData());
        assertNoAchievements(analyser.observeAchievement());
        verify(exerciseDao, times(1)).findForUser(1L,(Exercise.Type) null, TimeUnit.DAYS.toMillis(3), NOW_TIME);

        final Exercise newExercise = createExercise(DayOfWeek.SATURDAY, 25);
        assertNoAchievements(analyser.analyseExercise(newExercise));
        assertNoAchievements(analyser.observeAchievement());
    }

    @Test
    public void testAchievementsReachedNewLongExercise() throws Exception {
        when(exerciseDao.findForUser(eq(1L), any(Exercise.Type.class), any(), any())).thenReturn(
                Arrays.asList(
                        getLong(DayOfWeek.MONDAY),
                        getLong(DayOfWeek.TUESDAY),
                        getLong(DayOfWeek.FRIDAY)
                ));
        assertNoAchievements(analyser.loadInitialData());
        assertNoAchievements(analyser.observeAchievement());
        verify(exerciseDao, times(1)).findForUser(1L,(Exercise.Type) null, TimeUnit.DAYS.toMillis(3), NOW_TIME);

        final Exercise newExercise = createExercise(DayOfWeek.SATURDAY, 6000);
        assertAchievements(analyser.analyseExercise(newExercise));
        assertAchievements(analyser.observeAchievement());
    }

    @Test
    public void testAchievementsNotReachedNewLongExerciseSameDay() throws Exception {
        when(exerciseDao.findForUser(eq(1L), any(Exercise.Type.class), any(), any())).thenReturn(
                Arrays.asList(
                        getLong(DayOfWeek.MONDAY),
                        getLong(DayOfWeek.TUESDAY),
                        getLong(DayOfWeek.FRIDAY)
                ));
        assertNoAchievements(analyser.loadInitialData());
        assertNoAchievements(analyser.observeAchievement());
        verify(exerciseDao, times(1)).findForUser(1L,(Exercise.Type) null, TimeUnit.DAYS.toMillis(3), NOW_TIME);

        final Exercise newExercise = createExercise(DayOfWeek.FRIDAY, 6000);
        assertNoAchievements(analyser.analyseExercise(newExercise));
        assertNoAchievements(analyser.observeAchievement());
    }

    private Exercise getLong(DayOfWeek dayOfWeek) {
        return longExercises.get(dayOfWeek);
    }

    private Exercise getShort(DayOfWeek dayOfWeek) {
        return shortExercises.get(dayOfWeek);
    }

    private static void assertAchievements(Optional<UserAchievement> userAchievement) {
        assertTrue(userAchievement.isPresent());
    }

    private static void assertNoAchievements(Optional<UserAchievement> userAchievement) {
        assertFalse(userAchievement.isPresent());
    }

    private Exercise createExercise(DayOfWeek dayOfWeek, int durationMinutes) {
        Exercise exercise = new Exercise();
        exercise.setUser(user);
        long startOfWeekOffset = TimeUnit.DAYS.toMillis(4);
        exercise.setStartTimestamp(startOfWeekOffset  + TimeUnit.DAYS.toMillis(dayOfWeek.getValue() - 1) + TimeUnit.HOURS.toMillis(12));
        exercise.setDurationSecs((int) TimeUnit.MINUTES.toSeconds(durationMinutes));
        return exercise;
    }
}
