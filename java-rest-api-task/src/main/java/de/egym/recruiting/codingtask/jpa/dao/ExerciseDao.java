package de.egym.recruiting.codingtask.jpa.dao;

import de.egym.recruiting.codingtask.jpa.domain.Exercise;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * Created by apodznoev
 * date 17.12.2016.
 */
public interface ExerciseDao extends BaseDao<Exercise> {
    /**
     * Finds all exercises for given users
     *
     * @param userId             id of user
     * @param type               optional type of exercises to filter. If null - no filtering will be applied
     * @param startFromInclusive start of interval to filter exercises by {@link Exercise#getStartTimestamp()}
     * @param startTillExclusive end of interval to filter exercises by {@link Exercise#getStartTimestamp()}
     * @return list of exercises satisfying parameters order in ASC order by {@link Exercise#getStartTimestamp()}
     */
    List<Exercise> findForUser(long userId, @Nullable Exercise.Type type,
                               @Nullable Long startFromInclusive, @Nullable Long startTillExclusive);

    /**
     * Finds all exercises for given users
     *
     * @param userId             id of user
     * @param types              optional collection of exercises types to filter. If empty - no filtering will be applied
     * @param startFromInclusive start of interval to filter exercises by {@link Exercise#getStartTimestamp()}
     * @param startTillExclusive end of interval to filter exercises by {@link Exercise#getStartTimestamp()}
     * @return list of exercises satisfying parameters order in ASC order by {@link Exercise#getStartTimestamp()}
     */
    List<Exercise> findForUser(long userId, @Nonnull Collection<Exercise.Type> types,
                               @Nullable Long startFromInclusive, @Nullable Long startTillExclusive);
}
