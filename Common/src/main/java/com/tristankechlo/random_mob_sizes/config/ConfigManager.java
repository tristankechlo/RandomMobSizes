package com.tristankechlo.random_mob_sizes.config;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import com.tristankechlo.random_mob_sizes.IPlatformHelper;
import com.tristankechlo.random_mob_sizes.RandomMobSizes;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class ConfigManager {

    private static final File CONFIG_DIR = IPlatformHelper.INSTANCE.getConfigDirectory().toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    public static final String FILE_NAME = RandomMobSizes.MOD_ID + ".json";
    public static boolean saveBackup = false;

    public static void loadAndVerifyConfig() {
        ConfigManager.createConfigFolder();
        RandomMobSizesConfig.setToDefault();
        File configFile = new File(CONFIG_DIR, FILE_NAME);
        if (configFile.exists()) {
            ConfigManager.loadConfigFromFile(configFile);
            if (saveBackup) {
                saveBackup(configFile);
            }
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
            RandomMobSizes.LOGGER.error(e.getMessage());
        }
    }

    private static void loadConfigFromFile(File file) {
        try {
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(new FileReader(file));
            JsonObject json = jsonElement.getAsJsonObject();

            if (json != null) {
                RandomMobSizesConfig.deserialize(json);
                RandomMobSizes.LOGGER.info("Config '{}' was successfully loaded.", FILE_NAME);
            }
        } catch (Exception e) {
            RandomMobSizes.LOGGER.error("Error loading config '{}', config hasn't been loaded", FILE_NAME);
            RandomMobSizes.LOGGER.error(e.getMessage());
            saveBackup = true;
        }
    }

    private static void saveBackup(File file) {
        String datetimeFormatted = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss").format(LocalDateTime.now());
        String backupFileName = FILE_NAME.replace(".json", "_" + datetimeFormatted + ".backup.txt");
        Path backupFilePath = Paths.get(CONFIG_DIR.getAbsolutePath(), backupFileName);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            Files.write(backupFilePath, lines);
            RandomMobSizes.LOGGER.warn("Created backup file '{}'", backupFileName);
        } catch (Exception e) {
            RandomMobSizes.LOGGER.error("Error creating backup file '{}'", backupFileName);
            RandomMobSizes.LOGGER.error(e.getMessage());
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
