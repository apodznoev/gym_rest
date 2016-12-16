package de.egym.recruiting.codingtask;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

public abstract class AbstractIntegrationTest {

	private static final Logger log = LoggerFactory.getLogger(AbstractIntegrationTest.class);

	protected Injector injector = Guice.createInjector(new RootModule());

	@Before
	public void setupInjector() {
		log.debug("Injecting test members.");
		injector.injectMembers(this);
	}

}
