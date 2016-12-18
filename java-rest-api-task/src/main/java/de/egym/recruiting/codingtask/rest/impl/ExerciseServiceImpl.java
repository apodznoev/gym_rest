package de.egym.recruiting.codingtask.rest.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.egym.recruiting.codingtask.jpa.dao.ExerciseDao;
import de.egym.recruiting.codingtask.jpa.dao.UserDao;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import de.egym.recruiting.codingtask.jpa.domain.User;
import de.egym.recruiting.codingtask.rest.ExerciseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by apodznoev
 * date 17.12.2016.
 */
@Singleton
public class ExerciseServiceImpl implements ExerciseService {
    private static final Logger log = LoggerFactory.getLogger(ExerciseServiceImpl.class);

    private final UserDao userDao;
    private final ExerciseDao exerciseDao;
    private final AchievementsServiceImpl achievementsService;

    @Inject
    ExerciseServiceImpl(final UserDao userDao,
                        final ExerciseDao exerciseDao,
                        final AchievementsServiceImpl achievementsService) {
        this.exerciseDao = exerciseDao;
        this.userDao = userDao;
        this.achievementsService = achievementsService;
    }

    @Nonnull
    @Override
    public List<Exercise> indexExercises() {
        log.debug("Listing all the exercises.");
        return exerciseDao.findAll();
    }

    @Nonnull
    @Override
    public List<Exercise> indexExercisesForUser(long userId, @Nullable Exercise.Type type,
                                                @Nullable Long fromInclusive, @Nullable Long tillExclusive) {
        log.debug("Listing all the exercises for user: {}, type:{}, from: {}, till: {}.",
                userId, type, fromInclusive, tillExclusive);

        if (userDao.findById(userId) == null)
            throw new NotFoundException("User with given id not found");

        return exerciseDao.findForUser(userId, type, fromInclusive, tillExclusive);
    }

    @Nullable
    @Override
    public Exercise findExercise(long exerciseId) {
        log.debug("Looking fpr an exercise with id: {}.", exerciseId);
        Exercise exercise = exerciseDao.findById(exerciseId);
        if (exercise == null) {
            throw new NotFoundException("Exercise not found");
        }

        return exercise;
    }

    @Override
    public Exercise createExercise(Exercise newExercise) {
        log.debug("Creating an exercise: {}.", newExercise);
        newExercise.setId(null);
        validateExercise(newExercise);
        Exercise exercise = exerciseDao.create(newExercise);
        achievementsService.handleExercise(exercise);
        return exercise;
    }

    private void validateExercise(Exercise newExercise) throws BadRequestException {
        validateFields(newExercise);
        newExercise.setUser(validateUser(newExercise.getUser()));
        validateExerciseConflicts(newExercise);
    }

    private static void validateFields(Exercise newExercise) throws BadRequestException {
        if (newExercise.getStartTimestamp() <= 0)
            throw new BadRequestException("Start timestamp field is invalid");

        if (newExercise.getDurationSecs() <= 0)
            throw new BadRequestException("Duration field is invalid");

        if (newExercise.getCaloriesBurned() <= 0)
            throw new BadRequestException("Burned calories field is invalid");

        if (newExercise.getType() == null)
            throw new BadRequestException("Type field must be present");
    }

    private User validateUser(User user) throws ClientErrorException {
        if (user == null)
            throw new BadRequestException("User is not specified");

        if (user.getId() == null)
            throw new BadRequestException("User id is not specified");

        long userId = user.getId();
        User found = userDao.findById(userId);
        if (found == null)
            throw new NotFoundException("User with given id not exists: " + userId);

        return found;
    }

    private void validateExerciseConflicts(Exercise newExercise) throws ClientErrorException {
        long end = newExercise.getEndTimestamp();
        List<Exercise> exercisesBeforeGivenEnded = exerciseDao.findForUser(newExercise.getUser().getId(), null, null, end);
        Exercise conflict = exercisesBeforeGivenEnded
                .stream()
                .filter(exercise -> exercise.getEndTimestamp() > newExercise.getStartTimestamp())
                .findAny()
                .orElse(null);

        if (conflict != null)
            throw new ClientErrorException("Exercise overlaps with another exercise, id: " + conflict.getId(),
                    Response.Status.CONFLICT);
    }
}
