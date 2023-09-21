package com.tristankechlo.random_mob_sizes.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tristankechlo.random_mob_sizes.mixin.CompoundTagInvoker;
import com.tristankechlo.random_mob_sizes.sampler.GaussianScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.StaticScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.UniformScalingSampler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public enum SamplerTypes implements StringRepresentable {

    STATIC(StaticScalingSampler.TYPE, StaticScalingSampler::new),
    UNIFORM(UniformScalingSampler.TYPE, UniformScalingSampler::new),
    GAUSSIAN(GaussianScalingSampler.TYPE, GaussianScalingSampler::new);

    public static final Map<String, SamplerTypes> BY_NAME;
    private final String name;
    private final BiFunction<JsonElement, String, ScalingSampler> jsonDeserializer;

    private SamplerTypes(String name, BiFunction<JsonElement, String, ScalingSampler> fromJson) {
        this.name = name;
        this.jsonDeserializer = fromJson;
    }

    public static SamplerTypes byName(String name, SamplerTypes fallback) {
        return BY_NAME.getOrDefault(name, fallback);
    }

    public ScalingSampler fromJson(JsonElement json, String entityType) {
        return this.jsonDeserializer.apply(json, entityType);
    }

    public ScalingSampler fromNBT(CompoundTag nbt, String entityType) {
        Map<String, Tag> map = ((CompoundTagInvoker) nbt).getEntries();
        JsonObject json = new JsonObject();
        for (Map.Entry<String, Tag> entry : map.entrySet()) {
            final int id = entry.getValue().getId();
            if (id == Tag.TAG_FLOAT || id == Tag.TAG_DOUBLE || id == Tag.TAG_INT || id == Tag.TAG_LONG) {
                json.addProperty(entry.getKey(), ((NumericTag) entry.getValue()).getAsNumber());
            }
            if (id == Tag.TAG_BYTE) {
                json.addProperty(entry.getKey(), ((NumericTag) entry.getValue()).getAsByte() != 0);
            }
            if (id == Tag.TAG_STRING) {
                json.addProperty(entry.getKey(), entry.getValue().getAsString());
            }
        }
        return this.fromJson(json, entityType);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    static {
        BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(SamplerTypes::getSerializedName, (type) -> type));
    }

}
