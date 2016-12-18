package de.egym.recruiting.codingtask.rest.domain.achievements;

import de.egym.recruiting.codingtask.jpa.dao.ExerciseDao;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import de.egym.recruiting.codingtask.jpa.domain.User;
import de.egym.recruiting.codingtask.jpa.domain.UserAchievement;

import java.util.Optional;

/**
 * Created by apodznoev
 * date 18.12.2016.
 */
public abstract class AchievementAnalyser {
    protected User user;
    protected ExerciseDao exerciseDao;
    protected UserAchievement achieved;

    public void setUser(User user) {
        this.user = user;
    }

    public void setExerciseDao(ExerciseDao exerciseDao) {
        this.exerciseDao = exerciseDao;
    }

    /**
     * Loads and analyse initial data needed to analyse achievement conditions
     *
     * @return optional having achievement if conditions are met
     */
    public Optional<UserAchievement> loadInitialData(){
        doLoadInitialData();
        return observeAchievement();
    }

    protected abstract void doLoadInitialData();

    /**
     * Analyse new exercise possibly generating an achievement if it makes some conditions satisfied
     *
     * @param exercise new exercise
     * @return optional having achievement if conditions are met
     */
    public Optional<UserAchievement> analyseExercise(Exercise exercise){
        if(achieved != null)
            return observeAchievement();

        doAnalyseExercise(exercise);
        return observeAchievement();
    }

    protected abstract void doAnalyseExercise(Exercise exercise);

    /**
     * Checks if consumed exercises combine some achievement
     *
     * @return optional having achievement if conditions are met
     */
    Optional<UserAchievement> observeAchievement(){
        return Optional.ofNullable(achieved);
    }
}
