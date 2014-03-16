package net.xpjsky.common.archive;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 9/25/12 3:29 PM
 */
public class ArchiveException extends RuntimeException {

    public ArchiveException() {
    }

    public ArchiveException(String message) {
        super(message);
    }

    public ArchiveException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArchiveException(Throwable cause) {
        super(cause);
    }
}
