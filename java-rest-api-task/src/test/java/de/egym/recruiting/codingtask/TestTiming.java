package de.egym.recruiting.codingtask;

import org.joda.time.DateTimeUtils;

import java.time.Clock;
import java.time.Instant;

/**
 * Created by apodznoev
 * date 17.12.2016.
 */
public class TestTiming implements AutoCloseable {
    private static TestTiming INSTANCE = new TestTiming();

    private TestTiming(){}

    public static void useRealTime() {
        Timing.setClock(Clock.systemDefaultZone());
        DateTimeUtils.setCurrentMillisSystem();
    }

    public static TestTiming useTestTime(long time) {
        Timing.setClock(Clock.fixed(Instant.ofEpochMilli(time), Clock.systemDefaultZone().getZone()));
        DateTimeUtils.setCurrentMillisFixed(time);
        return INSTANCE;
    }

    @Override
    public void close() throws Exception {
        useRealTime();
    }
}
