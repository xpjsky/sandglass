package net.xpjsky.common.util;

import java.util.LinkedList;
import java.util.List;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 10/24/12 3:48 PM
 */
public class TimeWatch {

    private String name;

    public static TimeWatch startWatch() {
        return startWatch("Watch of " + Thread.currentThread().getName());
    }

    public static TimeWatch startWatch(String name) {
        return new TimeWatch(name);
    }

    private TimeWatch(String name) {
        startPoint = System.currentTimeMillis();
        stopPoint = 0;
        index = 0;
        watchPoints = new LinkedList<WatchPoint>();

        status = 1;
    }

    /* start and stop point, unit in ms */
    private long startPoint;
    private long stopPoint;

    /* current check point index */
    private int index;

    private List<WatchPoint> watchPoints;

    /* the status of TimeWatch, 0 as  */
    private int status;

    public void check() {
        if (status != 1) return;

        WatchPoint cp = new WatchPoint(index);
        watchPoints.add(cp);
    }

    public void check(String name) {
        if (status != 1) return;

        WatchPoint cp = new WatchPoint(index, name);
        watchPoints.add(cp);
    }

    public void stop() {
        stopPoint = System.currentTimeMillis();
        status = 2;
    }

    public void reset() {
        startPoint = 0;
        stopPoint = 0;
        status = 0;
    }

    @Override
    public String toString() {
        return "TimeWatch " + name + " with " + watchPoints.size() + " watch points";
    }

    private static class WatchPoint {
        String name;
        int index;
        long ms;

        WatchPoint(int index) {
            this.index = index;
            this.name = "Checkpoint - " + index;
        }

        WatchPoint(int index, String name) {
            this.index = index;
            this.name = name;
        }
    }

}
