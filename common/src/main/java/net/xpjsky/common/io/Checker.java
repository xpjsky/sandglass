package net.xpjsky.sandglass.common.io;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 10/19/12 5:36 PM
 */
public interface Checker<T> {

    public void update(int b);

    public void update(byte[] b, int off, int len);

    public T getValue();

}
