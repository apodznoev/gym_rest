package de.egym.recruiting.codingtask;

/**
 * Created by apodznoev
 * date 17.12.2016.
 */
public class TestTiming implements Timing.TimeProvider, AutoCloseable {
    public static TestTiming INSTANCE = new TestTiming();

    private TestTiming(){}

    private volatile long currentTime;

    public static TestTiming useTestTime() {
        Timing.setTimeProvider(INSTANCE);
        return INSTANCE;
    }

    public static void useRealTime() {
        Timing.setTimeProvider(Timing.DEFAULT);
    }

    public void setTime(long time) {
        INSTANCE.currentTime = time;
    }

    @Override
    public long getTime() {
        return currentTime;
    }

    @Override
    public void close() throws Exception {
        useRealTime();
    }

    @Override
    public String toString() {
        return "Test timing, now is: " + currentTime;
    }
}
