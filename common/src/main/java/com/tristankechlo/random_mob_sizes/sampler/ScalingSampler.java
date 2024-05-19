package com.tristankechlo.random_mob_sizes.sampler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.tristankechlo.random_mob_sizes.RandomMobSizes;
import com.tristankechlo.random_mob_sizes.sampler.types.StaticScalingSampler;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public abstract class ScalingSampler {

    public static final double MINIMUM_SCALING = ((RangedAttribute) Attributes.SCALE.value()).getMinValue();
    public static final double MAXIMUM_SCALING = ((RangedAttribute) Attributes.SCALE.value()).getMaxValue();
    protected final AttributeScalingTypes scaleHealth;
    protected final AttributeScalingTypes scaleDamage;
    protected final AttributeScalingTypes scaleSpeed;
    protected boolean addAttributeModifiers = true;
    protected boolean shouldScaleLoot = true;
    protected boolean shouldScaleXP = true;

    protected ScalingSampler() {
        this(AttributeScalingTypes.NORMAL, AttributeScalingTypes.NORMAL, AttributeScalingTypes.INVERSE);
    }

    protected ScalingSampler(AttributeScalingTypes health, AttributeScalingTypes damage, AttributeScalingTypes speed) {
        this.scaleHealth = health;
        this.scaleDamage = damage;
        this.scaleSpeed = speed;
    }

    protected ScalingSampler(JsonElement jsonElement, String entityType) {
        JsonObject json = GsonHelper.convertToJsonObject(jsonElement, entityType);
        this.scaleHealth = AttributeScalingTypes.byName(GsonHelper.getAsString(json, "scale_health", "none"));
        this.scaleDamage = AttributeScalingTypes.byName(GsonHelper.getAsString(json, "scale_damage", "none"));
        this.scaleSpeed = AttributeScalingTypes.byName(GsonHelper.getAsString(json, "scale_speed", "none"));
        this.shouldScaleLoot = getBooleanSafe(json, "scale_loot", entityType, true);
        this.shouldScaleXP = getBooleanSafe(json, "scale_xp", entityType, true);
    }

    protected abstract double sampleScalingFactor(RandomSource random, Difficulty difficulty);

    public final double sample(RandomSource random, Difficulty difficulty) {
        double value = sampleScalingFactor(random, difficulty);
        return Mth.clamp(value, MINIMUM_SCALING, MAXIMUM_SCALING);
    }

    protected abstract JsonObject serialize(JsonObject json);

    public final JsonElement serialize() {
        JsonObject json = new JsonObject();
        json = serialize(json);
        if (this.addAttributeModifiers) {
            json.addProperty("scale_loot", this.shouldScaleLoot);
            json.addProperty("scale_xp", this.shouldScaleXP);
            json.addProperty("scale_health", scaleHealth.getSerializedName());
            json.addProperty("scale_damage", scaleDamage.getSerializedName());
            json.addProperty("scale_speed", scaleSpeed.getSerializedName());
        }
        return json;
    }

    public boolean shouldScaleHealth() {
        return scaleHealth != AttributeScalingTypes.NONE;
    }

    public AttributeScalingTypes getHealthScaler() {
        return scaleHealth;
    }

    public boolean shouldScaleDamage() {
        return scaleDamage != AttributeScalingTypes.NONE;
    }

    public AttributeScalingTypes getDamageScaler() {
        return scaleDamage;
    }

    public boolean shouldScaleSpeed() {
        return scaleSpeed != AttributeScalingTypes.NONE;
    }

    public AttributeScalingTypes getSpeedScaler() {
        return scaleSpeed;
    }

    public boolean shouldScaleLoot() {
        return shouldScaleLoot;
    }

    public boolean shouldScaleXP() {
        return shouldScaleXP;
    }

    public static ScalingSampler deserializeSampler(JsonElement jsonElement, String entityType) {
        if (GsonHelper.isNumberValue(jsonElement)) {
            return new StaticScalingSampler(jsonElement.getAsDouble());
        } else if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, entityType);
            String type = jsonObject.get("type").getAsString();
            SamplerTypes samplerType = SamplerTypes.byName(type, null);
            if (samplerType == null) {
                throw new JsonParseException("Unknown ScalingType: " + type);
            }
            return samplerType.fromJson(jsonElement, entityType);
        }
        throw new JsonParseException("ScalingType must be a NumberValue or JsonObject");
    }

    protected static void ensureMinSmallerMax(double min, double max, String entityType) {
        if (min >= max) {
            throw new JsonParseException("'min_scaling' must be smaller than 'max_scaling' for '" + entityType + "' (can not be equal)");
        }
    }

    protected static double getDoubleSafe(JsonObject json, String key, String entityType) {
        return getDoubleSafe(json, key, MINIMUM_SCALING, MAXIMUM_SCALING, entityType);
    }

    protected static double getDoubleSafe(JsonObject json, String key, double min, double max, String entityType) {
        double temp = GsonHelper.getAsDouble(json, key);
        if (isDoubleOutOfBounds(temp, min, max)) {
            temp = Mth.clamp(temp, min, max);
            RandomMobSizes.LOGGER.error("'{}' for '{}' is out of range[{} - {}], using {} instead", key, entityType, min, max, temp);
            //throw new JsonParseException("'" + key + "' for '" + entityType + "' is out of range[" + min + " - " + max + "], using " + temp + " instead");
        }
        return temp;
    }

    protected static boolean isDoubleOutOfBounds(double value, double min, double max) {
        return value < min || value > max;
    }

    private boolean getBooleanSafe(JsonObject json, String key, String entityType, boolean def) {
        if (GsonHelper.isBooleanValue(json, key)) {
            return GsonHelper.getAsBoolean(json, key);
        } else {
            RandomMobSizes.LOGGER.error("Expected '{}' to be a Boolean", key);
            RandomMobSizes.LOGGER.error("Error while parsing config value '{}' for entity '{}', using default value.", key, entityType);
        }
        return def;
    }

}