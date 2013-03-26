package net.xpjsky.sandglass.common.util;

import java.io.File;
import java.io.InputStream;

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
    public static void createFile(File file) {

    }

    public static void createDirectory(File file) {

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

    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) return;

            for (File f : files) {
                delete(f);
            }
            file.delete();
        }
    }

    public static void clean(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) return;

            for (File f : files) {
                delete(f);
            }
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


}
