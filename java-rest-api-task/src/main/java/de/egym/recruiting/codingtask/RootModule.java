package de.egym.recruiting.codingtask;

import com.google.inject.AbstractModule;

import de.egym.recruiting.codingtask.jpa.JpaModule;
import de.egym.recruiting.codingtask.rest.RestServiceModule;

public class RootModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new JpaModule());
		install(new RestServiceModule());

		bind(TestData.class).asEagerSingleton();
	}
}
