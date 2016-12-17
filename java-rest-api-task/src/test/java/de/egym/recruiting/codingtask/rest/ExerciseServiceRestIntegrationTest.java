package de.egym.recruiting.codingtask.rest;

import de.egym.recruiting.codingtask.AbstractRestIntegrationTest;
import de.egym.recruiting.codingtask.TestData;
import de.egym.recruiting.codingtask.Timing;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

public class ExerciseServiceRestIntegrationTest extends AbstractRestIntegrationTest {

    @Test
    public void testIndex() {
        when().get("/api/v1/exercises").then().statusCode(HttpStatus.SC_OK)
                .body("caloriesBurned",
                                hasItems(
                                        TestData.USER_1_EXERCISE_1.getCaloriesBurned(),
                                        TestData.USER_1_EXERCISE_2.getCaloriesBurned(),
                                        TestData.USER_2_EXERCISE_1.getCaloriesBurned())
                        );
    }

    @Test
    public void testGetById() {
        when().get("/api/v1/exercises/1").then().statusCode(HttpStatus.SC_OK)
                .body("caloriesBurned", is(TestData.USER_1_EXERCISE_1.getCaloriesBurned()))
                .body("id", is(1))
                .body("user.email", is(TestData.USER_1.getEmail()));
    }

    @Test
    public void testGetByIdNotFound() {
        when().get("/api/v1/exercises/12345").then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void testGetForUser() {
        when().get("/api/v1/exercises/user/1").then().statusCode(HttpStatus.SC_OK)
                .body("caloriesBurned",
                        Matchers.allOf(iterableWithSize(2), hasItems(
                                TestData.USER_1_EXERCISE_1.getCaloriesBurned(),
                                TestData.USER_1_EXERCISE_2.getCaloriesBurned())
                        ));

        when().get("/api/v1/exercises/user/3").then().statusCode(HttpStatus.SC_OK)
                .body("", Matchers.hasSize(0));

        when().get("/api/v1/exercises/user/333").then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void testCreateOk() {
        Map<String, Object> exercise = new HashMap<>();
        exercise.put("user", 2);
        exercise.put("type", Exercise.Type.CIRCUIT_TRAINING);
        exercise.put("startTimestamp", Timing.getMillis());
        exercise.put("durationSecs", 123);
        exercise.put("caloriesBurned", 123);
        given().body(exercise).header("Content-Type", "application/json")
                .when().post("/api/v1/exercises").then().statusCode(HttpStatus.SC_OK)
                .body("user.email", is(TestData.USER_2.getEmail()));
    }

    @Test
    public void testCreateExerciseValidationError() {
        Map<String, Object> exercise = new HashMap<>();
        exercise.put("user", 2);
        exercise.put("startTimestamp", Timing.getMillis());
        exercise.put("durationSecs", 123);
        exercise.put("caloriesBurned", 123);
        exercise.put("distanceMeters", 123);
        given().body(exercise).header("Content-Type", "application/json")
                .when().post("/api/v1/exercises").then().statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void testCreateExerciseUserNotFoundError() {
        Map<String, Object> exercise = new HashMap<>();
        exercise.put("user", 123);
        exercise.put("type", Exercise.Type.CIRCUIT_TRAINING.name());
        exercise.put("startTimestamp", Timing.getMillis());
        exercise.put("durationSecs", 123);
        exercise.put("caloriesBurned", 123);
        exercise.put("distanceMeters", 123);
        given().body(exercise).header("Content-Type", "application/json")
                .when().post("/api/v1/exercises").then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void testCreateExerciseOverlapError() {
        Map<String, Object> exercise = new HashMap<>();
        exercise.put("user", 2);
        exercise.put("type", Exercise.Type.CIRCUIT_TRAINING.name());
        exercise.put("startTimestamp", TestData.USER_2_EXERCISE_1.getStartTimestamp());
        exercise.put("durationSecs", 123);
        exercise.put("caloriesBurned", 123);
        exercise.put("distanceMeters", 123);
        given().body(exercise).header("Content-Type", "application/json")
                .when().post("/api/v1/exercises").then().statusCode(HttpStatus.SC_CONFLICT);
    }
}
