package com.tristankechlo.random_mob_sizes.sampler.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tristankechlo.random_mob_sizes.sampler.AttributeScalingTypes;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;

/**
 * creates random doubles that are uniformly distributed between min and max,
 * basically random values between min and max
 */
public class UniformScalingSampler extends ScalingSampler {

    public static final String TYPE = "uniform";
    private final double min_scaling;
    private final double max_scaling;

    public UniformScalingSampler(double min_scaling, double max_scaling) {
        this.min_scaling = min_scaling;
        this.max_scaling = max_scaling;
    }

    public UniformScalingSampler(JsonElement jsonElement, String entityType) {
        super(jsonElement, entityType);
        JsonObject json = GsonHelper.convertToJsonObject(jsonElement, entityType);
        this.min_scaling = getDoubleSafe(json, "min_scaling", entityType);
        this.max_scaling = getDoubleSafe(json, "max_scaling", entityType);
        ensureMinSmallerMax(min_scaling, max_scaling, entityType);
    }

    public UniformScalingSampler(double min_scaling, double max_scaling, AttributeScalingTypes health, AttributeScalingTypes damage, AttributeScalingTypes speed) {
        super(health, damage, speed);
        this.min_scaling = min_scaling;
        this.max_scaling = max_scaling;
    }

    @Override
    protected double sampleScalingFactor(RandomSource random, Difficulty difficulty) {
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