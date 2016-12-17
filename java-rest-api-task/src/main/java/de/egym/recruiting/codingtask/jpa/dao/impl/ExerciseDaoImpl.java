package de.egym.recruiting.codingtask.jpa.dao.impl;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import de.egym.recruiting.codingtask.jpa.dao.AbstractBaseDao;
import de.egym.recruiting.codingtask.jpa.dao.ExerciseDao;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by apodznoev
 * date 17.12.2016.
 */
@Transactional
public class ExerciseDaoImpl extends AbstractBaseDao<Exercise> implements ExerciseDao {

    @Inject
    public ExerciseDaoImpl(final Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider, Exercise.class);
    }

    @Override
    public List<Exercise> findForUser(long userId, Exercise.Type type, Long startFromInclusive, Long startTillExclusive) {
        String querySql = "SELECT e FROM Exercise e WHERE e.user.id = :userId ";
        if (type != null)
            querySql += "AND e.type = :type ";
        if (startFromInclusive != null)
            querySql += "AND e.startTimestamp >= :from ";
        if (startTillExclusive != null)
            querySql += "AND e.startTimestamp < :to";

        Query query = getEntityManager().createQuery(querySql);

        if (type != null)
            query.setParameter("type", type);
        if (startFromInclusive != null)
            query.setParameter("from", startFromInclusive);
        if (startTillExclusive != null)
            query.setParameter("to", startTillExclusive);

        //noinspection unchecked
        return (List<Exercise>)
                query.setParameter("userId", userId)
                        .getResultList();
    }
}
