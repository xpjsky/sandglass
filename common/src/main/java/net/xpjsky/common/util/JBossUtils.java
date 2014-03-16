package net.xpjsky.common.util;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Description Here
 *
 * @author Paddy
 */
public class JBossUtils {

    public static String getMajorVersion(String jbossHomePath) {
        String version = getVersion(jbossHomePath);
        if (StringUtils.isNotBlank(version)) {
            int index = 0;
            for (int i = 0; i < version.length(); i++) {
                if (version.charAt(i) == '.') {
                    index = i;
                    break;
                }
            }
            return version.substring(0, index);
        }
        return null;
    }

    /**
     * return short version of JBoss, like 4.1, 5.1, 6.1, 7.2
     *
     * @param jbossHomePath the directory of jboss
     * @return short version of JBoss
     */
    public static String getShortVersion(String jbossHomePath) {
        String version = getVersion(jbossHomePath);

        if (StringUtils.isNotBlank(version)) {
            int index = 0, count = 0;
            for (int i = 0; i < version.length(); i++) {
                if (version.charAt(i) == '.') {
                    count++;
                }
                if (count == 2) {
                    index = i;
                    break;
                }
            }
            return version.substring(0, index);
        }
        return null;
    }

    public static String getVersion(String jbossHomePath) {
        File jbossHome = new File(jbossHomePath);
        if (jbossHome.exists() && jbossHome.isDirectory()) {
            try {
                String basePath = jbossHome.getCanonicalPath() + File.separator;

                // JBoss 4
                String jarPath = "lib/jboss-system.jar";
                String version = getVersionFromManifest(new File(basePath + jarPath));

                // JBoss 5 and 6
                if (StringUtils.isBlank(version)) {
                    jarPath = "lib/jboss-main.jar";
                    version = getVersionFromManifest(new File(basePath + jarPath));
                }

                // JBoss 7, JBoss EAP 6 and WildFly 8
                if (StringUtils.isBlank(version)) {
                    jarPath = "modules";

                    File main = getMainFolder(new File(basePath + jarPath));
                    if (main != null) {
                        File[] jar = main.listFiles(FileUtil.endsWithFilter("jar"));
                        if (jar.length > 0) {
                            version = getVersionFromManifest(jar[0]);
                        }
                    }
                }

                if (StringUtils.isNotBlank(version)) {
                    return version;
                }
            } catch (IOException e) {
                // ignore
            }

        }

        return null;
    }

    private static File getMainFolder(File modules) {
        File main = null;
        try {
            modules = modules.getCanonicalFile();
            String s = File.separator;
            String packageName = "org" + s + "jboss" + s + "as" + s + "server" + s + "main";
            File[] files = modules.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        if (file.getPath().endsWith(packageName)) {
                            main = file;
                            break;
                        }
                        main = getMainFolder(file);
                        if (main != null) {
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            // ignore
        }
        return main;
    }

    private static String getVersionFromManifest(File jarFile) {
        JarFile jar = null;
        String version = null;
        try {
            jar = new JarFile(jarFile);
            Manifest manifest = jar.getManifest();
            Attributes attr = manifest.getMainAttributes();
            if (attr != null) {
                version = attr.getValue("Specification-Version");
                if (StringUtils.isBlank(version)) {
                    version = attr.getValue("Implementation-Version");
                }
            }
        } catch (IOException e) {
            // ignore
        } finally {
            IOUtil.closeQuietly(jar);
        }
        return version;
    }

}
