package com.tristankechlo.random_mob_sizes.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tristankechlo.random_mob_sizes.RandomMobSizes;
import com.tristankechlo.random_mob_sizes.sampler.AttributeScalingTypes;
import com.tristankechlo.random_mob_sizes.sampler.GaussianScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public final class RandomMobSizesConfig {

    private static final BooleanValue KEEP_SCALING_ON_CONVERSION = new BooleanValue("keep_scaling_on_conversion", true);
    private static ScalingSampler defaultSampler = createDefaultSampler();
    private static final String DEFAULT_SAMPLER_NAME = "default_scaling";
    private static final EntityTypeList WHITELIST = new EntityTypeList("whitelist", List.of("minecraft:*"));
    private static final EntityTypeList BLACKLIST = new EntityTypeList("blacklist", EntityType.SHULKER, EntityType.WITHER);
    public static final ScalingOverrides SCALING_OVERRIDES = new ScalingOverrides();

    public static void setToDefault() {
        KEEP_SCALING_ON_CONVERSION.setToDefault();
        defaultSampler = createDefaultSampler();
        WHITELIST.setToDefault();
        BLACKLIST.setToDefault();
        SCALING_OVERRIDES.setToDefault();
    }

    public static JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.add(DEFAULT_SAMPLER_NAME, defaultSampler.serialize());
        WHITELIST.serialize(json);
        BLACKLIST.serialize(json);
        KEEP_SCALING_ON_CONVERSION.serialize(json);
        SCALING_OVERRIDES.serialize(json);
        return json;
    }

    public static void deserialize(JsonObject json) {
        WHITELIST.deserialize(json);
        BLACKLIST.deserialize(json);
        KEEP_SCALING_ON_CONVERSION.deserialize(json);
        SCALING_OVERRIDES.deserialize(json);

        // deserialize default sampler
        try {
            JsonElement defaultSamplerElement = GsonHelper.getNonNull(json, DEFAULT_SAMPLER_NAME);
            defaultSampler = ScalingSampler.deserializeSampler(defaultSamplerElement, DEFAULT_SAMPLER_NAME);
        } catch (Exception e) {
            RandomMobSizes.LOGGER.error("Error while parsing '{}', using default value", DEFAULT_SAMPLER_NAME);
            defaultSampler = createDefaultSampler();
            throw new ConfigParseException(e.getMessage());
        }
    }

    private static GaussianScalingSampler createDefaultSampler() {
        return new GaussianScalingSampler(0.5F, 1.5F, AttributeScalingTypes.NORMAL, AttributeScalingTypes.NORMAL, AttributeScalingTypes.INVERSE);
    }

    public static ScalingSampler getScalingSampler(EntityType<?> entityType) {
        ScalingSampler override = SCALING_OVERRIDES.getSampler(entityType);
        if (override != null) {
            return override;
        }
        if (BLACKLIST.get().contains(entityType)) {
            return null;
        }
        if (WHITELIST.get().contains(entityType)) {
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
