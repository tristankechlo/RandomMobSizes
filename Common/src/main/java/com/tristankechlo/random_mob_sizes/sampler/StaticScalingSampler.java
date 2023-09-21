package com.tristankechlo.random_mob_sizes.sampler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tristankechlo.random_mob_sizes.RandomMobSizes;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
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
            float scaling = jsonElement.getAsFloat();
            if (isFloatOutOfBounds(scaling, MINIMUM_SCALING, MAXIMUM_SCALING)) {
                RandomMobSizes.LOGGER.error("'scaling' for '{}' is out of range[{} - {}], changing to {}", entityType, MINIMUM_SCALING, MAXIMUM_SCALING, scaling);
                scaling = Mth.clamp(scaling, MINIMUM_SCALING, MAXIMUM_SCALING);
            }
            this.static_scaling = scaling;
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
