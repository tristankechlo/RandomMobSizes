package com.tristankechlo.random_mob_sizes.config;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.tristankechlo.random_mob_sizes.RandomMobSizesMod;
import com.tristankechlo.random_mob_sizes.sampler.GaussianSampler;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.StaticScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.UniformScalingSampler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class RandomMobSizesConfig {

    private static final Map<String, IDeserializer> DESERIALIZER = setupDeserializers();
    public static Map<EntityType<?>, ScalingSampler> SETTINGS = new HashMap<>();
    private static final Type MAP_TYPE = new TypeToken<Map<String, JsonElement>>() {}.getType();

    public static void setToDefault() {
        SETTINGS = getDefaultSettings();
    }

    public static JsonObject serialize(JsonObject json) {
        SETTINGS.forEach((entityType, scalingSampler) -> {
            ResourceLocation location = EntityType.getKey(entityType);
            JsonElement element = scalingSampler.serialize();
            json.add(location.toString(), element);
        });
        return json;
    }

    public static void deserialize(JsonObject json) {
        // deserialize entity specific samplers
        Map<String, JsonElement> settings = ConfigManager.GSON.fromJson(json, MAP_TYPE);
        Map<EntityType<?>, ScalingSampler> newSettings = new HashMap<>();
        settings.forEach((key, value) -> {
            Optional<EntityType<?>> entityType = EntityType.byString(key);
            if (entityType.isPresent()) {
                try {
                    EntityType<?> type = entityType.get();
                    ScalingSampler scalingSampler = deserializeSampler(value, key);
                    newSettings.put(type, scalingSampler);
                } catch (Exception e) {
                    RandomMobSizesMod.LOGGER.error("Error while parsing scaling for entity {}", key);
                    RandomMobSizesMod.LOGGER.error(e.getMessage());
                }
            } else {
                RandomMobSizesMod.LOGGER.error("Error loading config, unknown EntityType: '{}'", key);
            }
        });
        SETTINGS = ImmutableMap.copyOf(newSettings);
    }

    private static ScalingSampler deserializeSampler(JsonElement jsonElement, String entityType) {
        if (jsonElement.isJsonPrimitive()) {
            return new StaticScalingSampler(jsonElement.getAsFloat());
        } else if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            IDeserializer deserializer = DESERIALIZER.get(type);
            if (deserializer == null) {
                throw new JsonParseException("Unknown ScalingType: " + type);
            }
            return deserializer.deserialize(jsonElement, entityType);
        }
        throw new JsonParseException("ScalingType must be a JsonPrimitive or JsonObject");
    }

    private static Map<String, IDeserializer> setupDeserializers() {
        Map<String, IDeserializer> deserializer = new HashMap<>();
        deserializer.put(UniformScalingSampler.TYPE, UniformScalingSampler::new);
        deserializer.put(GaussianSampler.TYPE, GaussianSampler::new);
        return deserializer;
    }

    public static Map<EntityType<?>, ScalingSampler> getDefaultSettings() {
        Map<EntityType<?>, ScalingSampler> settings = new HashMap<>();
        settings.put(EntityType.BAT, new StaticScalingSampler(0.75F));
        settings.put(EntityType.COW, new UniformScalingSampler(0.5F, 1.5F));
        settings.put(EntityType.SHEEP, new GaussianSampler(0.5F, 1.5F));
        settings.put(EntityType.PIG, new GaussianSampler(0.5F, 1.5F));
        settings.put(EntityType.CHICKEN, new GaussianSampler(0.5F, 1.5F));
        settings.put(EntityType.FROG, new GaussianSampler(0.5F, 1.5F));
        return settings;
    }

    @FunctionalInterface
    private interface IDeserializer {
        ScalingSampler deserialize(JsonElement jsonElement, String key) throws JsonParseException;
    }

}
