package net.xpjsky.common.archive;

import net.xpjsky.common.archive.dls.BeforeFinish;
import net.xpjsky.common.archive.dls.Extract;
import net.xpjsky.common.archive.dls.Extractor;
import net.xpjsky.common.archive.dls.Finish;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

import java.io.*;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 9/24/12 10:07 AM
 */
public class ArchiveExtractor implements Extractor {

    private ArchiveType archiveType;
    private File archiveFile;
    private InputStream archiveStream;
    private File extractDir;
    private String extractDirPath;
    private EntryFilter filter;
    private boolean cleanFirst;
    private boolean deleteSource;

    public static Extractor getInstance(String archiveType) {
        ArchiveType type = ArchiveType.parse(archiveType);

        return new ArchiveExtractor(type);
    }

    public static Extractor getInstance(ArchiveType archiveType) {
        return new ArchiveExtractor(archiveType);
    }

    private ArchiveExtractor(ArchiveType archiveType) {
        this.archiveType = archiveType;
    }

    public Extract source(String archiveFile) {
        return source(new File(archiveFile));
    }

    public Extract source(File archiveFile) {
        if (!archiveFile.isFile()) {
            throw new RuntimeException("Archive file " + archiveFile.getAbsolutePath() + " is not exists!");
        }
        try {
            this.archiveFile = archiveFile;
            InputStream archiveStream = new BufferedInputStream(new FileInputStream(this.archiveFile));
            return source(archiveStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Extract source(InputStream archiveStream) {
        if (this.archiveStream != null) {
            try {
                this.archiveStream.close();
            } catch (IOException e) {
                // Do nothing
            }
        }

        this.archiveStream = archiveStream;
        return this;
    }

    public Extract extractTo(String directory) {
        return extractTo(new File(directory));
    }

    public Extract extractTo(File directory) {
        if (directory.exists() && directory.isFile()) {
            throw new RuntimeException(directory.getAbsolutePath() + " is not a directory!");
        }
        this.extractDir = directory;
        this.extractDirPath = directory.getAbsolutePath();
        return this;
    }

    public Extract withFilter(EntryFilter filter) {
        this.filter = filter;
        return this;
    }

    public BeforeFinish deleteSource() {
        this.deleteSource = true;
        return this;
    }

    public BeforeFinish cleanTarget() {
        this.cleanFirst = true;
        return this;
    }

    @Override
    public Finish finish() {

        checkArchive();
        checkExtractDir();
        cleanTarget();

        ArchiveInputStream ais = null;
        try {
            ArchiveStreamFactory factory = new ArchiveStreamFactory();
            if (archiveType != null) {
                if (archiveType.getCompressor() != null) {
                    archiveStream = ArchiveUtil.wrap(archiveStream, archiveType.getCompressor());
                }
                ais = factory.createArchiveInputStream(archiveType.getArchiver(), archiveStream);
            } else {
                ais = factory.createArchiveInputStream(archiveStream);
            }

            ArchiveEntry entry;
            while ((entry = ais.getNextEntry()) != null) {
                if (filter != null && !filter.accept(entry)) {
                    continue;
                }
                writeToFile(entry, ais, extractDirPath);
            }
        } catch (org.apache.commons.compress.archivers.ArchiveException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (ais != null) {
                try {
                    ais.close();
                } catch (IOException e) {
                }
            }
        }

        if (deleteSource && archiveFile != null) {
            archiveFile.delete();
        }

        return this;
    }

    public File finishAndGet() {
        finish();
        return null;
    }

    private void checkArchive() {
        if (archiveFile == null && archiveStream == null) {
            throw new RuntimeException("Neither archive file nor archive stream found !");
        }
    }

    private void checkExtractDir() {
        if (extractDir == null) {
            extractDirPath = ArchiveUtil.generateExtractToDir(archiveFile);
            extractDir = new File(extractDirPath);
            extractDir.mkdirs();
        } else if (!extractDir.exists()) {
            extractDir.mkdirs();
        }
    }

    private void writeToFile(ArchiveEntry entry, InputStream is, String basePath) {
        File file = new File(basePath + File.separator + entry.getName());
        // if the entry is a directory and the directory does not exists on file system yet, then create it
        // if creation failed, throw an ArchiveException
        if (entry.isDirectory() && !file.exists() && !file.mkdirs()) {
            throw new ArchiveException("Creating directory failed : " + file.getAbsolutePath());
        }
        // if the entry is a file, then check and create its parent directory first
        else {
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                throw new ArchiveException("Creating directory failed : " + file.getParent());
            }
            OutputStream fos = null;
            try {
                fos = new BufferedOutputStream(new FileOutputStream(file));
                ArchiveUtil.copy(is, fos);
            } catch (FileNotFoundException e) {
                throw new ArchiveException("Creating file failed : " + file.getAbsolutePath());
            } catch (IOException e) {
                throw new ArchiveException("Transfer file data error : " + e.getMessage());
            } finally {
                ArchiveUtil.closeQuietly(fos);
            }
        }
    }

    private void doCleanFirst() {
        if (cleanFirst) {
            ArchiveUtil.clean(extractDir);
        }
    }


}
