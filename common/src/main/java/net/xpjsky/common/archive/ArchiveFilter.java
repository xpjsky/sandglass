package net.xpjsky.common.archive;

import org.apache.commons.compress.archivers.ArchiveEntry;

/**
 * Description Here
 *
 * @author Paddy
 * @version 12-9-23 下午7:11
 */
public interface ArchiveFilter {

    public boolean accept(ArchiveEntry entry);

}
