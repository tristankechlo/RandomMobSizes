package com.tristankechlo.random_mob_sizes.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.tristankechlo.random_mob_sizes.RandomMobSizes;
import com.tristankechlo.random_mob_sizes.commands.SamplerTypes;
import com.tristankechlo.random_mob_sizes.sampler.GaussianSampler;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.StaticScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.UniformScalingSampler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.lang.reflect.Type;
import java.util.*;

public final class RandomMobSizesConfig {

    private static Map<EntityType<?>, ScalingSampler> SETTINGS = new HashMap<>();
    private static final Type MAP_TYPE = new TypeToken<Map<String, JsonElement>>() {}.getType();
    private static boolean keepScalingOnConversion = true;

    public static void setToDefault() {
        SETTINGS = getDefaultSettings();
        keepScalingOnConversion = true;
    }

    public static JsonObject serialize(JsonObject json) {
        json.addProperty("keep_scaling_on_conversion", keepScalingOnConversion);

        // serialize entity specific samplers
        SETTINGS.forEach((entityType, scalingSampler) -> {
            ResourceLocation location = EntityType.getKey(entityType);
            JsonElement element = scalingSampler.serialize();
            json.add(location.toString(), element);
        });
        return json;
    }

    public static void deserialize(JsonObject json) {
        try {
            keepScalingOnConversion = GsonHelper.getAsBoolean(json, "keep_scaling_on_conversion");
            json.remove("keep_scaling_on_conversion"); //remove the value from the json, so it doesn't get used as a sampler
        } catch (Exception e) {
            RandomMobSizes.LOGGER.error("Error while parsing config value 'keep_scaling_on_conversion' using default value.");
            RandomMobSizes.LOGGER.error(e.getMessage());
            keepScalingOnConversion = true;
        }

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
                    RandomMobSizes.LOGGER.error("Error while parsing scaling for entity {}", key);
                    RandomMobSizes.LOGGER.error(e.getMessage());
                }
            } else {
                RandomMobSizes.LOGGER.error("Error loading config, unknown EntityType: '{}'", key);
            }
        });
        SETTINGS.clear();
        SETTINGS.putAll(newSettings);
    }

    private static ScalingSampler deserializeSampler(JsonElement jsonElement, String entityType) {
        if (jsonElement.isJsonPrimitive()) {
            return new StaticScalingSampler(jsonElement, entityType);
        } else if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            SamplerTypes samplerType = SamplerTypes.byName(type, null);
            if (samplerType == null) {
                throw new JsonParseException("Unknown ScalingType: " + type);
            }
            return samplerType.fromJson(jsonElement, entityType);
        }
        throw new JsonParseException("ScalingType must be a JsonPrimitive or JsonObject");
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

    public static ScalingSampler getScalingSampler(EntityType<?> entityType) {
        return SETTINGS.get(entityType);
    }

    public static boolean setScalingSampler(EntityType<?> entityType, ScalingSampler scalingSampler) {
        //disallow all entities from the spawn group "misc", except for golems, villagers
        final List<EntityType<?>> allowedMisc = Arrays.asList(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER);
        if (entityType.getCategory() == MobCategory.MISC && !allowedMisc.contains(entityType)) {
            return false;
        }
        SETTINGS.put(entityType, scalingSampler);
        return true;
    }

    public static void removeScalingSampler(EntityType<?> entityType) {
        SETTINGS.remove(entityType);
    }

    public static Iterator<Map.Entry<EntityType<?>, ScalingSampler>> getIterator() {
        return SETTINGS.entrySet().iterator();
    }

    public static boolean keepScalingOnConversion() {
        return keepScalingOnConversion;
    }

}
