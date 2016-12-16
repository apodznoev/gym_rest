package de.egym.recruiting.codingtask.rest;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.apache.http.HttpStatus;
import org.junit.Test;

import de.egym.recruiting.codingtask.AbstractRestIntegrationTest;

public class UserServiceRestIntegrationTest extends AbstractRestIntegrationTest {

	@Test
	public void testIndexWithoutFilter() {
		when().get("/api/v1/users").then().statusCode(HttpStatus.SC_OK).body("lastName", hasItems("Bond", "Mueller"));
	}

	@Test
	public void testGetById() {
		when().get("/api/v1/users/1").then().statusCode(HttpStatus.SC_OK).body("lastName", is("Mueller")).body("id", is(1));
	}

	@Test
	public void testGetByIdNotFound() {
		when().get("/api/v1/users/12345").then().statusCode(HttpStatus.SC_NOT_FOUND);
	}
}
