package net.xpjsky.common.io;

import net.xpjsky.common.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLockInterruptionException;
import java.nio.channels.OverlappingFileLockException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description Here
 *
 * @author Paddy
 */
public class FileLock implements Lock {

    private final static ConcurrentMap<String, FileLock> fileLockList = new ConcurrentHashMap<String, FileLock>();

    private File lockFile;

    // process lock : for cross process locking
    private java.nio.channels.FileLock processLock;
    private FileChannel channel;

    // thread lock : for cross lock, just used in process
    private ReentrantLock threadLock;

    private FileLock(File lockFile, boolean fair) {
        this.lockFile = lockFile;
        this.threadLock = new ReentrantLock(fair);
    }

    public static FileLock create(File lockFile) {
        return create(lockFile, false);
    }

    public static FileLock create(File lockFile, boolean fair) {
        if (lockFile == null) {
            throw new NullPointerException();
        }

        try {
            lockFile = lockFile.getCanonicalFile();

            String key = lockFile.getPath();

            //check whether the file lock
            FileLock lock = fileLockList.get(key);
            if (lock == null) {
                synchronized (FileLock.class) {
                    lock = fileLockList.get(key);
                    if (lock == null) {
                        checkLockFile(lockFile);

                        lock = new FileLock(lockFile, fair);

                        fileLockList.put(key, lock);
                    }
                }
            }
            return lock;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void lock() {
        threadLock.lock();

        // null means not lock yet
        while (processLock == null) {
            try {
                processLock = openChannel().lock();
            }
            // OverlappingFileLockException should be impossible
            catch (OverlappingFileLockException e) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    // ignore
                }
            }
            // if any other exception or error thrown, then we need to unlock the threadLock
            catch (Throwable t) {
                threadLock.unlock();
                throw new RuntimeException(t);
            }
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        threadLock.lockInterruptibly();

        while (processLock == null) {
            try {
                processLock = openChannel().lock();
            }
            // OverlappingFileLockException should be impossible
            catch (FileLockInterruptionException e) {
                threadLock.unlock();
                throw new InterruptedException(e.getMessage());
            }
            // if any other Exception or Error thrown, then we need to unlock the threadLock
            catch (Throwable t) {
                threadLock.unlock();
                throw new RuntimeException(t);
            }
        }
    }

    @Override
    public boolean tryLock() {
        if (threadLock.tryLock()) {
            try {
                // null means not lock yet
                if (processLock == null) {
                    processLock = openChannel().tryLock();

                    // null mean lock failed
                    if (processLock == null) {
                        threadLock.unlock();
                        return false;
                    }
                }
            } catch (Throwable t) {
                threadLock.unlock();
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        if (threadLock.tryLock(time, unit)) {
            try {
                // null means not lock yet
                if (processLock == null) {
                    processLock = openChannel().tryLock();

                    // null mean lock failed
                    if (processLock == null) {
                        threadLock.unlock();
                        return false;
                    }
                }
            } catch (Throwable t) {
                threadLock.unlock();
                return false;
            }
        }
        return false;
    }

    @Override
    public void unlock() {
        if(!threadLock.isHeldByCurrentThread()) {
            throw new IllegalMonitorStateException();
        }
        try {
            if (processLock != null) {
                processLock.release();
                channel.close();
            }
        } catch (Exception e) {
            // ignore
        } finally {
            processLock = null;
            channel = null;
            threadLock.unlock();
        }
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

    public FileChannel getChannel() {
        if(threadLock.isHeldByCurrentThread()) {
            return channel;
        }

        return null;
    }

    private static void checkLockFile(File lockFile) {
        // not exists and create failed
        if (!FileUtil.createFile(lockFile)) {
            throw new RuntimeException("LockFile " + lockFile.getPath() + " can't be created");
        } else if (lockFile.isDirectory()) {
            throw new RuntimeException("LockFile should not be a folder");
        }
    }

    private FileChannel openChannel() {
        if (channel == null || (!channel.isOpen())) {
            try {
                channel = new RandomAccessFile(lockFile, "rw").getChannel();
            } catch (FileNotFoundException e) {
                // should be impossible, anyway we still catch and throw it
                throw new RuntimeException(e);
            }
        }

        return channel;
    }

//    public static void main(String[] args) {
//        for(int i = 0; i < 10; i++) {
//            final int finalI = i;
//            new Thread() {
//                @Override
//                public void run() {
//
//                    FileLock lock = FileLock.create(new File("/Users/Paddy/Workspace/test.lock"));
//                    lock.lock();
//                    System.out.println("Thread - " + finalI + " get the lock");
//
//                    try {
//                        Thread.sleep(1000 * 5);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    lock.unlock();
//                    System.out.println("Thread - " + finalI + " release the lock");
//                }
//            }.start();
//        }
//    }
}
