package com.tristankechlo.random_mob_sizes.sampler;

import net.minecraft.util.StringRepresentable;

import java.util.Objects;
import java.util.function.Function;

public enum AttributeScalingTypes implements StringRepresentable {

    NONE("none", (value) -> 1.0F),
    NORMAL("normal", (value) -> value),
    SQUARED("squared", (value) -> (value * value)),
    INVERSE("inverse", (value) -> (1.0F / value)),
    INVERSE_SQUARED("inverse_squared", (value) -> (1.0F / (value * value)));

    @SuppressWarnings("deprecation")
    public static final EnumCodec<AttributeScalingTypes> CODEC = StringRepresentable.fromEnum(AttributeScalingTypes::values);
    private final String name;
    private final Function<Float, Float> modifier;

    private AttributeScalingTypes(String name, Function<Float, Float> modifier) {
        this.name = name;
        this.modifier = modifier;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public float apply(float value) {
        return modifier.apply(value);
    }

    public static AttributeScalingTypes byName(String name) {
        return Objects.requireNonNull(CODEC.byName(name), "Unknown scaling type '" + name + "'");
    }

}
