package net.xpjsky.sandglass.common.archive.dls;

import net.xpjsky.sandglass.common.archive.EntryFilter;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 10/24/12 8:28 PM
 */
public interface Extract extends BeforeFinish{
    Extract extractTo(String extractPath);
    Extract withFilter(EntryFilter filter);
}
