package io.erosemberg.pkgo.util;

/**
 * @author Erik Rosemberg
 * @since 8/10/16
 */
public final class ArrayUtil {

    public static boolean contains(String[] args, String argument) {
        for (String a : args) {
            if (a.equals(argument)) {
                return true;
            }
        }

        return false;
    }

}
