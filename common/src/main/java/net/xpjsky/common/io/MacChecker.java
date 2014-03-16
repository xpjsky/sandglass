package net.xpjsky.common.io;


import net.xpjsky.common.util.HexUtil;
import net.xpjsky.common.util.MACUtil;

import javax.crypto.Mac;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 10/19/12 5:47 PM
 */
public class MacChecker implements Checker<String> {

    private Mac mac;

    public MacChecker(String algorithm, byte[] key) {
        mac = MACUtil.createMac(algorithm, key);
    }

    @Override
    public void update(int b) {
        mac.update((byte)b);
    }

    @Override
    public void update(byte[] b, int off, int len) {
        mac.update(b, off, len);
    }

    public String getValue() {
        byte[] bs = mac.doFinal();
        return HexUtil.bytesToHex(bs);
    }

}
