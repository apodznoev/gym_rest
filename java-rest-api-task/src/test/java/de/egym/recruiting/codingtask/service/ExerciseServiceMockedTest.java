package de.egym.recruiting.codingtask.service;

import de.egym.recruiting.codingtask.TestsHelper;
import de.egym.recruiting.codingtask.jpa.dao.ExerciseDao;
import de.egym.recruiting.codingtask.jpa.dao.UserDao;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import de.egym.recruiting.codingtask.jpa.domain.User;
import de.egym.recruiting.codingtask.rest.impl.AchievementsServiceImpl;
import de.egym.recruiting.codingtask.rest.impl.ExerciseServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by apodznoev
 * date 17.12.2016.
 */
public class ExerciseServiceMockedTest {
    @InjectMocks
    private ExerciseServiceImpl exerciseService;

    @Mock
    private AchievementsServiceImpl achievementsService;

    @Mock
    private UserDao userDao;

    @Mock
    private ExerciseDao exerciseDao;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testGetExerciseById() {
        final Exercise exercise = new Exercise();
        exercise.setId(1L);

        when(exerciseDao.findById(1L)).thenReturn(exercise);

        final Exercise returnedExercise = exerciseService.findExercise(1L);
        assertNotNull(returnedExercise);
        assertEquals(exercise, returnedExercise);

        verify(exerciseDao, times(1)).findById(1L);
    }

    @Test
    public void testIndexExercises() {
        final Exercise exercise1 = new Exercise();
        exercise1.setId(21L);
        exercise1.setType(Exercise.Type.RUNNING);

        final Exercise exercise2 = new Exercise();
        exercise2.setId(22L);
        exercise2.setType(Exercise.Type.CIRCUIT_TRAINING);

        when(exerciseDao.findAll()).thenReturn(Arrays.asList(exercise1, exercise2));

        final List<Exercise> returnedExercises = exerciseService.indexExercises();
        assertNotNull(returnedExercises);
        assertFalse(returnedExercises.isEmpty());
        assertEquals(2, returnedExercises.size());
        assertEquals(exercise1, returnedExercises.get(0));
        assertEquals(exercise2, returnedExercises.get(1));

        verify(exerciseDao, times(1)).findAll();
    }

    @Test
    public void testFindExerciseForUser() {
        final User user1 = new User();
        user1.setId(1L);

        final Exercise exercise = new Exercise();
        exercise.setId(1L);
        exercise.setType(Exercise.Type.OTHER);

        when(userDao.findById(1L)).thenReturn(user1);
        when(userDao.findById(2L)).thenReturn(null);
        when(exerciseDao.findForUser(eq(1L), any(), any(), any())).thenReturn(Collections.singletonList(exercise));

        final List<Exercise> exercises = exerciseService.indexExercisesForUser(1L, null, null, null);
        assertNotNull(exercises);
        assertEquals(1, exercises.size());
        assertEquals(exercise, exercises.get(0));
        verify(exerciseDao, times(1)).findForUser(1L, null, null, null);
        verify(userDao, times(1)).findById(1L);

        TestsHelper.checkException(() -> exerciseService.indexExercisesForUser(2L, null, null, null), NotFoundException.class);
        verify(userDao, times(1)).findById(2L);
    }

