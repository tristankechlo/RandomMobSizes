package com.tristankechlo.random_mob_sizes.config;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import com.tristankechlo.random_mob_sizes.IPlatformHelper;
import com.tristankechlo.random_mob_sizes.RandomMobSizes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ConfigManager {

    private static final File CONFIG_DIR = IPlatformHelper.INSTANCE.getConfigDirectory().toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    public static final String FILE_NAME = RandomMobSizes.MOD_ID + ".json";
    private static final File CONFIG_FILE = new File(CONFIG_DIR, FILE_NAME);

    public static boolean loadAndVerifyConfig() {
        ConfigManager.createConfigFolder();
        RandomMobSizesConfig.setToDefault();
        if (CONFIG_FILE.exists()) {
            boolean success = true;
            AtomicBoolean makeBackup = new AtomicBoolean(false);
            try {
                ConfigManager.loadConfigFromFile(() -> makeBackup.set(true));
                RandomMobSizes.LOGGER.info("Config '{}' was successfully loaded.", FILE_NAME);
            } catch (Exception e) {
                RandomMobSizes.LOGGER.error(e.getMessage());
                RandomMobSizes.LOGGER.error("Error loading config '{}', config hasn't been loaded. Using default config.", FILE_NAME);
                ConfigManager.backupConfig(); // save content of old config to a backup file
                RandomMobSizesConfig.setToDefault();
                success = false;
            }
            if (makeBackup.get()) {
                ConfigManager.backupConfig();
                success = false;
            }
            ConfigManager.writeConfigToFile(); // always write the parsed config to file
            return success;
        } else {
            ConfigManager.writeConfigToFile();
            RandomMobSizes.LOGGER.warn("No config '{}' was found, created a new one.", FILE_NAME);
            return true;
        }
    }

    private static void loadConfigFromFile(Runnable setMakeBackup) throws FileNotFoundException {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(new FileReader(CONFIG_FILE));
        JsonObject json = jsonElement.getAsJsonObject();
        RandomMobSizesConfig.deserialize(json, setMakeBackup);
    }

    private static void writeConfigToFile() {
        try {
            JsonElement jsonObject = RandomMobSizesConfig.serialize();
            JsonWriter writer = new JsonWriter(new FileWriter(CONFIG_FILE));
            writer.setIndent("\t");
            GSON.toJson(jsonObject, writer);
            writer.close();
        } catch (Exception e) {
            RandomMobSizes.LOGGER.error("There was an error writing the config to file: '{}'", FILE_NAME);
            RandomMobSizes.LOGGER.error(e.getMessage());
        }
    }

    private static void backupConfig() {
        String backupFileName = FILE_NAME.replace(".json", ".backup.txt");
        Path backupFilePath = Paths.get(CONFIG_DIR.getAbsolutePath(), backupFileName);
        try {
            List<String> lines = Files.readAllLines(CONFIG_FILE.toPath());
            Files.write(backupFilePath, lines);
            RandomMobSizes.LOGGER.warn("Created backup file '{}'", backupFileName);
        } catch (Exception e) {
            RandomMobSizes.LOGGER.error("Error creating backup file '{}'", backupFileName);
            RandomMobSizes.LOGGER.error(e.getMessage());
        }
    }

    public static boolean resetConfig() {
        try {
            RandomMobSizesConfig.setToDefault();
            ConfigManager.writeConfigToFile();
            RandomMobSizes.LOGGER.info("Config '{}' was set to default.", FILE_NAME);
            return true;
        } catch (Exception e) {
            RandomMobSizes.LOGGER.error("Error resetting config '{}'", FILE_NAME);
            RandomMobSizes.LOGGER.error(e.getMessage());
        }
        return false;
    }

    public static boolean saveConfig() {
        try {
            ConfigManager.writeConfigToFile();
            RandomMobSizes.LOGGER.info("Config '{}' was saved.", FILE_NAME);
            return true;
        } catch (Exception e) {
            RandomMobSizes.LOGGER.error("Error saving config '{}'", FILE_NAME);
            RandomMobSizes.LOGGER.error(e.getMessage());
        }
        return false;
    }

    public static boolean reloadConfig() {
        try {
            return ConfigManager.loadAndVerifyConfig();
        } catch (Exception e) {
            RandomMobSizes.LOGGER.error("Error reloading config '{}'", FILE_NAME);
            RandomMobSizes.LOGGER.error(e.getMessage());
        }
        return false;
    }

    public static String getConfigPath() {
        return CONFIG_FILE.getAbsolutePath();
    }

    private static void createConfigFolder() {
        if (!CONFIG_DIR.exists()) {
            if (!CONFIG_DIR.mkdirs()) {
                throw new RuntimeException("Could not create config folder: " + CONFIG_DIR.getAbsolutePath());
            }
        }
    }

}