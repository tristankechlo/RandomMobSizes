package com.tristankechlo.random_mob_sizes.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tristankechlo.random_mob_sizes.RandomMobSizes;
import com.tristankechlo.random_mob_sizes.sampler.AttributeScalingTypes;
import com.tristankechlo.random_mob_sizes.sampler.GaussianScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.UniformScalingSampler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public final class ScalingOverrides {

    private static final String jsonKey = "scaling_overrides";
    private static Map<EntityType<?>, ScalingSampler> SETTINGS = new HashMap<>();

    public void setToDefault() {
        SETTINGS = getDefaultSettings();
    }

    public void serialize(JsonObject json) {
        JsonObject scalingOverrides = new JsonObject();
        SETTINGS.forEach((entityType, scalingSampler) -> {
            ResourceLocation location = EntityType.getKey(entityType);
            JsonElement element = scalingSampler.serialize();
            scalingOverrides.add(location.toString(), element);
        });
        json.add(jsonKey, scalingOverrides);
    }

    public void deserialize(JsonObject jsonObject) {
        JsonObject json = GsonHelper.getAsJsonObject(jsonObject, jsonKey);
        Map<EntityType<?>, ScalingSampler> newSettings = new HashMap<>();
        json.entrySet().forEach((entry) -> {
            Optional<EntityType<?>> entityType = EntityType.byString(entry.getKey());
            if (entityType.isEmpty()) {
                RandomMobSizes.LOGGER.error("Error loading config, skipping unknown entity: '{}'", entry.getKey());
                return;
            }
            try {
                EntityType<?> type = entityType.get();
                ScalingSampler scalingSampler = ScalingSampler.deserializeSampler(entry.getValue(), entry.getKey());
                newSettings.put(type, scalingSampler);
            } catch (Exception e) {
                RandomMobSizes.LOGGER.error("Error while parsing scaling, skipping scaling for entity '{}'", entry.getKey());
                RandomMobSizes.LOGGER.error(e.getMessage());
            }
        });
        SETTINGS.clear();
        SETTINGS.putAll(newSettings);
    }

    public ScalingSampler getSampler(EntityType<?> entityType) {
        return SETTINGS.get(entityType);
    }

    public boolean setScalingSampler(EntityType<?> entityType, ScalingSampler scalingSampler) {
        if (!RandomMobSizes.isEntityTypeAllowed(entityType)) {
            return false;
        }
        SETTINGS.put(entityType, scalingSampler);
        return true;
    }

    public void removeScalingSampler(EntityType<?> entityType) {
        SETTINGS.remove(entityType);
    }

    public static Iterator<Map.Entry<EntityType<?>, ScalingSampler>> getIterator() {
        return SETTINGS.entrySet().iterator();
    }

    private static Map<EntityType<?>, ScalingSampler> getDefaultSettings() {
        Map<EntityType<?>, ScalingSampler> settings = new HashMap<>();
        settings.put(EntityType.COW, new GaussianScalingSampler(0.5F, 1.5F));
        settings.put(EntityType.SHEEP, new GaussianScalingSampler(0.5F, 1.5F));
        settings.put(EntityType.PIG, new GaussianScalingSampler(0.5F, 1.5F));
        settings.put(EntityType.CHICKEN, new UniformScalingSampler(0.5F, 1.5F, AttributeScalingTypes.SQUARE, AttributeScalingTypes.NONE, AttributeScalingTypes.INVERSE_SQUARE));
        return settings;
    }

}
