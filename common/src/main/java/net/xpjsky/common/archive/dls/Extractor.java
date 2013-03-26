package net.xpjsky.sandglass.common.archive.dls;

import java.io.File;
import java.io.InputStream;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 10/24/12 8:09 PM
 */
public interface Extractor extends Extract {
    Extract source(String archiveFile);
    Extract source(File archiveFile);
    Extract source(InputStream archiveStream);
}

