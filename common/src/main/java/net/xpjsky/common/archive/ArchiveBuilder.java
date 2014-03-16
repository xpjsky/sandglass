package net.xpjsky.common.archive;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 9/24/12 10:30 AM
 */
public class ArchiveBuilder {

    private Logger logger = LoggerFactory.getLogger(ArchiveBuilder.class);
    private static final String nameExpr = "[\\w\\.\\s\\+\\-()]{1,100}";

    // Building Status
    private static final int NOT_READY = 0, STARTED = 1, FINISHED = 2;
    // Building Mode
    private static final int STREAM_MODE = 1, FILE_MODE = 2;

    private ArchiveType archiveType = null;
    private int compressionLevel = -1;
    private boolean deleteSource = false;
    private boolean stopIfExist = false;
    private boolean deleteExist = true;

    // FILE_MODE   : path + name
    private String archivePath = null;
    private String archiveName = null;
    private File archiveFile = null;

    // STREAM_MODE : output stream
    private OutputStream archiveStream = null;

    private FileFilter filter = null;

    private int status = NOT_READY;
    private int mode = 0;

    private String folderEntry = "";
    private List<Entry> entryList = null;
    private ArchiveOutputStream aos = null;

    public static ArchiveBuilder newBuilder(String archiveType) {
        ArchiveType type = ArchiveType.parse(archiveType);
        if (type == null) {
            throw new ArchiveException("Unknown archive type " + archiveType);
        }
        return new ArchiveBuilder(type);
    }

    public static ArchiveBuilder newBuilder(ArchiveType archiveType) {
        return new ArchiveBuilder(archiveType);
    }

    private ArchiveBuilder(ArchiveType archiveType) {
        if (archiveType == null) {
            throw new ArchiveException("Archive type can not be null");
        }
        this.archiveType = archiveType;
        this.entryList = new LinkedList<Entry>();
    }

    /**
     * folder entry path, based from root path, null or empty means return to root
     */
    public ArchiveBuilder switchTo(String directory) {
        if (directory == null || "".equals(directory = directory.trim())) {
            folderEntry = "";
            return this;
        }

        if (directory.startsWith("/")) {
            directory = directory.substring(1);
        }
        //TODO make sure the path format : xx/xxx/xxx/xx
        if (directory.length() > 1 && !directory.endsWith("/")) {
            directory = directory + "/";
        }
        folderEntry = directory;
        return this;
    }

    public ArchiveBuilder putEntry(String fileOrDirectory) {
        return putEntry(fileOrDirectory, null);
    }

    public ArchiveBuilder putEntry(String fileOrDirectory, String entryName) {
        checkEmpty(fileOrDirectory, "File can not be empty");

        return putEntry(new File(fileOrDirectory), entryName);
    }

    public ArchiveBuilder putEntry(File fileOrDirectory) {
        return putEntry(fileOrDirectory, null);
    }

    public ArchiveBuilder putEntry(File fileOrDirectory, String entryName) {
        checkNull(fileOrDirectory, "Entry can not be null");
        if (!fileOrDirectory.exists()) {
            logger.warn("File {} not found!", fileOrDirectory.getAbsolutePath());
            return this;
        }
        if (entryName == null || "".equals(entryName = entryName.trim())) {
            entryName = folderEntry + fileOrDirectory.getName();
        }
        if (fileOrDirectory.isDirectory()) {
            entryName = entryName + "/";
        }
        entryList.add(new Entry(fileOrDirectory, entryName, false));
        return this;
    }

    public ArchiveBuilder putEntry(InputStream fileStream, String entryName) {
        checkNull(fileStream, "Stream can not be null");
        if (entryName == null || entryName.trim().equals("")) {
            throw new ArchiveException("Stream entry must have an entry name");
        }
        File tempFile = ArchiveUtil.turnToTempFile(fileStream);
        entryList.add(new Entry(tempFile, entryName, true));
        return this;
    }

    /**
     * set the <b>directory</b> to store archive file
     *
     * @param archivePath directory path to store archive file
     * @return this
     */
    public ArchiveBuilder archiveTo(String archivePath) {
        checkNull(archivePath, "Archive path can not be null");
        checkStatus(STARTED, "Directory can not be changed after archiving started");

        this.archiveStream = null;
        this.archivePath = archivePath;
        this.archiveFile = null;
        this.mode = FILE_MODE;

        return this;
    }

