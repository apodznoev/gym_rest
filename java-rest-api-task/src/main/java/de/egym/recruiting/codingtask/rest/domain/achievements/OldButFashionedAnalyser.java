package de.egym.recruiting.codingtask.rest.domain.achievements;

import de.egym.recruiting.codingtask.Timing;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import de.egym.recruiting.codingtask.jpa.domain.UserAchievement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Be older that 60 years and exercise in average 5 hour per week for a one year
 * <p>
 * Created by apodznoev
 * date 18.12.2016.
 */
public class OldButFashionedAnalyser extends AchievementAnalyser {
    private static final double DAYS_IN_YEAR = 365.242196;
    private static final int MIN_AGE_REQUIRED_YEARS = 60;
    //in average after five hours per week for a year
    private static final int REQUIRED_HOURS_OF_TRAINING = 260;

    private final List<Exercise> allExercises = new ArrayList<>();

    @Override
    protected void doLoadInitialData() {
        if (isUserEligible()) {
            long now = Timing.getMillis();
            int yearsDiff = user.getAge() - MIN_AGE_REQUIRED_YEARS;
            allExercises.addAll(exerciseDao.findForUser(user.getId(), (Exercise.Type) null, now - yearsDiff, now));
            analyse(allExercises);
        }
    }

    @Override
    protected void doAnalyseExercise(Exercise exercise) {
        if (isUserEligible()) {
            allExercises.add(exercise);
            analyse(allExercises);
        }
    }

    private void analyse(List<Exercise> allExercises) {
        long sumSecs = allExercises
                .stream()
                .mapToInt(Exercise::getDurationSecs)
                .sum();

        if (TimeUnit.SECONDS.toHours(sumSecs) >= REQUIRED_HOURS_OF_TRAINING) {
            Collections.sort(allExercises, (o1, o2) -> Long.compare(o1.getStartTimestamp(), o2.getStartTimestamp()));
            Exercise lastExercise = allExercises.get(allExercises.size() - 1);
            achieved = new UserAchievement(AchievementType.OLD_BUT_FASHIONED, lastExercise.getEndTimestamp());
        }

    }

    private boolean isUserEligible() {
        return user.getAge() >= MIN_AGE_REQUIRED_YEARS;
    }
}
