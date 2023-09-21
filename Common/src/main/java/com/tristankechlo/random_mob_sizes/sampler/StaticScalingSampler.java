package com.tristankechlo.random_mob_sizes.sampler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;

public class StaticScalingSampler extends ScalingSampler {

    public static final String TYPE = "static";
    private final float static_scaling;

    public StaticScalingSampler(float static_scaling) {
        this.static_scaling = static_scaling;
    }

    public StaticScalingSampler(JsonElement jsonElement, String entityType) {
        super(jsonElement, entityType);
        if (GsonHelper.isNumberValue(jsonElement)) {
            this.static_scaling = jsonElement.getAsFloat();
        } else {
            JsonObject json = GsonHelper.convertToJsonObject(jsonElement, entityType);
            this.static_scaling = getFloatSafe(json, "scaling", entityType);
        }
    }

    public StaticScalingSampler(float static_scaling, AttributeScalingTypes health, AttributeScalingTypes damage, AttributeScalingTypes speed) {
        super(health, damage, speed);
        this.static_scaling = static_scaling;
    }

    @Override
    protected float sampleScalingFactor(RandomSource random) {
        return static_scaling;
    }

    @Override
    protected JsonObject serialize(JsonObject json) {
        json.addProperty("type", TYPE);
        json.addProperty("scaling", static_scaling);
        return json;
    }

}
