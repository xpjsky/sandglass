package net.xpjsky.common.buffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 9/6/12 4:51 PM
 */
public class BufferPool {

    private static final Logger logger = LoggerFactory.getLogger(BufferPool.class);

    private static BufferPool defaultPool = new BufferPool(2, 50, 1024 * 10);

    private LinkedList<Buffer> buffers;
    private Lock lock = new ReentrantLock();
    private HashSet<Buffer> pickedBuffer;

    private int count = 0;
    private int hitCount = 0;
    private int misCount = 0;

    private BufferPool(int min, int max, int size) {
        this.min = min;
        this.max = max;
        this.size = size;

        buffers = new LinkedList<Buffer>();
        pickedBuffer = new HashSet<Buffer>();

        scale();
    }

    private int min, max;
    private int size;

//    public static BufferPool defaultPool() {
//        return defaultPool;
//    }

//    public static BufferPool buildPool(int min, int max, int size) {
//        return new BufferPool(min, max, size);
//    }

    private Buffer get0() {
        try {
            lock.lockInterruptibly();

            while (true) {
                if (buffers.size() > 0) {
                    Buffer buffer = buffers.removeFirst();
                    buffer.setPicker(Thread.currentThread().getName());
                    buffer.setPickTime(new Date());
                    pickedBuffer.add(buffer);

                    hitCount++;
                    return buffer;
                } else if (count < max) {
                    scale();
                } else {
                    logger.debug("BufferPool : no more buffer can be used, create temporary buffer");
                    misCount++;
                    return new Buffer(-1, size);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    private void put0(Buffer buffer) {
        try {
            lock.lock();
            if (buffer.getId() == -1) {
                return;
            }
            buffer.setPicker(null);
            buffer.setPickTime(null);
            pickedBuffer.remove(buffer);
            buffers.add(buffer);

        } finally {
            lock.unlock();
        }
    }

    private void scale() {
        if (max <= count) {
            logger.debug("BufferPool have already reached the max size");
            return;
        }
        int toSize = count < min ? min : (count + 10);
        toSize = toSize > max ? max : toSize;

        logger.debug("BufferPool will scale from {} to {}", count, toSize);

        Buffer buffer;
        for (int i = count; i < toSize; i++) {
            buffer = new Buffer(i, size);
            buffers.add(buffer);
        }
        count = toSize;
    }



    public static Buffer get() {
        return defaultPool.get0();
    }

    public static Buffer getNew(int size) {
        return new Buffer(-1, size);
    }

    public static void put(Buffer buffer) {
        defaultPool.put0(buffer);
    }

    public static String hitRatio() {
        return String.valueOf(defaultPool.hitCount) + "/" + (defaultPool.hitCount + defaultPool.misCount);
    }

}
