package com.tristankechlo.random_mob_sizes.config;

public abstract class ScalingSampler {
    public abstract float sampleScalingFactor();

    static class StaticScalingSampler extends ScalingSampler {

        final float static_scaling;

        public StaticScalingSampler(float static_scaling) {
            this.static_scaling = static_scaling;
        }

        @Override
        public float sampleScalingFactor() {
            return static_scaling;
        }
    }

    static class UniformScalingSampler extends ScalingSampler {

        final float min_scaling;
        final float max_scaling;

        public UniformScalingSampler(float min_scaling, float max_scaling) {
            this.min_scaling = min_scaling;
            this.max_scaling = max_scaling;
        }

        @Override
        public float sampleScalingFactor() {
            return min_scaling + (float) (Math.random() * (max_scaling - min_scaling));
        }
    }
}