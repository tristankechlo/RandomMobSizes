package com.tristankechlo.random_mob_sizes.commands;

import com.google.gson.JsonElement;
import com.tristankechlo.random_mob_sizes.sampler.GaussianSampler;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.UniformScalingSampler;

public enum SamplerTypes {

    UNIFORM("uniform", UniformScalingSampler::new, UniformScalingSampler::new),
    GAUSSIAN("gaussian", GaussianSampler::new, GaussianSampler::new);

    private final String name;
    private final IDeserializer deserializer;
    private final IConstructor constructor;

    private SamplerTypes(String name, IDeserializer deserializer, IConstructor constructor) {
        this.name = name;
        this.deserializer = deserializer;
        this.constructor = constructor;
    }

    public static SamplerTypes byName(String name) {
        for (SamplerTypes samplerType : values()) {
            if (samplerType.name.equals(name)) {
                return samplerType;
            }
        }
        return null;
    }

    public ScalingSampler fromJson(JsonElement json, String entityType) {
        return deserializer.apply(json, entityType);
    }

    public ScalingSampler create(float minScaling, float maxScaling) {
        return constructor.create(minScaling, maxScaling);
    }

    @FunctionalInterface
    public interface IDeserializer {
        ScalingSampler apply(JsonElement json, String entityType);
    }

    @FunctionalInterface
    public interface IConstructor {
        ScalingSampler create(float min_scaling, float max_scaling);
    }

}