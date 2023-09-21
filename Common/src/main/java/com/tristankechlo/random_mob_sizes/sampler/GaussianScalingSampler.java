package com.tristankechlo.random_mob_sizes.sampler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class GaussianScalingSampler extends ScalingSampler {

    private static final double MEAN = 0.5D;
    private static final double STD_DEV = 0.13D;
    public static final String TYPE = "gaussian";
    private final float min_scaling;
    private final float max_scaling;

    public GaussianScalingSampler(float min_scaling, float max_scaling) {
        this.min_scaling = min_scaling;
        this.max_scaling = max_scaling;
    }

    public GaussianScalingSampler(JsonElement jsonElement, String entityType) {
        super(jsonElement, entityType);
        JsonObject json = GsonHelper.convertToJsonObject(jsonElement, entityType);
        this.min_scaling = getFloatSafe(json, "min_scaling", entityType);
        this.max_scaling = getFloatSafe(json, "max_scaling", entityType);
        ensureMinSmallerMax(min_scaling, max_scaling, entityType);
    }

    @Override
    protected float sampleScalingFactor(RandomSource random) {
        return (float) (min_scaling + (randomGaussian(random) * (max_scaling - min_scaling)));
    }

    /* returns a random number from a Gaussian distribution, clamped to [0, 1] */
    private double randomGaussian(RandomSource random) {
        double value = MEAN + (random.nextGaussian() * STD_DEV);
        return Mth.clamp(value, 0.0D, 1.0D);
    }

    @Override
    protected JsonObject serialize(JsonObject json) {
        json.addProperty("type", TYPE);
        json.addProperty("min_scaling", min_scaling);
        json.addProperty("max_scaling", max_scaling);
        return json;
    }

}
