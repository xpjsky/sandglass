package net.xpjsky.sandglass.launcher;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Descriptions Here
 *
 * @author Paddy
 * @version 12-8-1 下午11:54
 */
public class Main {

    private static final String CLASSPATH_ROOT = "lib";
    private static String LIB_EXT = ".jar";
    private static String LAUNCHER_CLASS = "demo.launcher.AppLauncher";

    private static ClassLoader cl;

    static {
        try {
            cl = getClassLoaderFromPath(
                    new File(CLASSPATH_ROOT),
                    Thread.currentThread().getContextClassLoader()
            );
            Thread.currentThread().setContextClassLoader(cl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ClassLoader getClassLoaderFromPath(File path, ClassLoader parent) throws Exception {
        // get jar files from jarPath
        File[] entries = path.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().endsWith(Main.LIB_EXT);
            }
        });

        List<URL> classpathEntries = new ArrayList<URL>();

        for (File entry : entries) {
             classpathEntries.add(entry.toURI().toURL());
        }
        return new URLClassLoader(classpathEntries.toArray(new URL[classpathEntries.size()]), parent);
    }

    private static void getClassPathLib(List<URL> classpathEntries, File path) throws MalformedURLException {
        classpathEntries.add(path.toURI().toURL());
        if(path.isDirectory()) {
            getClassPathLib(classpathEntries, path);
        }
    }

    public static void main(String[] args) throws Exception {
        Launcher launcher = Launcher.class.cast(
                Class.forName(LAUNCHER_CLASS, true, cl).newInstance()
        );
        launcher.launch("this string is capitalized");
    }
}
