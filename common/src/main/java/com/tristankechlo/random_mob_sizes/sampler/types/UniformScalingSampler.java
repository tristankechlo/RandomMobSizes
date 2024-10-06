package com.tristankechlo.random_mob_sizes.sampler.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tristankechlo.random_mob_sizes.sampler.AttributeScalingTypes;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;

/**
 * creates random floats that are uniformly distributed between min and max,
 * basically random values between min and max
 */
public class UniformScalingSampler extends ScalingSampler {

    public static final String TYPE = "uniform";
    private final float min_scaling;
    private final float max_scaling;

    public UniformScalingSampler(float min_scaling, float max_scaling) {
        this.min_scaling = min_scaling;
        this.max_scaling = max_scaling;
    }

    public UniformScalingSampler(JsonElement jsonElement, String entityType) {
        super(jsonElement, entityType);
        JsonObject json = GsonHelper.convertToJsonObject(jsonElement, entityType);
        this.min_scaling = getFloatSafe(json, "min_scaling", entityType);
        this.max_scaling = getFloatSafe(json, "max_scaling", entityType);
        ensureMinSmallerMax(min_scaling, max_scaling, entityType);
    }

    public UniformScalingSampler(float min_scaling, float max_scaling, AttributeScalingTypes health, AttributeScalingTypes damage, AttributeScalingTypes speed) {
        super(health, damage, speed);
        this.min_scaling = min_scaling;
        this.max_scaling = max_scaling;
    }

    @Override
    protected float sampleScalingFactor(RandomSource random, Difficulty difficulty) {
        return min_scaling + (float) (random.nextDouble() * (max_scaling - min_scaling));
    }

    @Override
    protected JsonObject serialize(JsonObject json) {
        json.addProperty("type", TYPE);
        json.addProperty("min_scaling", min_scaling);
        json.addProperty("max_scaling", max_scaling);
        return json;
    }

}
