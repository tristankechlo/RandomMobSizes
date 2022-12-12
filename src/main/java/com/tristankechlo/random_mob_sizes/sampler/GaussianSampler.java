package com.tristankechlo.random_mob_sizes.sampler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;

import java.util.Random;

public class GaussianSampler extends ScalingSampler {

    public static final String TYPE = "gaussian";
    private final Random random = new Random();
    final float min_scaling;
    final float max_scaling;
    final double mean = 0.5D;
    final double std_dev = 0.15D;

    public GaussianSampler(float min_scaling, float max_scaling) {
        this.min_scaling = min_scaling;
        this.max_scaling = max_scaling;
    }

    public GaussianSampler(EntityType<?> entityType, JsonElement jsonElement) {
        if (!jsonElement.isJsonObject()) {
            throw new JsonParseException("Expected to get a JsonObject for " + EntityType.getKey(entityType) + ", but got a " + jsonElement.getClass().getSimpleName());
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
        double value = mean + (random.nextGaussian() * std_dev);
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
