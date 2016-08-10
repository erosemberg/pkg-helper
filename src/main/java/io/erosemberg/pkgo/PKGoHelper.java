package io.erosemberg.pkgo;

import io.erosemberg.pkgo.util.ArrayUtil;
import io.erosemberg.pkgo.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Erik Rosemberg
 * @since 8/10/16
 */
public class PKGoHelper {

    public static void main(String[] args) {
        boolean debug = ArrayUtil.contains(args, "-d");
        Log.setShouldDebug(debug);
        Properties properties = new Properties();
        try {
            FileInputStream input = new FileInputStream("config.properties");
            properties.load(input);
        } catch (IOException e) {
            Log.red("config.properties does not exist. Please set it up before starting!");
            System.exit(0);
        }
        Helper helper = new Helper();
        helper.init(properties);
    }
}
