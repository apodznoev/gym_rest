package de.egym.recruiting.codingtask.rest.domain.achievements;

import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import de.egym.recruiting.codingtask.jpa.domain.UserAchievement;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Walk at least 8 hours and reach at least 20 kilometers
 * <p>
 * Created by apodznoev
 * date 18.12.2016.
 */
public class WandererAnalyser extends AchievementAnalyser {
    private static final long MIN_DISTANCE_FOR_WANDERUNG_METERS = 20000;
    private static final long MIN_DURATION_FOR_WANDERUNG_SECS = TimeUnit.HOURS.toSeconds(8);

    @Override
    protected void doLoadInitialData() {
        List<Exercise> exercises = exerciseDao.findForUser(user.getId(), Exercise.Type.WALKING, null, null);
        for (Exercise exercise : exercises) {
            if (match(exercise)) {
                achieved = new UserAchievement(AchievementType.WANDERER, exercise.getEndTimestamp());
                break;
            }
        }
    }

    @Override
    protected void doAnalyseExercise(Exercise exercise) {
        if (match(exercise))
            achieved = new UserAchievement(AchievementType.WANDERER, exercise.getEndTimestamp());
    }

    private static boolean match(Exercise exercise) {
        return exercise.getDistanceMeters() != null
                && exercise.getDistanceMeters() >= MIN_DISTANCE_FOR_WANDERUNG_METERS
                && exercise.getDurationSecs() >= MIN_DURATION_FOR_WANDERUNG_SECS;
    }
}
