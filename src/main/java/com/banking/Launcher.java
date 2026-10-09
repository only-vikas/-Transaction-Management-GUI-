package com.banking;

/**
 * Launcher class to start the JavaFX application.
 * <p>
 * Required because JavaFX 11+ needs the Application subclass to be
 * on the module path. This workaround avoids module-path issues when
 * running from a fat JAR or without module-info.java.
 */
public class Launcher {
    public static void main(String[] args) {
        App.main(args);
    }
}
