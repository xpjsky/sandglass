package net.xpjsky.sandglass.common.archive;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 9/24/12 10:05 AM
 */
public enum ArchiveType {

    GZIP("GZIP",        null,   "gz"),
    BZIP2("BZIP2",      null,   "bz2"),
    DEFLATE("DEFLATE",  null,   "deflate"),

    ZIP("ZIP",          "zip",  null),
    TAR("TAR",          "tar",  null),

    TAR_GZ("TAR.GZ",    "tar",  "gz"),
    TAR_BZ2("TAR.BZ2",  "tar",  "bz2"),
    TAR_XZ("TAR.XZ",    "tar",  "xz");

    private String name;
    private String archiver;
    private String compressor;

    ArchiveType(String name, String archiver, String compressor) {
        this.name = name;
        this.archiver = archiver;
        this.compressor = compressor;
    }

    public static ArchiveType parse(String archiveType) {
        for (ArchiveType type : ArchiveType.values()) {
            if (type.getName().equalsIgnoreCase(archiveType)) {
                return type;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getArchiver() {
        return archiver;
    }

    public String getCompressor() {
        return compressor;
    }

    public String getFilenameExtension() {
        return (archiver == null ? "" : ("." + archiver)) + (compressor == null ? "" : ("." + compressor));
    }

    @Override
    public String toString() {
        return name;
    }
}