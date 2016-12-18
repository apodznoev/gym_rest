package de.egym.recruiting.codingtask.rest.impl;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.egym.recruiting.codingtask.Timing;
import de.egym.recruiting.codingtask.jpa.dao.ExerciseDao;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import de.egym.recruiting.codingtask.jpa.domain.UserAchievement;
import de.egym.recruiting.codingtask.rest.AchievementsService;
import de.egym.recruiting.codingtask.rest.domain.AchievementsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * TODO: write REST service tests
 *
 * Created by apodznoev
 * date 17.12.2016.
 */
@Singleton
public class AchievementsServiceImpl implements AchievementsService {
    private static final Logger log = LoggerFactory.getLogger(AchievementsServiceImpl.class);
    private final ExerciseDao exerciseDao;
    private final AchievementsFactory achievementsFactory;

    //userId -> points for last 4 weeks
    private final Cache<Long, Long> last4WeeksPointsCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .recordStats()
            .ticker(new Ticker() {
                @Override
                public long read() {
                    return TimeUnit.MILLISECONDS.toNanos(Timing.getMillis());
                }
            })
            .build();

    @Inject
    AchievementsServiceImpl(final ExerciseDao exerciseDao) {
        this.exerciseDao = exerciseDao;
        this.achievementsFactory = new AchievementsFactory(exerciseDao);
    }

    @Override
    public List<UserAchievement> getAchievements(long userId) {
        log.debug("Querying achievements for the user: {}", userId);
        return achievementsFactory.getAchievements(userId);
    }

    @Override
    public long calculatePoints(long userId,
                                @Nullable Long fromInclusive,
                                @Nullable Long toExclusive) {
        log.debug("Calculating achievement points for user: {} from: {} to: {}", userId, fromInclusive, toExclusive);
        return calculate(userId,
                fromInclusive == null ? 0 : fromInclusive,
                toExclusive == null ? Timing.getMillis() : toExclusive
        );
    }

    @Override
    public long getLastPoints(long userId) {
        log.debug("Getting last achievement points for user: {}", userId);
        log.debug("Cache stats: {}", last4WeeksPointsCache.stats());
        long now = Timing.getMillis();
        return last4WeeksPointsCache.asMap().computeIfAbsent(userId, id -> calculate(id, now - TimeUnit.DAYS.toMillis(28), now));

    }

    private long calculate(long userId, long from, long to) {
        log.debug("Performing calculation of points user: {}, from: {}, to: {}", userId, from, to);
        List<Exercise> exercises = exerciseDao.findForUser(userId, null, from, to);
        return exercises.stream()
                .mapToLong(exercise ->
                        exercise.getCaloriesBurned() + TimeUnit.SECONDS.toMinutes(exercise.getDurationSecs()))
                .sum();
    }

    public void handleNewExercise(Exercise exercise) {
        log.debug("New exercise: {} for user: {} received clearing cache", exercise.getId(), exercise.getUser().getId());
        //but later we can dynamically merge it with already calculated points instead of just invalidating
        last4WeeksPointsCache.invalidate(exercise.getUser().getId());
        achievementsFactory.consume(exercise);
    }
}
