package de.egym.recruiting.codingtask;

import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import de.egym.recruiting.codingtask.jpa.domain.User;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by apodznoev
 * date 17.12.2016.
 */
public class ExerciseMatcher extends TypeSafeMatcher<Exercise> {

    private UserMatcher user;
    private Exercise.Type type;
    private Long startTimestamp;
    private Integer durationSecs;
    private Integer caloriesBurned;
    private Integer distanceMeters;

    private ExerciseMatcher(User user, Exercise.Type type, Long startTimestamp, Integer durationSecs, Integer caloriesBurned, Integer distanceMeters) {
        this.user = UserMatcher.copy(user);
        this.type = type;
        this.startTimestamp = startTimestamp;
        this.durationSecs = durationSecs;
        this.caloriesBurned = caloriesBurned;
        this.distanceMeters = distanceMeters;
    }


    public static ExerciseMatcher copy(Exercise exercise) {
        return new ExerciseMatcher(
                exercise.getUser(), exercise.getType(), exercise.getStartTimestamp(),
                exercise.getDurationSecs(), exercise.getCaloriesBurned(), exercise.getDistanceMeters()
        );
    }

    @Override
    protected boolean matchesSafely(Exercise item) {
        boolean match = true;

        if (user != null)
            match = user.matches(item.getUser());

        if (type != null)
            match = item.getType().equals(type);

        if (startTimestamp != null)
            match &= item.getStartTimestamp() == startTimestamp;

        if (durationSecs != null)
            match &= item.getDurationSecs() == durationSecs;

        if (caloriesBurned != null)
            match &= item.getCaloriesBurned() == caloriesBurned;

        if (distanceMeters != null)
            match &= distanceMeters.equals(item.getDistanceMeters());

        return match;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValueList("", ", ", "", user, type, startTimestamp, durationSecs, caloriesBurned, distanceMeters);
    }
}
