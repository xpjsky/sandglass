package net.xpjsky.sandglass.common.util;

import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.*;

/**
 * Descriptions Here
 *
 * @author Paddy
 * @version 7/29/12 10:52 PM
 */
public final class DateUtil {

    private DateUtil(){}

    public static Date truncateTime(Date date) {
        Calendar c = getInstance();
        c.setTime(date);
        c.set(HOUR_OF_DAY, 0);
        c.set(MINUTE, 0);
        c.set(SECOND, 0);

        return c.getTime();
    }

}