    /**
     * set the output stream to let archive data put to
     *
     * @param os output stream to use
     * @return this
     */
    public ArchiveBuilder archiveTo(OutputStream os) {
        checkNull(os, "Archive stream can not be null");
        checkStatus(STARTED, "Can not archive to stream after archiving started");

        this.archivePath = null;
        this.archiveStream = os;
        this.archiveFile = null;
        this.mode = STREAM_MODE;

        return this;
    }

    /**
     * archiving to temp directory
     *
     * @return this
     */
    public ArchiveBuilder archiveToTemp() {
        checkStatus(STARTED, "Can not archive to temp after archiving started");

        try {
            this.archiveStream = null;
            this.archiveFile = File.createTempFile("temp_archive", archiveType.getFilenameExtension());
            this.archivePath = null;
            this.archiveName = null;
            this.mode = FILE_MODE;
        } catch (IOException e) {
            throw new ArchiveException("Temp archive file creating failed");
        }
        return this;
    }

    /**
     * filename without extension, should match expression "[\w\.\s\+\-()]{1,100}"
     */
    public ArchiveBuilder withName(String archiveName) {
        // if archive file have already defined or current archive mode is STREAM_MODE
        // then just ignore this archive name.
        if (archiveFile != null || mode == STREAM_MODE) {
            logger.info("Archive name ignored");
            return this;
        }

        checkStatus(STARTED, "Archive name can not be changed after archiving started");

        if (null == archiveName || "".equals(archiveName = archiveName.trim())) {
            throw new ArchiveException("Archive name can not be null or empty");
        }
        if (!archiveName.matches(nameExpr)) {
            throw new ArchiveException("Archive name invalid, should match " + nameExpr);
        }
        this.archiveName = archiveName + archiveType.getFilenameExtension();
        return this;
    }

    public ArchiveBuilder withFilter(FileFilter filter) {
        checkStatus(STARTED, "File filter can not be changed after archiving started");

        this.filter = filter;
        return this;
    }

    /**
     * compression level, integer number between 0-9, only for ZIP serials(zip, jar).
     */
    public ArchiveBuilder inLevel(int level) {
        checkStatus(STARTED, "Compression level can not be changed after archiving started");

        if ((level < 0 || level > 9) && level != -1) {
            if (level > 9) level = 9;
            if (level < 0) level = 0;
            logger.warn("Compression Level can only be 0-9, set to {}", level);
        }
        this.compressionLevel = level;
        return this;
    }

    public ArchiveBuilder deleteSource() {
        if (status >= FINISHED) {
            doDeleteSource();
        }
        this.deleteSource = true;
        return this;
    }

    public ArchiveBuilder stopIfExist() {
        this.stopIfExist = true;
        return this;
    }

    public ArchiveBuilder finish() {
        // check status
        if (status >= FINISHED) {
            return this;
        }
        // set status to STARTED
        status = STARTED;
        // check ArchiveOutputStream
        createArchive();

        logger.info("Archiving started, archive to {}",
                archiveFile == null ? "stream" : archiveFile.getAbsolutePath());
        // do Archive by iterate entryList
        try {
            InputStream is = null;
            for (Entry entry : entryList) {
                if (filter == null || filter.accept(entry.getFile())) {
                    ArchiveUtil.writeToEntry(aos, entry.getFile(), entry.getName());
                }
            }
            aos.finish();
        } catch (IOException e) {
            ArchiveUtil.closeQuietly(aos);
            if (archiveFile != null) {
                boolean delete = archiveFile.delete();
                if (!delete) {
                    logger.warn("Temp archive file delete failed : " + archiveFile.getAbsolutePath());
                }
            }
            throw new ArchiveException("Archiving failed : " + e.getMessage());
        } finally {
            ArchiveUtil.closeQuietly(aos);
        }
        // rename archive from tmp file to normal archive file
        renameTmp();
        // set status to FINISHED
        status = FINISHED;
        return this;
    }

