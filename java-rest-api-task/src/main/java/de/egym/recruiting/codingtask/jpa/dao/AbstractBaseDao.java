package de.egym.recruiting.codingtask.jpa.dao;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;

import de.egym.recruiting.codingtask.jpa.domain.AbstractEntity;

public abstract class AbstractBaseDao<T extends AbstractEntity> implements BaseDao<T> {

	private final Logger log = LoggerFactory.getLogger(AbstractBaseDao.class);

	private final Provider<EntityManager> entityManagerProvider;

	private final Class<T> clazz;

	protected AbstractBaseDao(final Provider<EntityManager> entityManagerProvider, final Class<T> clazz) {
		this.clazz = clazz;
		this.entityManagerProvider = entityManagerProvider;
	}

	protected Class<T> getClazz() {
		return clazz;
	}

	protected EntityManager getEntityManager() {
		return entityManagerProvider.get();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findAll() {
		log.debug("Finding all items for type = " + clazz);
		final Query query = getEntityManager().createQuery("SELECT e FROM " + clazz.getName() + " AS e");
		try {
			return query.getResultList();
		} catch (NoResultException e) {
			return Collections.emptyList();
		}
	}

	@Override
	public T findById(final Long id) {
		log.debug("Finding " + clazz + " with id = " + id);
		if (id == null || id == 0L) {
			return null;
		}
		return getEntityManager().find(clazz, id);
	}

	@Override
	public T create(final T item) {
		log.debug("Creating item = " + String.valueOf(item));
		getEntityManager().persist(item);
		return item;
	}

	@Override
	public T update(final T item) {
		log.debug("Updating item = " + String.valueOf(item));
		if (item == null) {
			return null;
		}
		return getEntityManager().merge(item);
	}

	@Override
	public void refresh(final T item) {
		log.debug("Refreshing item = " + String.valueOf(item));
		if (item == null) {
			return;
		}
		getEntityManager().refresh(item);
	}

	@Override
	public void delete(final T item) {
		if (item == null || item.getId() == null) {
			log.warn("Tried to delete a not persisted object: " + item + " of type " + clazz);
			return;
		}
		deleteById(item.getId());
	}

	@Override
	public void deleteById(final Long id) {
		log.debug("Deleting " + clazz + " with id = " + id);
		getEntityManager().remove(getEntityManager().find(clazz, id));
	}
}
