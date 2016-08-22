package io.erosemberg.pkgo;

import io.erosemberg.pkgo.config.HelperConfig;
import io.erosemberg.pkgo.util.ArrayUtil;
import io.erosemberg.pkgo.util.Config;
import io.erosemberg.pkgo.util.Log;

/**
 * @author Erik Rosemberg
 * @since 8/10/16
 */
public class PKGoHelper {

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> Log.debug(t.getName() + " encountered an exception, " + e.getMessage()));
        boolean debug = ArrayUtil.contains(args, "-d");
        Log.setShouldDebug(debug);

        HelperConfig config = Config.load(HelperConfig.class);
        Helper helper = Helper.getInstance();
        helper.init(config);
    }
}
