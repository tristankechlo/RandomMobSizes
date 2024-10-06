package com.tristankechlo.random_mob_sizes.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tristankechlo.random_mob_sizes.RandomMobSizes;
import com.tristankechlo.random_mob_sizes.sampler.AttributeScalingTypes;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.types.StaticScalingSampler;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public final class RandomMobSizesConfig {

    private static final BooleanValue KEEP_SCALING_ON_CONVERSION = new BooleanValue("keep_scaling_on_conversion", true);
    private static ScalingSampler defaultSampler = createDefaultSampler();
    private static final String DEFAULT_SAMPLER_NAME = "default_scaling";
    private static final EntityTypeList INCLUDE_LIST = new EntityTypeList("include_list", List.of("minecraft:*"));
    private static final EntityTypeList EXCLUDE_LIST = new EntityTypeList("exclude_list", EntityType.SHULKER, EntityType.WITHER);
    public static final ScalingOverrides SCALING_OVERRIDES = new ScalingOverrides();

    public static void setToDefault() {
        KEEP_SCALING_ON_CONVERSION.setToDefault();
        defaultSampler = createDefaultSampler();
        INCLUDE_LIST.setToDefault();
        EXCLUDE_LIST.setToDefault();
        SCALING_OVERRIDES.setToDefault();
    }

    public static JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.add(DEFAULT_SAMPLER_NAME, defaultSampler.serialize());
        INCLUDE_LIST.serialize(json);
        EXCLUDE_LIST.serialize(json);
        KEEP_SCALING_ON_CONVERSION.serialize(json);
        SCALING_OVERRIDES.serialize(json);
        return json;
    }

    public static void deserialize(JsonObject json, Runnable setMakeBackup) {
        INCLUDE_LIST.deserialize(json, setMakeBackup);
        EXCLUDE_LIST.deserialize(json, setMakeBackup);
        KEEP_SCALING_ON_CONVERSION.deserialize(json, setMakeBackup);
        SCALING_OVERRIDES.deserialize(json, setMakeBackup);

        // deserialize default sampler
        try {
            JsonElement defaultSamplerElement = GsonHelper.getAsJsonObject(json, DEFAULT_SAMPLER_NAME);
            defaultSampler = ScalingSampler.deserializeSampler(defaultSamplerElement, DEFAULT_SAMPLER_NAME);
        } catch (Exception e) {
            RandomMobSizes.LOGGER.error("Error while parsing '{}', using default value.", DEFAULT_SAMPLER_NAME);
            RandomMobSizes.LOGGER.error(e.getMessage());
            defaultSampler = createDefaultSampler();
            setMakeBackup.run();
        }
    }

    private static ScalingSampler createDefaultSampler() {
        return new StaticScalingSampler(1F, AttributeScalingTypes.NONE, AttributeScalingTypes.NONE, AttributeScalingTypes.NONE);
    }

    public static ScalingSampler getScalingSampler(EntityType<?> entityType) {
        ScalingSampler override = SCALING_OVERRIDES.getSampler(entityType);
        if (override != null) {
            return override;
        }
        if (EXCLUDE_LIST.get().contains(entityType)) {
            return null;
        }
        if (INCLUDE_LIST.get().contains(entityType)) {
            return defaultSampler;
        }
        return null;
    }

    public static boolean keepScalingOnConversion() {
        return KEEP_SCALING_ON_CONVERSION.get();
    }

    public static ScalingSampler getDefaultSampler() {
        return defaultSampler;
    }

}
