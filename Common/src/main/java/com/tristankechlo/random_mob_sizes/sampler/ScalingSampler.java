package com.tristankechlo.random_mob_sizes.sampler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.tristankechlo.random_mob_sizes.commands.SamplerTypes;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public abstract class ScalingSampler {

    public static final float MINIMUM_SCALING = 0.1F;
    public static final float MAXIMUM_SCALING = 10.0F;
    protected final AttributeScalingTypes scaleHealth;
    protected final AttributeScalingTypes scaleDamage;
    protected final AttributeScalingTypes scaleSpeed;

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
    }

    protected abstract float sampleScalingFactor(RandomSource random);

    public final float sample(RandomSource random) {
        return Mth.clamp(sampleScalingFactor(random), MINIMUM_SCALING, MAXIMUM_SCALING);
    }

    protected abstract JsonObject serialize(JsonObject json);

    public final JsonElement serialize() {
        JsonObject json = new JsonObject();
        json = serialize(json);
        json.addProperty("scale_health", scaleHealth.getSerializedName());
        json.addProperty("scale_damage", scaleDamage.getSerializedName());
        json.addProperty("scale_speed", scaleSpeed.getSerializedName());
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

    public static ScalingSampler deserializeSampler(JsonElement jsonElement, String entityType) {
        if (GsonHelper.isNumberValue(jsonElement)) {
            return new StaticScalingSampler(jsonElement.getAsFloat());
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

}