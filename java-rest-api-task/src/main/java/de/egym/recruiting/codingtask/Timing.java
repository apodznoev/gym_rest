package de.egym.recruiting.codingtask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

/**
 * Created by apodznoev
 * date 17.12.2016.
 */
public class Timing {
    private static final Logger log = LoggerFactory.getLogger(Timing.class);

    private static volatile Clock clock = Clock.systemDefaultZone();

    public static void setClock(Clock clock) {
        log.info("Time was changed! New timer is: {}", clock);
        Timing.clock = clock;
    }

    public static long getMillis() {
        return clock.millis();
    }

    public static Clock getClock() {
        return clock;
    }
}
