package de.egym.recruiting.codingtask.rest.domain;

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
    abstract Optional<UserAchievement> loadInitialData();

    /**
     * Analyse new exercise possibly generating an achievement if it makes some conditions satisfied
     *
     * @param exercise new exercise
     * @return optional having achievement if conditions are met
     */
    abstract Optional<UserAchievement> analyseExercise(Exercise exercise);

    /**
     * Checks if consumed exercises combine some achievement
     *
     * @return optional having achievement if conditions are met
     */
    abstract Optional<UserAchievement> observeAchievement();
}
