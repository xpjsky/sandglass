package net.xpjsky.sandglass.common.util;

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
        checkPoints = new LinkedList<CheckPoint>();

        status = 1;
    }

    /* start and stop point, unit in ms */
    private long startPoint;
    private long stopPoint;

    /* current check point index */
    private int index;

    private List<CheckPoint> checkPoints;

    /* the status of TimeWatch, 0 as  */
    private int status;

    public void check() {
        if (status != 1) return;

        CheckPoint cp = new CheckPoint(index);
        checkPoints.add(cp);
    }

    public void check(String name) {
        if (status != 1) return;

        CheckPoint cp = new CheckPoint(index, name);
        checkPoints.add(cp);
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
        return "TimeWatch " + name + " with " + checkPoints.size() + " check points";
    }

    private static class CheckPoint {
        String name;
        int index;
        long ms;

        CheckPoint(int index) {
            this.index = index;
            this.name = "Checkpoint - " + index;
        }

        CheckPoint(int index, String name) {
            this.index = index;
            this.name = name;
        }
    }

}
