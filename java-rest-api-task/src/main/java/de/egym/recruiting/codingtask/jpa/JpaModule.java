package de.egym.recruiting.codingtask.jpa;

import com.google.inject.Singleton;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;

import de.egym.recruiting.codingtask.jpa.dao.UserDao;
import de.egym.recruiting.codingtask.jpa.dao.UserDaoImpl;

public class JpaModule extends ServletModule {

	/**
	 * The name of the JPA persistence unit, must be the same as in the persistence.xml.
	 */
	private static final String PERSISTENCE_UNIT_NAME = "hsqldb";

	@Override
	protected void configureServlets() {
		super.configureServlets();

		install(new JpaPersistModule(PERSISTENCE_UNIT_NAME));
		filter("/*").through(JpaPersistFilter.class);
		bind(JpaPersistenceInitializer.class).asEagerSingleton();

		bind(UserDao.class).to(UserDaoImpl.class).in(Singleton.class);
	}
}
