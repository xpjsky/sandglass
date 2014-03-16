package net.xpjsky.common.archive.dls;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 10/24/12 8:27 PM
 */
public interface BeforeFinish extends Finish{
    BeforeFinish cleanTarget();
    BeforeFinish deleteSource();
}
