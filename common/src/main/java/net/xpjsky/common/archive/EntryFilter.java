package net.xpjsky.common.archive;

import org.apache.commons.compress.archivers.ArchiveEntry;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 9/24/12 10:07 AM
 */
public interface EntryFilter {

    public boolean accept(ArchiveEntry entry);

}