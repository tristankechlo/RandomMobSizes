package com.tristankechlo.random_mob_sizes.sampler.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;


public class GaussianScalingSampler extends ScalingSampler {

    private static final double STD_DEV = 0.13D;
    public static final String TYPE = "gaussian";
    private final float min_scaling;
    private final float max_scaling;
    private final float mean;
    private final boolean close_to_original;

    public GaussianScalingSampler(float min_scaling, float max_scaling) {
        this.min_scaling = min_scaling;
        this.max_scaling = max_scaling;
        this.close_to_original = false;
        this.mean = 0.5F;
    }

    public GaussianScalingSampler(JsonElement jsonElement, String entityType) {
        super(jsonElement, entityType);
        JsonObject json = GsonHelper.convertToJsonObject(jsonElement, entityType);

        this.min_scaling = getFloatSafe(json, "min_scaling", entityType);
        this.max_scaling = getFloatSafe(json, "max_scaling", entityType);
        ensureMinSmallerMax(min_scaling, max_scaling, entityType);

        this.close_to_original = GsonHelper.getAsBoolean(json, "close_to_original", false);

        if (this.close_to_original) {
            if (min_scaling >= 1.0F) {
                throw new IllegalArgumentException("close_to_original is true, min_scaling needs to be smaller than 1.0");
            }
            if (max_scaling <= 1.0F) {
                throw new IllegalArgumentException("close_to_original is true, max_scaling needs to be bigger than 1.0");
            }
            // calculate the mean, so that the average will be at 1.0F
            this.mean = Mth.clamp(((1.0F - min_scaling) / (max_scaling - min_scaling)), 0.0F, 1.0F);
        } else {
            // mean will be in the center between min and max
            this.mean = 0.5F;
        }
    }

    @Override
    protected float sampleScalingFactor(RandomSource random, Difficulty difficulty) {
        return (float) (this.min_scaling + (randomGaussian(random) * (this.max_scaling - this.min_scaling)));
    }

    /* returns a random number from a Gaussian distribution, clamped to [0, 1] */
    private double randomGaussian(RandomSource random) {
        double value = this.mean + (random.nextGaussian() * STD_DEV);
        return Mth.clamp(value, 0.0D, 1.0D);
    }

    @Override
    protected JsonObject serialize(JsonObject json) {
        json.addProperty("type", TYPE);
        json.addProperty("min_scaling", min_scaling);
        json.addProperty("max_scaling", max_scaling);
        json.addProperty("close_to_original", close_to_original);
        return json;
    }

}
