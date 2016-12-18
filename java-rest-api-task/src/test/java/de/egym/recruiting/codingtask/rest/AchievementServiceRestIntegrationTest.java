package de.egym.recruiting.codingtask.rest;

import de.egym.recruiting.codingtask.AbstractRestIntegrationTest;
import de.egym.recruiting.codingtask.TestData;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

public class AchievementServiceRestIntegrationTest extends AbstractRestIntegrationTest {

    @Test
    public void testIndexOkNoAchievement() {
        when().get("/api/v1/users/1/achievements").then().statusCode(HttpStatus.SC_OK)
                .body("", Matchers.hasSize(0));

        when().get("/api/v1/users/2/achievements").then().statusCode(HttpStatus.SC_OK)
                .body("", Matchers.hasSize(0));

        when().get("/api/v1/users/3/achievements").then().statusCode(HttpStatus.SC_OK)
                .body("", Matchers.hasSize(0));

        //TODO: better to get NotFound if user not found, but it'l be a big overhead in service.
        //TODO: or possibly return 404 when achievements are empty also
        when().get("/api/v1/users/234/achievements").then().statusCode(HttpStatus.SC_OK)
                .body("", Matchers.hasSize(0));
    }

    @Test
    public void testLastPoints() {
        String points1 = String.valueOf(TestData.USER_1_EXERCISE_1.getCaloriesBurned() + TestData.USER_1_EXERCISE_1.getDurationSecs() / 60
                + TestData.USER_1_EXERCISE_2.getCaloriesBurned() + TestData.USER_1_EXERCISE_2.getDurationSecs() / 60);
        when().get("/api/v1/users/1/achievements/lastPoints").then().statusCode(HttpStatus.SC_OK).body(is(points1));

        String points2 = String.valueOf(TestData.USER_2_EXERCISE_1.getCaloriesBurned() + TestData.USER_2_EXERCISE_1.getDurationSecs() / 60);
        when().get("/api/v1/users/2/achievements/lastPoints").then().statusCode(HttpStatus.SC_OK).body(is(points2));

        when().get("/api/v1/users/3/achievements/lastPoints").then().statusCode(HttpStatus.SC_OK).body(is("0"));

        when().get("/api/v1/users/223/achievements/lastPoints").then().statusCode(HttpStatus.SC_OK).body(is("0"));
    }

    @Test
    public void testCalculatePoints() {
        //bad idea, will cause troubles in case if rest tests are running more than a hour
        long end = TestData.USER_1_EXERCISE_2.getStartTimestamp() - TimeUnit.HOURS.toMillis(1);
        String points1 = String.valueOf(TestData.USER_1_EXERCISE_1.getCaloriesBurned() + TestData.USER_1_EXERCISE_1.getDurationSecs() / 60);
        when().get("/api/v1/users/1/achievements/calculatePoints?to=" + end)
                .then().statusCode(HttpStatus.SC_OK).body(is(points1));
    }


}
