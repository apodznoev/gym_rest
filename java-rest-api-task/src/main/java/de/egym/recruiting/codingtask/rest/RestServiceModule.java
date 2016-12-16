package de.egym.recruiting.codingtask.rest;

import com.google.inject.AbstractModule;

public class RestServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(UserService.class).to(UserServiceImpl.class);
		bind(ObjectMapperProvider.class);
		bind(RestExceptionMapper.class);
	}
}
