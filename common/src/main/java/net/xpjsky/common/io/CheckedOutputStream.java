package net.xpjsky.sandglass.common.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 10/19/12 5:43 PM
 */
public class CheckedOutputStream extends FilterOutputStream {

    private Checker observer;

    public CheckedOutputStream(OutputStream out, Checker observer) {
        super(out);
        this.observer = observer;
    }

    @Override
    public void write(int b) throws IOException {
        super.write(b);
        observer.update(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
        observer.update(b, 0, len);
    }

    public Checker getObserver() {
        return observer;
    }
}