    public InputStream returnStream() {
        if (status < FINISHED) {
            finish();
        }
        try {
            // StreamMode : there is no input stream to return
            if (mode == STREAM_MODE) {
                return null;
            }
            // FileMode,TempMode : return InputStream of archive file
            else {
                return new FileInputStream(archiveFile);
            }
        } catch (FileNotFoundException e) {
            throw new ArchiveException("Archive file not found");
        }
    }

    public BufferedInputStream returnBufferedStream() {
        return new BufferedInputStream(returnStream());
    }

    public File returnFile() {
        if (status < FINISHED) {
            finish();
        }
        // StreamMode : there is no archive file to return
        if (mode == STREAM_MODE) {
            return null;
        }
        // FileMode,TempMode : return archive file
        else {
            return archiveFile;
        }
    }

    // ------------ UTIL METHOD -----------------------------------------------
    private void createArchive() {

        if (mode != FILE_MODE && mode != STREAM_MODE) {
            throw new ArchiveException("ArchiveBuilder is not ready yet, " +
                    "call archiveTo() method to choose where to archive to");
        }

        if (mode == FILE_MODE) {
            // Normal File mode, else would be Temp File mode
            if (archiveFile == null) {
                if ((archivePath == null || "".equals(archivePath = archivePath.trim()))) {
                    throw new ArchiveException("Directory to archive to is not defined");
                }

                File archiveDir = new File(archivePath);
                // if is an already existing file
                if (archiveDir.isFile()) {
                    throw new ArchiveException(archiveDir.getAbsolutePath() + " is an already existing file");
                }
                // if not exist and make dirs failed
                if (!archiveDir.exists() && !archiveDir.mkdirs()) {
                    throw new ArchiveException("Directory " + archiveDir.getAbsolutePath() + " create failed");
                }
                archivePath = archiveDir.getAbsolutePath();

                if (archiveName == null || "".equals(archiveName = archiveName.trim())) {
                    archiveName = archiveDir.getName() + archiveType.getFilenameExtension();
                }

                archiveFile = new File(archivePath + File.separator + archiveName + ".tmp");
            }

            try {
                archiveStream = new BufferedOutputStream(new FileOutputStream(archiveFile));
            } catch (FileNotFoundException e) {
                throw new ArchiveException("Archive file not found : " + archiveFile.getAbsolutePath());
            }
        }

        // check Compression
        if (archiveType.getCompressor() != null) {
            try {
                archiveStream = ArchiveUtil.wrap(archiveStream, archiveType.getCompressor());
            } catch (IOException e) {
                throw new ArchiveException(e);
            }
        }
        // creating archive output stream
        try {
            aos = new ArchiveStreamFactory().createArchiveOutputStream(archiveType.getArchiver(), archiveStream);

            if (aos instanceof ZipArchiveOutputStream) {
                ((ZipArchiveOutputStream) aos).setLevel(compressionLevel);
            }
        } catch (org.apache.commons.compress.archivers.ArchiveException e) {
            throw new ArchiveException(e);
        }
    }

    private void doDeleteSource() {

    }

    private void renameTmp() {
        String name = archiveFile.getName();
        name = name.substring(0, name.length() - 4);
        File newFile = new File(archiveFile.getParent() + File.separator + name);
        if(!newFile.delete()) {
            throw new ArchiveException("Archive file already exist and delete failed");
        }
        boolean rename = archiveFile.renameTo(newFile);
        if(!rename) {
            throw new ArchiveException("Renaming temp archive file failed");
        }
    }

    private void checkStatus(int status, String message) {
        if (this.status >= status) {
            throw new ArchiveException(message == null ? ("Status Error : " + status) : message);
        }
    }

    private void checkNull(Object o, String errorMessage) {
        if (o == null) {
            throw new ArchiveException(errorMessage);
        }
    }

    private void checkEmpty(String s, String errorMessage) {
        if (s == null || "".equals(s.trim())) {
            throw new ArchiveException(errorMessage);
        }
    }

    static class Entry {
        private File file;
        private String name;
        private boolean temp;

        Entry(File file, String name, boolean temp) {
            this.file = file;
            this.name = name;
            this.temp = temp;
        }

        public File getFile() {
            return file;
        }

        public String getName() {
            return name;
        }

        public boolean isTemp() {
            return temp;
        }
    }
}
