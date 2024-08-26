package com.tristankechlo.random_mob_sizes.sampler;

import net.minecraft.util.StringRepresentable;

import java.util.Objects;
import java.util.function.Function;

public enum AttributeScalingTypes implements StringRepresentable {

    NONE("none", (value) -> 1.0),
    NORMAL("normal", (value) -> value),
    SQUARE("square", (value) -> (value * value)),
    INVERSE("inverse", (value) -> (1.0 / value)),
    INVERSE_SQUARE("inverse_square", (value) -> (1.0 / (value * value))),
    SQUARE_HALVED("square_halved", (value) -> (1.0F + (((value * value) - 1.0F) * 0.5F))),
    INVERSE_HALVED("inverse_halved", (value) -> (1.0F + (((1.0F / value) - 1.0F) * 0.5F))),
    INVERSE_SQUARE_HALVED("inverse_square_halved", (value) -> (1.0F + (((1.0F / (value * value)) - 1.0F) * 0.5F)));

    @SuppressWarnings("deprecation")
    public static final EnumCodec<AttributeScalingTypes> CODEC = StringRepresentable.fromEnum(AttributeScalingTypes::values);
    private final String name;
    private final Function<Double, Double> modifier;

    private AttributeScalingTypes(String name, Function<Double, Double> modifier) {
        this.name = name;
        this.modifier = modifier;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public double apply(double value) {
        return modifier.apply(value);
    }

    public static AttributeScalingTypes byName(String name) {
        return Objects.requireNonNull(CODEC.byName(name), "Unknown scaling type '" + name + "'");
    }

}