    @Test
    public void testCreateExerciseForUserBasicValidation() {
        final Exercise newExercise = new Exercise();
        User user = new User();
        user.setId(1L);
        newExercise.setUser(user);
        newExercise.setType(Exercise.Type.OTHER);
        newExercise.setStartTimestamp(1000);
        newExercise.setDurationSecs(10);
        newExercise.setCaloriesBurned(1000);

        when(userDao.findById(1L)).thenReturn(user);
        when(exerciseDao.create(newExercise)).thenReturn(newExercise);
        Exercise created = exerciseService.createExercise(newExercise);
        assertEquals(newExercise, created);

        verify(exerciseDao, times(1)).create(newExercise);
        verify(userDao, times(1)).findById(1L);

        newExercise.setCaloriesBurned(0);
        TestsHelper.checkException(() -> exerciseService.createExercise(newExercise), BadRequestException.class);
        newExercise.setCaloriesBurned(-1);
        TestsHelper.checkException(() -> exerciseService.createExercise(newExercise), BadRequestException.class);

        newExercise.setCaloriesBurned(1);
        newExercise.setDurationSecs(0);
        TestsHelper.checkException(() -> exerciseService.createExercise(newExercise), BadRequestException.class);
        newExercise.setDurationSecs(-1);
        TestsHelper.checkException(() -> exerciseService.createExercise(newExercise), BadRequestException.class);

        newExercise.setDurationSecs(10);
        newExercise.setStartTimestamp(-1);
        TestsHelper.checkException(() -> exerciseService.createExercise(newExercise), BadRequestException.class);

        newExercise.setStartTimestamp(1100);
        newExercise.setType(null);
        TestsHelper.checkException(() -> exerciseService.createExercise(newExercise), BadRequestException.class);

        newExercise.setType(Exercise.Type.OTHER);
        newExercise.setUser(null);
        TestsHelper.checkException(() -> exerciseService.createExercise(newExercise), BadRequestException.class);

        newExercise.setUser(user);
        user.setId(null);
        TestsHelper.checkException(() -> exerciseService.createExercise(newExercise), BadRequestException.class);

        when(userDao.findById(2L)).thenReturn(null);
        user.setId(2L);
        TestsHelper.checkException(() -> exerciseService.createExercise(newExercise), NotFoundException.class);
    }

    @Test
    public void testCreateExerciseOverlapValidation() {
        final User user1 = new User();
        user1.setId(1L);

        final Exercise existingExercise = new Exercise();
        existingExercise.setId(1L);
        existingExercise.setStartTimestamp(100_000);
        existingExercise.setDurationSecs(50);

        when(exerciseDao.findForUser(eq(1L), any(), AdditionalMatchers.leq(100_000L), isNull(Long.class))).thenReturn(Collections.singletonList(existingExercise));
        when(exerciseDao.findForUser(eq(1L), any(), AdditionalMatchers.geq(100_001L), isNull(Long.class))).thenReturn(Collections.emptyList());
        when(exerciseDao.findForUser(eq(1L), any(), isNull(Long.class), AdditionalMatchers.leq(100_000L))).thenReturn(Collections.emptyList());
        when(exerciseDao.findForUser(eq(1L), any(), isNull(Long.class), AdditionalMatchers.geq(100_001L))).thenReturn(Collections.singletonList(existingExercise));
        when(userDao.findById(1L)).thenReturn(user1);
        when(userDao.findById(2L)).thenReturn(null);

        final Exercise newExercise = new Exercise();
        newExercise.setUser(user1);
        newExercise.setType(Exercise.Type.OTHER);
        newExercise.setStartTimestamp(94001);
        newExercise.setCaloriesBurned(123);
        newExercise.setDurationSecs(6);

        TestsHelper.checkException(() -> exerciseService.createExercise(newExercise), ClientErrorException.class);
        verify(userDao, times(1)).findById(1L);
        verify(exerciseDao, times(1)).findForUser(eq(1L), any(), anyLong(), anyLong());

        newExercise.setStartTimestamp(94000);
        exerciseService.createExercise(newExercise);
        verify(userDao, times(2)).findById(1L);
        verify(exerciseDao, times(2)).findForUser(eq(1L), any(), anyLong(), anyLong());
        verify(exerciseDao, times(1)).create(newExercise);

        newExercise.setStartTimestamp(150_000 - 1);
        TestsHelper.checkException(() -> exerciseService.createExercise(newExercise), ClientErrorException.class);
        verify(userDao, times(3)).findById(1L);
        verify(exerciseDao, times(3)).findForUser(eq(1L), any(), anyLong(), anyLong());

        newExercise.setStartTimestamp(150_000);
        exerciseService.createExercise(newExercise);
        verify(userDao, times(4)).findById(1L);
        verify(exerciseDao, times(4)).findForUser(eq(1L), any(), anyLong(), anyLong());
        verify(exerciseDao, times(2)).create(newExercise);
    }
}
