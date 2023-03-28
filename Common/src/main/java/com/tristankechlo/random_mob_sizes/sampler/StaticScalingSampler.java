package com.tristankechlo.random_mob_sizes.sampler;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

public class StaticScalingSampler extends ScalingSampler {

    private final float static_scaling;

    public StaticScalingSampler(JsonElement jsonElement, String entityType) {
        if (!jsonElement.isJsonPrimitive()) {
            throw new JsonParseException("Expected to get a JsonPrimitive for " + entityType + ", but got a " + jsonElement.getClass().getSimpleName());
        }
        this.static_scaling = jsonElement.getAsFloat();
    }

    public StaticScalingSampler(float static_scaling) {
        this.static_scaling = static_scaling;
    }

    @Override
    protected float sampleScalingFactor() {
        return static_scaling;
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(static_scaling);
    }

}
