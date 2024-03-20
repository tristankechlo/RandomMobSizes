package com.tristankechlo.random_mob_sizes.sampler.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tristankechlo.random_mob_sizes.RandomMobSizes;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import net.minecraft.core.NonNullList;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;

public class DifficultyScalingSampler extends ScalingSampler {

    public static final String TYPE = "difficulty";
    protected final NonNullList<ScalingSampler> samplers;

    public DifficultyScalingSampler(NonNullList<ScalingSampler> samplers) {
        this.samplers = samplers;
        this.addAttributeModifiers = false; // do not add 'scale_health' etc. attribute modifiers to the base json
    }

    public DifficultyScalingSampler(JsonElement jsonElement, String entityType) {
        JsonObject json = GsonHelper.convertToJsonObject(jsonElement, entityType);
        this.samplers = NonNullList.withSize(Difficulty.values().length, new StaticScalingSampler(1.0F));

        for (Difficulty difficulty : Difficulty.values()) {
            String key = difficulty.getSerializedName();
            if (!json.has(key)) {
                RandomMobSizes.LOGGER.warn("Difficulty scaling sampler for entity type: '{}' and difficulty: '{}' is missing, using fallback instead", entityType, key);
                continue;
            }
            try {
                ScalingSampler sampler = ScalingSampler.deserializeSampler(json.get(key), entityType);
                samplers.set(difficulty.getId(), sampler);
            } catch (Exception e) {
                RandomMobSizes.LOGGER.error("Error while parsing the difficulty scaling sampler for entity type: '{}' and difficulty: '{}'", entityType, key);
                // rethrow the exception, when parsed by defaultSampler, the exception will be caught and will use the default sampler instead
                // when parsed by scalingOverrider, the exception will be caught and the sampler will be ignored
                // either way, the exception will be caught/logged and the config will be backed up
                throw e;
            }
        }
        this.addAttributeModifiers = false; // do not add 'scale_health' etc. attribute modifiers to the base json
    }

    @Override
    protected float sampleScalingFactor(RandomSource random, Difficulty difficulty) {
        return samplers.get(difficulty.getId()).sample(random, difficulty);
    }

    @Override
    protected JsonObject serialize(JsonObject json) {
        json.addProperty("type", TYPE);
        for (Difficulty difficulty : Difficulty.values()) {
            JsonElement difficultyJson = samplers.get(difficulty.getId()).serialize();
            json.add(difficulty.getSerializedName(), difficultyJson);
        }
        return json;
    }

}