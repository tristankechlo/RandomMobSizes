package com.tristankechlo.random_mob_sizes;

import java.nio.file.Path;
import java.util.ServiceLoader;

public interface IPlatformHelper {

    public static final IPlatformHelper INSTANCE = load(IPlatformHelper.class);

    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }

    Path getConfigDirectory();


    private static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        RandomMobSizes.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }

}