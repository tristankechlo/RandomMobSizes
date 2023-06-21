package com.tristankechlo.random_mob_sizes.commands;

import com.google.gson.JsonElement;
import com.tristankechlo.random_mob_sizes.sampler.GaussianSampler;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.UniformScalingSampler;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.Map;

public enum SamplerTypes implements StringRepresentable {

    UNIFORM("uniform", UniformScalingSampler::new, UniformScalingSampler::new),
    GAUSSIAN("gaussian", GaussianSampler::new, GaussianSampler::new);

    public static final Map<String, SamplerTypes> BY_NAME;
    private final String name;
    private final IDeserializer deserializer;
    private final IConstructor constructor;

    private SamplerTypes(String name, IDeserializer deserializer, IConstructor constructor) {
        this.name = name;
        this.deserializer = deserializer;
        this.constructor = constructor;
    }

    public static SamplerTypes byName(String name, SamplerTypes fallback) {
        return BY_NAME.getOrDefault(name, fallback);
    }

    public ScalingSampler fromJson(JsonElement json, String entityType) {
        return deserializer.apply(json, entityType);
    }

    public ScalingSampler create(float minScaling, float maxScaling) {
        return constructor.create(minScaling, maxScaling);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    static {
        BY_NAME = Arrays.stream(values()).collect(java.util.stream.Collectors.toMap(SamplerTypes::getSerializedName, (type) -> type));
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