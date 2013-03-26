package net.xpjsky.sandglass.common.archive;

import net.xpjsky.sandglass.common.buffer.Buffer;
import net.xpjsky.sandglass.common.buffer.BufferPool;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 9/24/12 10:08 AM
 */
public class ArchiveUtil {

    private static final Logger logger = LoggerFactory.getLogger(ArchiveUtil.class);

    public static InputStream wrap(InputStream archiveStream, String compressor) throws IOException {
        if ("gz".equals(compressor)) {
            archiveStream = new GzipCompressorInputStream(archiveStream);
        } else if ("bz2".equals(compressor)) {
            archiveStream = new BZip2CompressorInputStream(archiveStream);
        } else if ("xz".equals(compressor)) {
            archiveStream = new XZCompressorInputStream(archiveStream);
        } else if ("deflate".equals(compressor)) {
            archiveStream = new InflaterInputStream(archiveStream);
        } else {
            throw new RuntimeException("Unknown compression type!");
        }
        return archiveStream;
    }

    public static OutputStream wrap(OutputStream os, String compressor) throws IOException {
        if ("gz".equals(compressor)) {
            os = new GzipCompressorOutputStream(os);
        } else if ("bz2".equals(compressor)) {
            os = new BZip2CompressorOutputStream(os);
        } else if ("xz".equals(compressor)) {
            os = new XZCompressorOutputStream(os);
        } else if ("deflate".equals(compressor)) {
            os = new DeflaterOutputStream(os);
        } else {
            throw new RuntimeException("Unknown compression type!");
        }
        return os;
    }

    public static String generateExtractToDir(File sourceFile) {
        String sourceFileName = sourceFile.getName();

        String dirName;
        String[] np = sourceFileName.split("\\.");
        if (np.length >= 3 && "tar".equals(np[np.length - 2])) {
            dirName = sourceFileName.substring(0, sourceFileName.lastIndexOf(".tar." + np[np.length - 1]));
        } else if (np.length >= 2) {
            dirName = sourceFileName.substring(0, sourceFileName.lastIndexOf("." + np[np.length - 1]));
        } else {
            dirName = sourceFileName;
        }
        return sourceFile.getParentFile().getAbsolutePath() + File.separator + dirName;
    }

    public static void writeToFile(ArchiveEntry entry, InputStream is, String basePath) {
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
                copy(is, fos);
            } catch (FileNotFoundException e) {
                throw new ArchiveException("Creating file failed : " + file.getAbsolutePath());
            } catch (IOException e) {
                throw new ArchiveException("Transfer file data error : " + e.getMessage());
            } finally {
                closeQuietly(fos);
            }
        }
    }

    public static void writeToEntry(ArchiveOutputStream aos, File file, String entryName)
            throws IOException {
        ArchiveEntry entry = null;
        try {
            entry = aos.createArchiveEntry(file, entryName);
            aos.putArchiveEntry(entry);
            logger.info("\t archiving {} to {}", file.getAbsolutePath(), entryName);
        } catch (IOException e) {
            throw new ArchiveException("Put archive entry error : ", e);
        }
        InputStream fis = null;
        try {
            if (file.isDirectory()) {
                aos.closeArchiveEntry();
                File[] files = file.listFiles();
                if (files != null) {
                    for (File sf : files) {
                        if (sf.isDirectory()) {
                            writeToEntry(aos, sf, entryName + sf.getName() + "/");
                        } else {
                            writeToEntry(aos, sf, entryName + sf.getName());
                        }
                    }
                }
            } else {
                fis = new BufferedInputStream(new FileInputStream(file));
                copy(fis, aos);
                aos.closeArchiveEntry();
            }
        } catch (FileNotFoundException e) {
            throw new ArchiveException("File to be archived not found : " + file.getAbsolutePath());
        } finally {
            closeQuietly(fis);
        }
    }


    public static File turnToTempFile(InputStream is) {
        try {
            File temp = File.createTempFile("temp_archive_entry", null);
            FileOutputStream fos = new FileOutputStream(temp);
            copy(is, fos);
            closeQuietly(is);
            closeQuietly(fos);
            return temp;
        } catch (IOException e) {
            throw new ArchiveException("Creating temp file failed : " + e.getMessage());
        }
    }


    public static long copy(InputStream is, OutputStream os) throws IOException {
        Buffer buffer = BufferPool.get();
        int n;
        long count = 0;
        try {
            while (-1 != (n = is.read(buffer.get()))) {
                os.write(buffer.get(), 0, n);
                count += n;
            }
        } finally {
            buffer.back();
        }
        return count;
    }

    public static void closeQuietly(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }

    public static void closeQuietly(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }

    /**
     * if target is a file, then the file will be deleted, else if target is a directory, then the content in this
     * directory will be deleted, but the directory will be kept
     *
     * @param directory target directory need to be clean
     */
    public static void clean(File directory) {
        if (directory.isFile()) {
//            logger.debug("Delete File : {}", directory.getAbsolutePath());
            directory.delete();
            return;
        }

        File[] files = directory.listFiles();
        for (File f : files) {
            delete(f);
        }
    }

    /**
     * target file or directory will all be deleted
     *
     * @param file
     */
    public static void delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) return;

            for (File f : files) {
                delete(f);
            }

//            logger.debug("Delete Dir  : {}", file.getAbsolutePath());
            file.delete();
        }

//        logger.debug("Delete File : {}", file.getAbsolutePath());
        file.delete();
    }

}