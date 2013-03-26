package net.xpjsky.sandglass.common.archive;

import java.io.File;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 9/25/12 5:06 PM
 */
public interface FileFilter {

    public boolean accept(File file);

}
