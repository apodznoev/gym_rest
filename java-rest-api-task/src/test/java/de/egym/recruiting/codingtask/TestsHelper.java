package de.egym.recruiting.codingtask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by apodznoev
 * date 17.12.2016.
 */
public class TestsHelper {

    public static void checkException(Runnable createUser, Class<? extends Exception> exceptionClass) {
        try {
            createUser.run();
            fail("Exception expected:" + exceptionClass);
        } catch (Exception e) {
            assertEquals("Received:" + e.getMessage(), e.getClass(), exceptionClass);
        }
    }
}
