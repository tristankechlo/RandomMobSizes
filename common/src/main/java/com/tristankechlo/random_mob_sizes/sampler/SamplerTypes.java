package com.tristankechlo.random_mob_sizes.sampler;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.tristankechlo.random_mob_sizes.sampler.types.DifficultyScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.types.GaussianScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.types.StaticScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.types.UniformScalingSampler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;

import java.util.Optional;
import java.util.function.BiFunction;

public enum SamplerTypes implements StringRepresentable {

    STATIC(StaticScalingSampler.TYPE, StaticScalingSampler::new),
    UNIFORM(UniformScalingSampler.TYPE, UniformScalingSampler::new),
    GAUSSIAN(GaussianScalingSampler.TYPE, GaussianScalingSampler::new),
    DIFFICULTY(DifficultyScalingSampler.TYPE, DifficultyScalingSampler::new);

    @SuppressWarnings("deprecation")
    public static final StringRepresentable.EnumCodec<SamplerTypes> CODEC;
    private final String name;
    private final BiFunction<JsonElement, String, ScalingSampler> jsonDeserializer;

    private SamplerTypes(String name, BiFunction<JsonElement, String, ScalingSampler> fromJson) {
        this.name = name;
        this.jsonDeserializer = fromJson;
    }

    public static SamplerTypes byName(String name, SamplerTypes fallback) {
        SamplerTypes type = CODEC.byName(name);
        return type != null ? type : fallback;
    }

    public ScalingSampler fromJson(JsonElement json, String entityType) {
        return this.jsonDeserializer.apply(json, entityType);
    }

    public ScalingSampler fromNBT(CompoundTag nbt, String entityType) {
        Optional<JsonElement> json = CompoundTag.CODEC.encodeStart(JsonOps.INSTANCE, nbt).result();
        return this.fromJson(json.orElseThrow(), entityType);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    static {
        CODEC = StringRepresentable.fromEnum(SamplerTypes::values);
    }

}
