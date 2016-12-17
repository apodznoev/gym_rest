package de.egym.recruiting.codingtask.rest.general;

import com.google.inject.AbstractModule;
import de.egym.recruiting.codingtask.rest.ExerciseService;
import de.egym.recruiting.codingtask.rest.UserService;
import de.egym.recruiting.codingtask.rest.impl.ExerciseServiceImpl;
import de.egym.recruiting.codingtask.rest.impl.UserServiceImpl;

public class RestServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(UserService.class).to(UserServiceImpl.class);
		bind(ExerciseService.class).to(ExerciseServiceImpl.class);
		bind(ObjectMapperProvider.class);
		bind(RestExceptionMapper.class);
	}
}
