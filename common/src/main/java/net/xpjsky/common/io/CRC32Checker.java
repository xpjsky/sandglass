package net.xpjsky.common.io;

import java.util.zip.CRC32;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 12/18/12
 */
public class CRC32Checker implements Checker<Long> {

    private CRC32 crc32 = new CRC32();

    @Override
    public void update(int b) {
        crc32.update(b);
    }

    @Override
    public void update(byte[] b, int off, int len) {
        crc32.update(b, off, len);
    }

    @Override
    public Long getValue() {
        return crc32.getValue();
    }
}
