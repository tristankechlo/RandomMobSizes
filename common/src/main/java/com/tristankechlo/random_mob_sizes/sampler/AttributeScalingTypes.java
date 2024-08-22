package com.tristankechlo.random_mob_sizes.sampler;

import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum AttributeScalingTypes implements StringRepresentable {

    NONE("none", (value) -> 1.0F),
    NORMAL("normal", (value) -> value),
    SQUARE("square", (value) -> (value * value)),
    INVERSE("inverse", (value) -> (1.0F / value)),
    INVERSE_SQUARE("inverse_square", (value) -> (1.0F / (value * value))),
    SQUARE_HALVED("square_halved", (value) -> (1.0F + (((value * value) - 1.0F) * 0.5F))),
    INVERSE_HALVED("inverse_halved", (value) -> (1.0F + (((1.0F / value) - 1.0F) * 0.5F))),
    INVERSE_SQUARE_HALVED("inverse_square_halved", (value) -> (1.0F + (((1.0F / (value * value)) - 1.0F) * 0.5F)));


    public static final Map<String, AttributeScalingTypes> BY_NAME;
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
        return Objects.requireNonNull(BY_NAME.get(name), "Unknown scaling type '" + name + "'");
    }

    static {
        BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(AttributeScalingTypes::getSerializedName, (type) -> type));
    }

}
