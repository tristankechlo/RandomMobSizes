package com.tristankechlo.random_mob_sizes.sampler.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tristankechlo.random_mob_sizes.RandomMobSizes;
import com.tristankechlo.random_mob_sizes.sampler.AttributeScalingTypes;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;

public class StaticScalingSampler extends ScalingSampler {

    public static final String TYPE = "static";
    private final double static_scaling;

    public StaticScalingSampler(double static_scaling) {
        this.static_scaling = static_scaling;
    }

    public StaticScalingSampler(JsonElement jsonElement, String entityType) {
        super(jsonElement, entityType);
        if (GsonHelper.isNumberValue(jsonElement)) {
            double scaling = jsonElement.getAsDouble();
            if (isDoubleOutOfBounds(scaling, MINIMUM_SCALING, MAXIMUM_SCALING)) {
                RandomMobSizes.LOGGER.error("'scaling' for '{}' is out of range[{} - {}], changing to {}", entityType, MINIMUM_SCALING, MAXIMUM_SCALING, scaling);
                scaling = Mth.clamp(scaling, MINIMUM_SCALING, MAXIMUM_SCALING);
            }
            this.static_scaling = scaling;
        } else {
            JsonObject json = GsonHelper.convertToJsonObject(jsonElement, entityType);
            this.static_scaling = getDoubleSafe(json, "scaling", entityType);
        }
    }

    public StaticScalingSampler(double static_scaling, AttributeScalingTypes health, AttributeScalingTypes damage, AttributeScalingTypes speed) {
        super(health, damage, speed);
        this.static_scaling = static_scaling;
    }

    @Override
    protected double sampleScalingFactor(RandomSource random, Difficulty difficulty) {
        return static_scaling;
    }

    @Override
    protected JsonObject serialize(JsonObject json) {
        json.addProperty("type", TYPE);
        json.addProperty("scaling", static_scaling);
        return json;
    }

}
