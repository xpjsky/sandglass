package net.xpjsky.common.util;

/**
 * Description Here
 *
 * @author Paddy.Xie
 * @version 8/21/12 10:15 AM
 */
public final class HexUtil {

    private static final char[] HEX_TABLE = new char[] {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'
    };

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(HEX_TABLE[(b & 0xF0) >> 4]);
            sb.append(HEX_TABLE[b & 0x0F]);
        }

        return sb.toString();
    }

    public static byte[] hexToBytes(String hex) {
        if (hex.length() % 2 == 1) {
            throw new RuntimeException("Even char required : " + hex);
        }

        byte[] hexBytes = hex.getBytes();
        byte[] bytes = new byte[hexBytes.length / 2];

        for (int i = 0; i < hex.length(); i += 2) {
            int ti = Integer.parseInt(new String(hexBytes, i, 2), 16);
            bytes[i / 2] = (byte) ti;
        }
        return bytes;
    }

}
