package de.egym.recruiting.codingtask.rest.domain.achievements;

import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import de.egym.recruiting.codingtask.jpa.domain.UserAchievement;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Complete a 1 hour training between 23:00 and 5:00
 * <p>
 * Created by apodznoev
 * date 18.12.2016.
 */
public class NightShiftAnalyser extends AchievementAnalyser {
    private final static int hourOfDayStart = 23;
    private final static int hourOfDayEnd = 5;
    private final static int minDurationSecs = (int) TimeUnit.HOURS.toSeconds(1);

    @Override
    protected void doLoadInitialData() {
        List<Exercise> exercises = exerciseDao.findForUser(user.getId(), (Exercise.Type) null, null, null);
        for (int i = exercises.size() - 1; i >= 0; i--) {
            Exercise exercise = exercises.get(0);
            if (match(exercise)) {
                achieved = new UserAchievement(AchievementType.NIGHT_SHIFT, exercise.getEndTimestamp());
                break;
            }
        }
    }

    @Override
    protected void doAnalyseExercise(Exercise exercise) {
        if (match(exercise))
            achieved = new UserAchievement(AchievementType.NIGHT_SHIFT, exercise.getEndTimestamp());
    }

    private static boolean match(Exercise exercise) {
        if (exercise.getDurationSecs() >= minDurationSecs) {
            LocalDateTime startTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(exercise.getStartTimestamp()), ZoneId.systemDefault());
            LocalDateTime endTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(exercise.getEndTimestamp()), ZoneId.systemDefault());
            int hourStart = startTime.get(ChronoField.HOUR_OF_DAY);
            int hourEnd = endTime.get(ChronoField.HOUR_OF_DAY);
            boolean startMatch = hourStart >= hourOfDayStart || hourStart <= hourOfDayEnd;
            boolean endMatch = hourEnd >= hourOfDayStart || hourEnd <= hourOfDayEnd;
            return startMatch && endMatch;
        }

        return false;
    }
}
