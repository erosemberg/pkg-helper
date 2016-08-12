package io.erosemberg.pkgo.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.erosemberg.pkgo.util.annotation.ConfigPath;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Erik Rosemberg
 * @since 8/11/16
 */
public class Config {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting()
            .serializeNulls()
            .serializeSpecialFloatingPointValues()
            .disableHtmlEscaping()
            .create();
    private transient File file;

    public static <T extends Config> T load(Class<T> clazz) {
        Config config = null;

        try {
            ConfigPath path = clazz.getAnnotation(ConfigPath.class);
            if (path == null) {
                Log.red("Invalid config file found, ConfigPath.class not found");
                return null;
            }
            File file = new File(path.value());
            if (!file.exists()) {
                config = clazz.newInstance();
                config.file = file;
                config.save();
            } else {
                try (FileReader fr = new FileReader(file)) {
                    config = gson.fromJson(fr, clazz);
                    config.file = file;
                } catch (IOException ignored) {
                }
            }
        } catch (InstantiationException | IllegalAccessException ignored) {
        }

        return config == null ? null : clazz.cast(config);
    }

    public boolean save() {
        try {
            if (this.file.getParentFile() != null) {
                this.file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            try (FileWriter fw = new FileWriter(file)) {
                fw.write(gson.toJson(this));
            } catch (IOException e) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}
