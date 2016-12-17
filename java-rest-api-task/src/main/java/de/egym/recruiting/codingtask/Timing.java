package de.egym.recruiting.codingtask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by apodznoev
 * date 17.12.2016.
 */
public class Timing {
    private static final Logger log = LoggerFactory.getLogger(Timing.class);

    static final TimeProvider DEFAULT = new TimeProvider() {
        @Override
        public long getTime() {
            return System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return "Real timing, now:" + System.currentTimeMillis();
        }
    };

    private static volatile TimeProvider provider = DEFAULT;

    public static void setTimeProvider(TimeProvider provider) {
        log.info("Time was changed! New time is: {}", provider);
        Timing.provider = provider;
    }

    public static long getMillis() {
        return provider.getTime();
    }

    public interface TimeProvider {

        long getTime();
    }
}
