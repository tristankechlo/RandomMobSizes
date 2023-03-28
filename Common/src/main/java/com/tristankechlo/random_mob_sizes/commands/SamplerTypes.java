package com.tristankechlo.random_mob_sizes.commands;

import com.google.gson.JsonElement;
import com.tristankechlo.random_mob_sizes.sampler.GaussianSampler;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.UniformScalingSampler;
import net.minecraft.util.StringRepresentable;

public enum SamplerTypes implements StringRepresentable {

    UNIFORM("uniform", UniformScalingSampler::new, UniformScalingSampler::new),
    GAUSSIAN("gaussian", GaussianSampler::new, GaussianSampler::new);

    @SuppressWarnings("deprecation")
    public static final StringRepresentable.EnumCodec<SamplerTypes> CODEC;
    private final String name;
    private final IDeserializer deserializer;
    private final IConstructor constructor;

    private SamplerTypes(String name, IDeserializer deserializer, IConstructor constructor) {
        this.name = name;
        this.deserializer = deserializer;
        this.constructor = constructor;
    }

    public static SamplerTypes byName(String name, SamplerTypes fallback) {
        SamplerTypes type = CODEC.byName(name);
        return type != null ? type : fallback;
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
        CODEC = StringRepresentable.fromEnum(SamplerTypes::values);
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