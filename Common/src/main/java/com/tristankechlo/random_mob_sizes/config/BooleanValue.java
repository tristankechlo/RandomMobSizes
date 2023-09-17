package com.tristankechlo.random_mob_sizes.config;

import com.google.gson.JsonObject;
import com.tristankechlo.random_mob_sizes.RandomMobSizes;
import net.minecraft.util.GsonHelper;

import java.util.function.Supplier;

public final class BooleanValue implements Supplier<Boolean> {

    private boolean value;
    private final boolean defaultValue;
    private final String key;

    public BooleanValue(String key, boolean defaultValue) {
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.key = key;
    }

    public void setToDefault() {
        this.value = defaultValue;
    }

    public void serialize(JsonObject json) {
        json.addProperty(key, value);
    }

    public void deserialize(JsonObject json) {
        try {
            value = GsonHelper.getAsBoolean(json, key);
        } catch (Exception e) {
            RandomMobSizes.LOGGER.error("Error while parsing config value '{}' using default value.", key);
            RandomMobSizes.LOGGER.error(e.getMessage());
            value = defaultValue;
        }
    }

    @Override
    public Boolean get() {
        return value;
    }

}
