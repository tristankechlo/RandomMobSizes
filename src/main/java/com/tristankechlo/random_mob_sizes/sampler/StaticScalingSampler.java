package com.tristankechlo.random_mob_sizes.sampler;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class StaticScalingSampler extends ScalingSampler {

    final float static_scaling;

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
