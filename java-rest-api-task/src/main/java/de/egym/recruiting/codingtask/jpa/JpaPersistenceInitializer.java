package de.egym.recruiting.codingtask.jpa;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.Transactional;

@Transactional
class JpaPersistenceInitializer {

	@Inject
	JpaPersistenceInitializer(final PersistService persistenceService) {
		persistenceService.start();
	}
}
