package util;

import net.xpjsky.common.util.TimeWatch;
import org.junit.Test;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 12/18/12
 */
public class TimeWatchTest {

    @Test
    public void test() {
        TimeWatch watcher = TimeWatch.startWatch();

        watcher.check();

        watcher.check();

        watcher.check();

        watcher.stop();

        watcher.toString();

        watcher.reset();

    }

}
