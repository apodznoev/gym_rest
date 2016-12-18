package de.egym.recruiting.codingtask.rest.domain;

import de.egym.recruiting.codingtask.Timing;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import de.egym.recruiting.codingtask.jpa.domain.UserAchievement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by apodznoev
 * date 18.12.2016.
 */
public class TrainingAddictAnalyser extends AchievementAnalyser {
    private static final Logger log = LoggerFactory.getLogger(TrainingAddictAnalyser.class);

    private static final int AMOUNT_DAYS_OF_WEEK_REQUIRED = 4;
    private static final int MIN_EXERCISE_DURATION_SECS = (int) TimeUnit.MINUTES.toSeconds(30);

    private final NavigableMap<DayOfWeek, List<Exercise>> thisWeekExercises = new TreeMap<>(
            (Comparator<DayOfWeek>) (d1, d2) -> Integer.compare(d1.getValue(), d2.getValue())
    );

    private UserAchievement achieved;

    @Override
    public Optional<UserAchievement> loadInitialData() {
        log.debug("Loading initial data for user:{}", user.getId());
        LocalDateTime now = LocalDateTime.now(Timing.getClock());
        LocalDateTime beginOfWeek = now
                .minus(now.getDayOfWeek().getValue(), ChronoUnit.DAYS)
                .minus(now.getHour(), ChronoUnit.HOURS)
                .minus(now.getMinute(), ChronoUnit.MINUTES)
                .minus(now.getSecond(), ChronoUnit.SECONDS)
                .minus(now.getNano(), ChronoUnit.NANOS);
        long timestampStart = beginOfWeek.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        long timestampNow = now.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        List<Exercise> exercises = exerciseDao.findForUser(user.getId(), null, timestampStart, timestampNow);
        thisWeekExercises.putAll(exercises
                .stream()
                .filter(exercise -> exercise.getDurationSecs() >= MIN_EXERCISE_DURATION_SECS)
                .collect(Collectors.groupingBy(TrainingAddictAnalyser::getDayOfWeek))
        );
        return observeAchievement();
    }

    @Override
    public Optional<UserAchievement> analyseExercise(Exercise exercise) {
        log.debug("Analysing exercise: {} for user: {}", exercise.getId(), user.getId());
        DayOfWeek dayOfWeek = getDayOfWeek(exercise);
        thisWeekExercises.merge(dayOfWeek, Collections.singletonList(exercise), (previous, next) -> {
            if (previous.size() == 1) {
                previous = new ArrayList<>(previous);
            }
            previous.addAll(next);
            return previous;
        });

        return observeAchievement();
    }

    @Override
    public Optional<UserAchievement> observeAchievement() {
        log.debug("Analysing exercises user:{}", user.getId());
        evictOutdated();
        if (achieved == null && thisWeekExercises.keySet().size() >= AMOUNT_DAYS_OF_WEEK_REQUIRED) {
            log.debug("User:{} got a new achievement", user.getId());
            List<Exercise> exercises = new ArrayList<>(thisWeekExercises.lastEntry().getValue());
            Collections.sort(exercises, (o1, o2) -> Long.compare(o1.getStartTimestamp(), o2.getStartTimestamp()));
            Exercise mostLateExercise = exercises.get(exercises.size() - 1);
            achieved = new UserAchievement(AchievementType.TRAINING_ADDICT, mostLateExercise.getEndTimestamp());
        }

        return Optional.ofNullable(achieved);
    }

    private void evictOutdated() {
        if (thisWeekExercises.isEmpty())
            return;

        Exercise firstExercise = thisWeekExercises.firstEntry().getValue().get(0);
        LocalDateTime firstExerciseDate = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(firstExercise.getStartTimestamp()), ZoneId.of("UTC")
        );
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDateTime now = LocalDateTime.now(Timing.getClock());
        int weekOfYear = firstExerciseDate.get(weekFields.weekOfWeekBasedYear());

        if (weekOfYear < now.get(weekFields.weekOfWeekBasedYear())) {
            log.debug("Week passed, clearing achievement for user: {}", user.getId());
            thisWeekExercises.clear();
            achieved = null;
        }
    }

    private static DayOfWeek getDayOfWeek(Exercise exercise) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(exercise.getStartTimestamp()), ZoneId.of("UTC")).getDayOfWeek();
    }
}
