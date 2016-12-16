package de.egym.recruiting.codingtask.jpa.dao;

import java.util.List;

import de.egym.recruiting.codingtask.jpa.domain.AbstractEntity;

public interface BaseDao<T extends AbstractEntity> {

	/**
	 * Retrieves the entity of type clazz and the given id.
	 *
	 * @param id
	 *            The entity id.
	 * @return The entity with the specified id or null if no such entity exists.
	 */
	T findById(Long id);

	/**
	 * Returns a list all entity items.
	 *
	 * @return the list, cannot be null.
	 */
	List<T> findAll();

	/**
	 * Creates a new instance and adds it to the database.
	 *
	 * @param item
	 *            the entity to create. Cannot be null.
	 * @return the created entity.
	 */
	T create(T item);

	/**
	 * Updates a item instance.
	 *
	 * @param item
	 *            the entity to update. Cannot be null.
	 * @return the updated entity.
	 */
	T update(T item);

	/**
	 * Refresh the state of the instance from the database, overwriting changes made to the entity, if any.
	 */
	void refresh(T item);

	/**
	 * Deletes an entity of type clazz with he given id.
	 *
	 * @param id
	 *            The entity id.
	 */
	void deleteById(Long id);

	/**
	 * Deletes the given item from the database.
	 *
	 * @param item
	 *            the item to delete. If null or without an id nothing changes.
	 */
	void delete(T item);
}
