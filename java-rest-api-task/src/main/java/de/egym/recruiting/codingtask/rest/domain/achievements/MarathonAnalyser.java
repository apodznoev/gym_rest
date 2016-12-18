package de.egym.recruiting.codingtask.rest.domain.achievements;

import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import de.egym.recruiting.codingtask.jpa.domain.UserAchievement;

import java.util.List;
import java.util.Optional;

/**
 * Cope with a standard marathon distance
 * </p>
 * Created by apodznoev
 * date 18.12.2016.
 */
public class MarathonAnalyser extends AchievementAnalyser {
    private static final int MARATHON_DISTANCE_METERS = 42_195;

    @Override
    protected void doLoadInitialData() {
        List<Exercise> exercises = exerciseDao.findForUser(user.getId(), Exercise.Type.RUNNING, null, null);
        Optional<Exercise> marathonRun = exercises.stream().filter(MarathonAnalyser::isMarathonRun).findFirst();
        if (marathonRun.isPresent())
            achieved = new UserAchievement(AchievementType.MARATHON, marathonRun.get().getEndTimestamp());
    }

    @Override
    protected void doAnalyseExercise(Exercise exercise) {
        if (isMarathonRun(exercise))
            achieved = new UserAchievement(AchievementType.MARATHON, exercise.getEndTimestamp());
    }

    private static boolean isMarathonRun(Exercise exercise) {
        return exercise.getType() == Exercise.Type.RUNNING && exercise.getDistanceMeters() >= MARATHON_DISTANCE_METERS;
    }
}
