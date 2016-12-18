package de.egym.recruiting.codingtask.rest.domain.achievements;

import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import de.egym.recruiting.codingtask.jpa.domain.UserAchievement;

import java.util.Arrays;
import java.util.List;

/**
 * Try running, swimming and cycling exercises
 * <p>
 * TODO: better to limit exercises in a one last month
 * Created by apodznoev
 * date 18.12.2016.
 */
public class TriathlonAnalyser extends AchievementAnalyser {
    private boolean running;
    private boolean swimming;
    private boolean cycling;

    @Override
    protected void doLoadInitialData() {
        List<Exercise> exercises = exerciseDao.findForUser(
                user.getId(), Arrays.asList(Exercise.Type.RUNNING, Exercise.Type.SWIMMING, Exercise.Type.CYCLING), null, null
        );
        exercises.forEach(this::processExercise);
        if (completed()) {
            achieved = new UserAchievement(AchievementType.TRIATHLON, exercises.get(exercises.size() - 1).getEndTimestamp());
        }
    }

    @Override
    protected void doAnalyseExercise(Exercise exercise) {
        processExercise(exercise);
        if (completed())
            achieved = new UserAchievement(AchievementType.TRIATHLON, exercise.getEndTimestamp());

    }

    private boolean completed() {
        return running && swimming && cycling;
    }

    private void processExercise(Exercise exercise) {
        switch (exercise.getType()) {
            case RUNNING:
                running = true;
                break;
            case SWIMMING:
                swimming = true;
                break;
            case CYCLING:
                cycling = true;
                break;
        }
    }
}
