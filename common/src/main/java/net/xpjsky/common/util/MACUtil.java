package net.xpjsky.sandglass.common.util;

import net.xpjsky.sandglass.common.buffer.Buffer;
import net.xpjsky.sandglass.common.buffer.BufferPool;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 10/10/12 5:18 PM
 */
public class MACUtil {

    public static final String HMAC_SHA256 = "HmacSHA256";
    public static final String HMAC_SHA384 = "HmacSHA384";
    public static final String HMAC_SHA512 = "HmacSHA512";

    public static String encodeHmacSHA256(byte[] content, byte[] key) {
        return encodeHmacSHA(HMAC_SHA256, content, key);
    }

    public static String encodeHmacSHA384(byte[] content, byte[] key) {
        return encodeHmacSHA(HMAC_SHA384, content, key);
    }

    public static String encodeHmacSHA512(byte[] content, byte[] key) {
        return encodeHmacSHA(HMAC_SHA512, content, key);
    }

    public static String encodeHmacSHA256(InputStream is, byte[] key) {
        return encodeHmacSHA(HMAC_SHA256, is, key);
    }

    public static String encodeHmacSHA384(InputStream is, byte[] key) {
        return encodeHmacSHA(HMAC_SHA384, is, key);
    }

    public static String encodeHmacSHA512(InputStream is, byte[] key) {
        return encodeHmacSHA(HMAC_SHA512, is, key);
    }


    public static String encodeHmacSHA(String algorithm, byte[] content, byte[] key) {
        Mac mac = createMac(algorithm, key);
        mac.update(content);
        return HexUtil.bytesToHex(mac.doFinal());
    }

    public static String encodeHmacSHA(String algorithm, InputStream is, byte[] key) {
        Mac mac = createMac(algorithm, key);
        Buffer buffer = BufferPool.get();
        int length;
        try {
            while ((length = is.read(buffer.get())) != -1) {
                mac.update(buffer.get(), 0, length);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        buffer.back();
        return HexUtil.bytesToHex(mac.doFinal());
    }

    public static Mac createMac(String macAlgorithm, byte[] key) {
        try {
            Mac mac = Mac.getInstance(macAlgorithm);
            mac.init(new SecretKeySpec(key, macAlgorithm));
            return mac;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeBase64(byte[] bytes) {
        return new String(Base64.encode(bytes));
    }

    public static byte[] decodeBase64(String string) {
        return Base64.decode(string);
    }

}
