package com.tristankechlo.random_mob_sizes.config;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import com.tristankechlo.random_mob_sizes.IPlatformHelper;
import com.tristankechlo.random_mob_sizes.RandomMobSizes;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public final class ConfigManager {

    private static final File CONFIG_DIR = IPlatformHelper.INSTANCE.getConfigDirectory().toFile();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    public static final String FILE_NAME = RandomMobSizes.MOD_ID + ".json";

    public static void loadAndVerifyConfig() {
        ConfigManager.createConfigFolder();
        RandomMobSizesConfig.setToDefault();
        File configFile = new File(CONFIG_DIR, FILE_NAME);
        if (configFile.exists()) {
            ConfigManager.loadConfigFromFile(configFile);
            ConfigManager.writeConfigToFile(configFile);
            RandomMobSizes.LOGGER.info("Saved the checked/corrected config: '{}'", FILE_NAME);
        } else {
            ConfigManager.writeConfigToFile(configFile);
            RandomMobSizes.LOGGER.warn("No config '{}' was found, created a new one.", FILE_NAME);
        }
    }

    private static void writeConfigToFile(File file) {
        JsonElement jsonObject = RandomMobSizesConfig.serialize(new JsonObject());
        try {
            JsonWriter writer = new JsonWriter(new FileWriter(file));
            writer.setIndent("\t");
            GSON.toJson(jsonObject, writer);
            writer.close();
        } catch (Exception e) {
            RandomMobSizes.LOGGER.error("There was an error writing the config to file: '{}'", FILE_NAME);
            e.printStackTrace();
        }
    }

    private static void loadConfigFromFile(File file) {
        JsonObject json = null;
        try {
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(new FileReader(file));
            json = jsonElement.getAsJsonObject();
        } catch (Exception e) {
            RandomMobSizes.LOGGER.error("There was an error loading the config file: '{}'", FILE_NAME);
            e.printStackTrace();
        }
        if (json != null) {
            RandomMobSizesConfig.deserialize(json);
            RandomMobSizes.LOGGER.info("Config '{}' was successfully loaded.", FILE_NAME);
        } else {
            RandomMobSizes.LOGGER.error("Error loading config '{}', config hasn't been loaded.", FILE_NAME);
        }
    }

    public static void resetConfig() {
        RandomMobSizesConfig.setToDefault();
        File configFile = new File(CONFIG_DIR, FILE_NAME);
        ConfigManager.writeConfigToFile(configFile);
        RandomMobSizes.LOGGER.info("Config '{}' was set to default.", FILE_NAME);
    }

    public static void saveConfig() {
        File configFile = new File(CONFIG_DIR, FILE_NAME);
        ConfigManager.writeConfigToFile(configFile);
        RandomMobSizes.LOGGER.info("Config '{}' was saved.", FILE_NAME);
    }

    public static void reloadConfig() {
        File configFile = new File(CONFIG_DIR, FILE_NAME);
        if (configFile.exists()) {
            ConfigManager.loadConfigFromFile(configFile);
            ConfigManager.writeConfigToFile(configFile);
            RandomMobSizes.LOGGER.info("Saved the checked/corrected config: " + FILE_NAME);
        } else {
            ConfigManager.writeConfigToFile(configFile);
            RandomMobSizes.LOGGER.warn("No config '{}' was found, created a new one.", FILE_NAME);
        }
    }

    public static String getConfigPath() {
        File configFile = new File(CONFIG_DIR, FILE_NAME);
        return configFile.getAbsolutePath();
    }

    private static void createConfigFolder() {
        if (!CONFIG_DIR.exists()) {
            if (!CONFIG_DIR.mkdirs()) {
                throw new RuntimeException("Could not create config folder: " + CONFIG_DIR.getAbsolutePath());
            }
        }
    }

}
