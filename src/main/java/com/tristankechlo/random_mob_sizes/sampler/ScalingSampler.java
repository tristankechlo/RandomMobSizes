package com.tristankechlo.random_mob_sizes.sampler;

import com.google.gson.JsonElement;
import net.minecraft.util.math.MathHelper;

public abstract class ScalingSampler {

    private final float minimum = 0.1F;
    private final float maximum = 10.0F;

    protected abstract float sampleScalingFactor();

    public abstract JsonElement serialize();

    public float sample() {
        return MathHelper.clamp(sampleScalingFactor(), minimum, maximum);
    }

}