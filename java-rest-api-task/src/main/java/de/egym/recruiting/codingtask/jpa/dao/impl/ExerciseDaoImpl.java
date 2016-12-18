package de.egym.recruiting.codingtask.jpa.dao.impl;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import de.egym.recruiting.codingtask.jpa.dao.AbstractBaseDao;
import de.egym.recruiting.codingtask.jpa.dao.ExerciseDao;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;
import java.util.Collections;
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
    public List<Exercise> findForUser(long userId, @Nullable Exercise.Type type,
                                      @Nullable Long startFromInclusive, @Nullable Long startTillExclusive) {
        return findForUser(userId, type == null ? Collections.emptyList() : Collections.singletonList(type), startFromInclusive, startTillExclusive);
    }

    @Override
    public List<Exercise> findForUser(long userId, @Nonnull Collection<Exercise.Type> types,
                                      @Nullable Long startFromInclusive, @Nullable Long startTillExclusive) {
        String querySql = "SELECT e FROM Exercise e WHERE e.user.id = :userId ";
        if (!types.isEmpty())
            querySql += "AND e.type IN :typesList ";
        if (startFromInclusive != null)
            querySql += "AND e.startTimestamp >= :from ";
        if (startTillExclusive != null)
            querySql += "AND e.startTimestamp < :to";

        Query query = getEntityManager().createQuery(querySql + " ORDER BY e.startTimestamp ASC");

        if (!types.isEmpty())
            query.setParameter("typesList", types);
        if (startFromInclusive != null)
            query.setParameter("from", Math.max(0, startFromInclusive));
        if (startTillExclusive != null)
            query.setParameter("to", Math.max(0, startTillExclusive));

        //noinspection unchecked
        return (List<Exercise>)
                query.setParameter("userId", userId)
                        .getResultList();
    }
}
