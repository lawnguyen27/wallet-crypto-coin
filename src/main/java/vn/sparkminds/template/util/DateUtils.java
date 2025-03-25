package vn.sparkminds.template.util;

import java.time.Clock;
import java.time.Instant;

public final class DateUtils {

    private DateUtils() {}

    public static final Clock CLOCK = Clock.systemUTC();

    public static Instant getCurrentInstant() {
        return Instant.now(CLOCK);
    }

}
