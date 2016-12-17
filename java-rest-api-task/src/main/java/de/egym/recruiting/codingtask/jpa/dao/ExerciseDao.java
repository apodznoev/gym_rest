package de.egym.recruiting.codingtask.jpa.dao;

import de.egym.recruiting.codingtask.jpa.domain.Exercise;

import java.util.List;

/**
 * Created by apodznoev
 * date 17.12.2016.
 */
public interface ExerciseDao extends BaseDao<Exercise> {
    List<Exercise> findForUser(long userId, Exercise.Type type, Long startFromInclusive, Long startTillExclusive);
}
