package com.tristankechlo.random_mob_sizes.sampler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;

public class UniformScalingSampler extends ScalingSampler {

    public static final String TYPE = "uniform";
    private final float min_scaling;
    private final float max_scaling;

    public UniformScalingSampler(float min_scaling, float max_scaling) {
        this.min_scaling = min_scaling;
        this.max_scaling = max_scaling;
    }

    public UniformScalingSampler(JsonElement jsonElement, String entityType) {
        if (!jsonElement.isJsonObject()) {
            throw new JsonParseException("Expected to get a JsonObject for " + entityType + ", but got a " + jsonElement.getClass().getSimpleName());
        }
        JsonObject json = jsonElement.getAsJsonObject();
        this.min_scaling = GsonHelper.getAsFloat(json, "min_scaling");
        this.max_scaling = GsonHelper.getAsFloat(json, "max_scaling");
    }

    @Override
    protected float sampleScalingFactor(RandomSource random) {
        return min_scaling + (float) (random.nextDouble() * (max_scaling - min_scaling));
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