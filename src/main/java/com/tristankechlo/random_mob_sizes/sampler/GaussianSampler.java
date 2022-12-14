package com.tristankechlo.random_mob_sizes.sampler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.Mth;

import java.util.Random;

public class GaussianSampler extends ScalingSampler {

    private static final double MEAN = 0.5D;
    private static final double STD_DEV = 0.15D;
    private static final Random RANDOM = new Random();
    public static final String TYPE = "gaussian";
    private final float min_scaling;
    private final float max_scaling;

    public GaussianSampler(float min_scaling, float max_scaling) {
        this.min_scaling = min_scaling;
        this.max_scaling = max_scaling;
    }

    public GaussianSampler(JsonElement jsonElement, String entityType) {
        if (!jsonElement.isJsonObject()) {
            throw new JsonParseException("Expected to get a JsonObject for " + entityType + ", but got a " + jsonElement.getClass().getSimpleName());
        }
        JsonObject json = jsonElement.getAsJsonObject();
        this.min_scaling = json.get("min_scaling").getAsFloat();
        this.max_scaling = json.get("max_scaling").getAsFloat();
    }

    @Override
    protected float sampleScalingFactor() {
        return min_scaling + (float) (randomGaussian() * (max_scaling - min_scaling));
    }

    /* returns a random number from a Gaussian distribution, clamped to [0, 1] */
    private double randomGaussian() {
        double value = MEAN + (RANDOM.nextGaussian() * STD_DEV);
        return Mth.clamp(value, 0.0D, 1.0D);
    }

    @Override
    public JsonElement serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("type", TYPE);
        json.addProperty("min_scaling", min_scaling);
        json.addProperty("max_scaling", max_scaling);
        return json;
    }

}
