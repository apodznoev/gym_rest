package de.egym.recruiting.codingtask.jpa;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.UnitOfWork;

/**
 * Overrides Guice implementation to handle when the persistence service is already started.
 *
 * https://stackoverflow.com/questions/17402081/how-to-start-jpa-in-a-guice-quartz-web-application
 */
@Singleton
final class JpaPersistFilter implements Filter {

	private final UnitOfWork unitOfWork;

	private final PersistService persistService;

	@Inject
	JpaPersistFilter(UnitOfWork unitOfWork, PersistService persistService) {
		this.unitOfWork = unitOfWork;
		this.persistService = persistService;
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			persistService.start();
		} catch (IllegalStateException e) {
			// Ignore exception is the persist service was already started
		}
	}

	public void destroy() {
		persistService.stop();
	}

	public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
			throws IOException, ServletException {
		unitOfWork.begin();
		try {
			filterChain.doFilter(servletRequest, servletResponse);
		} finally {
			unitOfWork.end();
		}
	}
}
