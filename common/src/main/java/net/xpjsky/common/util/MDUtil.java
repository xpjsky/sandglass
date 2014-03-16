package net.xpjsky.common.util;

import net.xpjsky.common.buffer.Buffer;
import net.xpjsky.common.buffer.BufferPool;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 10/10/12 5:18 PM
 */
public class MDUtil {

    public static final String SHA256 = "SHA-256";
    public static final String SHA384 = "SHA-384";
    public static final String SHA512 = "SHA-512";

    public static String encodeSHA256(byte[] content) {
        return encodeSHA(SHA256, content);
    }

    public static String encodeSHA384(byte[] content) {
        return encodeSHA(SHA384, content);
    }

    public static String encodeSHA512(byte[] content) {
        return encodeSHA(SHA512, content);
    }

    public static String encodeSHA256(InputStream is) {
        return encodeSHA(SHA256, is);
    }

    public static String encodeSHA384(InputStream is) {
        return encodeSHA(SHA384, is);
    }

    public static String encodeSHA512(InputStream is) {
        return encodeSHA(SHA512, is);
    }

    public static String encodeSHA(String algorithm, byte[] content) {
        MessageDigest md = createMD(algorithm);
        return HexUtil.bytesToHex(md.digest(content));
    }

    public static String encodeSHA(String algorithm, InputStream is) {
        Buffer buffer = BufferPool.get();
        int length;
        try {
            MessageDigest md = createMD(algorithm);
            while((length = is.read(buffer.get())) != -1) {
                md.update(buffer.get(), 0, length);
            }
            return HexUtil.bytesToHex(md.digest());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static MessageDigest createMD(String mdAlgorithm) {
        try {
            return MessageDigest.getInstance(mdAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
