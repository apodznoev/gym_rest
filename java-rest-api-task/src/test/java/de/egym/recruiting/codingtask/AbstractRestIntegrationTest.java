package de.egym.recruiting.codingtask;

import org.junit.BeforeClass;

import com.jayway.restassured.RestAssured;

/**
 * All classes that have the suffix RestIntegrationTest are handled specially by the <code>build.gradle</code> script and are excluded from
 * the normal test execution. They are executed in a special gradle task, which first starts a Jetty server, thus we are able to make real
 * HTTP requests during such tests.
 */
public abstract class AbstractRestIntegrationTest extends AbstractIntegrationTest {

	@BeforeClass
	public static void setupConnection() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8080;
		RestAssured.basePath = "/rest";
	}
}
