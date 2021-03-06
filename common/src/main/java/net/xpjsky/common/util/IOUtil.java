package net.xpjsky.common.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 10/10/12 5:13 PM
 */
public class IOUtil {

    public static int copy(InputStream is, OutputStream os)
            throws IOException {
        return 1;
    }

    public static int copy(InputStream is, OutputStream os, int length) {
        return 1;
    }

    public static void closeQuietly(Closeable... cs) {
        if (cs != null) {
            for (Closeable c : cs) {
                if (c != null) {
                    try {
                        c.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
        }
    }
}
