package io.erosemberg.pkgo.util;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author Erik Rosemberg
 * @since 8/10/16
 */
public final class Log {

    private static final Logger LOGGER = Logger.getGlobal();
    private static boolean shouldDebug = false;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tH:%1$tM:%1$tS] [%4$s]: %5$s%n");
        SimpleFormatter formatter = new SimpleFormatter();
        Arrays.stream(LOGGER.getHandlers()).forEach(handler -> {
            handler.setFormatter(formatter);
        });
    }

    public static void setShouldDebug(boolean shouldDebug) {
        Log.shouldDebug = shouldDebug;
    }

    public static void info(String text) {
        LOGGER.info(text);
    }

    public static void debug(String text) {
        if (shouldDebug) {
            LOGGER.info(text);
        }
    }

    public static void red(String text) {
        String output = "\u001b[0;31m" + text + "\u001b[m ";
        LOGGER.warning(output);
    }

    public static void green(String text) {
        String output = "\u001b[0;32m" + text + "\u001b[m ";
        LOGGER.info(output);
    }

}
