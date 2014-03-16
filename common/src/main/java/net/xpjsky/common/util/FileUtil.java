package net.xpjsky.common.util;

import java.io.*;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 10/10/12 5:01 PM
 */
public final class FileUtil {

    public static void turnToFile(InputStream is, File file) {

    }

    public static File createTempFile(String name) {
        return null;
    }

    /**
     * create file as well as its parent
     *
     * @param file
     */
    public static boolean createFile(File file) {
        if(file.isDirectory()) {
            return false;
        }

        if(file.isFile()) {
            return true;
        }
        File parent;
        try {
            if((parent = file.getParentFile()) != null) {
                return createDirectory(parent) && file.createNewFile();
            } else {
                return file.createNewFile();
            }
        } catch (IOException e) {
            return file.exists();
        }
    }

    public static boolean createDirectory(File file) {
        if(file.isDirectory()) {
            return true;
        }

        if(file.isFile()) {
            return false;
        }

        return file.mkdirs();
    }

    public static boolean rename(File file, String newName) {
        File newFile = new File(file.getParent() + File.separator + newName);
        return !newFile.exists() && file.renameTo(newFile);
    }

    public static boolean move(File source, File target) {
        return move(source, target, false);
    }

    public static boolean move(File source, File target, boolean overwrite) {
        if (target.exists()) {
            if (overwrite) {
                if (!target.delete()) {
                    return false;
                }
            } else {
                return false;
            }
        }
        if (!target.getParentFile().exists() && !target.getParentFile().mkdirs()) {
            return false;
        }
        return source.renameTo(target);
    }

    public static boolean delete(File file) {
        boolean cleaned = clean(file);

        return cleaned && file.delete();
    }

    public static boolean clean(File file) {
        if (file.isFile()) {
            return file.delete();
        } else {
            File[] files = file.listFiles();
            if (files == null) return true;

            boolean cleaned = true;
            for (File f : files) {
                // if f is directory, then clean the directory firstly, then delete the directory
                if(f.isDirectory()) {
                    cleaned = clean(f) && f.delete() && cleaned;
                }
                // if f is file, delete it directly
                else {
                    cleaned = f.delete() && cleaned;
                }
            }
            return cleaned;
        }
    }

    public static void appendPrefix(File file, String prefix) {

    }

    public static void removePrefix(File file, String prefix) {

    }

    public static void appendSuffix(File file, String suffix) {

    }

    public static void removeSuffix(File file, String suffix) {

    }

    public static FilenameFilter endsWithFilter(final String charSeq) {
        return new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(charSeq);
            }
        };
    }

}
