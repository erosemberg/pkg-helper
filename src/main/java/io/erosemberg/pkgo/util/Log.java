package io.erosemberg.pkgo.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.logging.FileHandler;
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
        SimpleDateFormat format = new SimpleDateFormat("M-d_HHmmss");
        try {
            FileHandler h = new FileHandler("pkh-session-" + format.format(Calendar.getInstance().getTime()) + ".log");
            LOGGER.addHandler(h);
            SimpleFormatter formatter = new SimpleFormatter();
            Arrays.stream(LOGGER.getHandlers()).forEach(handler -> {
                handler.setFormatter(formatter);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setShouldDebug(boolean shouldDebug) {
        Log.shouldDebug = shouldDebug;
    }

    public static void info(String text) {
        LOGGER.info(text);
    }

    public static void debug(String text) {
        if (shouldDebug) {
            LOGGER.info("[DEBUG] " + text);
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

    public static void blue(String text) {
        String output = "\u001b[0;36m" + text + "\u001b[m ";
        LOGGER.info(output);
    }

}
