package com.tristankechlo.random_mob_sizes.sampler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.Random;

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
        this.min_scaling = GsonHelper.getAsFloat(json, "min_scaling");
        this.max_scaling = GsonHelper.getAsFloat(json, "max_scaling");
    }

    public UniformScalingSampler(float min_scaling, float max_scaling, AttributeScalingTypes health, AttributeScalingTypes damage, AttributeScalingTypes speed) {
        super(health, damage, speed);
        this.min_scaling = min_scaling;
        this.max_scaling = max_scaling;
    }

    @Override
    protected float sampleScalingFactor(Random random) {
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
