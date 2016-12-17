package de.egym.recruiting.codingtask.rest;

import de.egym.recruiting.codingtask.AbstractRestIntegrationTest;
import de.egym.recruiting.codingtask.TestData;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class UserServiceRestIntegrationTest extends AbstractRestIntegrationTest {

	@Test
	public void testIndexWithoutFilter() {
		when().get("/api/v1/users").then().statusCode(HttpStatus.SC_OK).body("lastName", hasItems("Bond", "Mueller"));
	}

	@Test
	public void testIndexWithPrefix() {
		when().get("/api/v1/users?lastNamePrefix=bon").then().statusCode(HttpStatus.SC_OK).body("lastName",
				Matchers.allOf(
						Matchers.iterableWithSize(2),
						Matchers.hasItems("Bond", "Bongiovi")
				));

		when().get("/api/v1/users?lastNamePrefix=asdfa").then().statusCode(HttpStatus.SC_OK).body("lastName",
				Matchers.hasSize(0));
	}

	@Test
	public void testGetById() {
		when().get("/api/v1/users/1").then().statusCode(HttpStatus.SC_OK).body("lastName", is("Mueller")).body("id", is(1));
	}

	@Test
	public void testGetByIdNotFound() {
		when().get("/api/v1/users/12345").then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void testCreateUser() {
		Map<String, String> user = new HashMap<>();
		user.put("firstName","testName");
		user.put("lastName","lastLastName");
		user.put("email","fake@fake.com");
		user.put("birthday","1990-05-31");
		given().body(user).header("Content-Type","application/json")
				.when().post("/api/v1/users/").then().statusCode(HttpStatus.SC_OK);
	}

	@Test
	public void testCreateUserDuplicate() {
		Map<String, String> user = new HashMap<>();
		user.put("firstName","testName");
		user.put("lastName","lastLastName");
		user.put("email", TestData.USER_1.getEmail());
		user.put("birthday","1990-05-31");
		given().body(user).header("Content-Type","application/json")
				.when().post("/api/v1/users/").then().statusCode(HttpStatus.SC_CONFLICT);
	}
}
