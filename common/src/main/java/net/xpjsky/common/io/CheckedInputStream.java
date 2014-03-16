package net.xpjsky.common.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 10/19/12 5:36 PM
 */
public class CheckedInputStream extends FilterInputStream implements CheckedStream {

    private Checker checker;

    protected CheckedInputStream(InputStream in, Checker checker) {
        super(in);
        this.checker = this.checker;
    }

    @Override
    public int read() throws IOException {
        int i = super.read();
        if (i != -1) {
            checker.update(i);
        }
        return i;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int length = super.read(b, off, len);
        if(length != -1) {
            checker.update(b, 0, length);
        }
        return length;
    }

    public Checker getChecker() {
        return checker;
    }
}
