package net.xpjsky.sandglass.launcher;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 10/19/12 9:58 AM
 */
public class Configuration {

    private String configFile;

    private String dirLog;
    private String dirLib;
    private String libExt;
    private String launcher;

    public Configuration() {
        this("launcher.properties");
    }

    public Configuration(String file) {
        this.configFile = file;
//        load();
    }

    public void load(String fileName) {

    }



}
