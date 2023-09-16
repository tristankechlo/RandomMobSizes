package com.tristankechlo.random_mob_sizes.sampler;

import com.google.gson.JsonElement;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public abstract class ScalingSampler {

    public static final float MINIMUM_SCALING = 0.1F;
    public static final float MAXIMUM_SCALING = 10.0F;

    protected abstract float sampleScalingFactor(RandomSource random);

    public abstract JsonElement serialize();

    public final float sample(RandomSource random) {
        return Mth.clamp(sampleScalingFactor(random), MINIMUM_SCALING, MAXIMUM_SCALING);
    }

}