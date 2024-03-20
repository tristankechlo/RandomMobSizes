package com.tristankechlo.random_mob_sizes.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.tristankechlo.random_mob_sizes.RandomMobSizes;
import com.tristankechlo.random_mob_sizes.sampler.AttributeScalingTypes;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.types.DifficultyScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.types.GaussianScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.types.StaticScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.types.UniformScalingSampler;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;

import java.util.*;

public final class ScalingOverrides {

    private static final String JSON_KEY = "scaling_overrides";
    private static Map<EntityType<?>, ScalingSampler> SETTINGS = new HashMap<>();

    public void setToDefault() {
        SETTINGS = new HashMap<>();
        SETTINGS.put(EntityType.COW, new GaussianScalingSampler(0.5F, 1.5F));
        SETTINGS.put(EntityType.SHEEP, new GaussianScalingSampler(0.5F, 1.5F));
        SETTINGS.put(EntityType.PIG, new GaussianScalingSampler(0.5F, 1.5F));
        SETTINGS.put(EntityType.CHICKEN, new UniformScalingSampler(0.5F, 1.5F, AttributeScalingTypes.SQUARE, AttributeScalingTypes.NONE, AttributeScalingTypes.INVERSE_SQUARE));
        SETTINGS.put(EntityType.FROG, new GaussianScalingSampler(0.5F, 1.5F));

        NonNullList<ScalingSampler> samplers = NonNullList.withSize(Difficulty.values().length, new StaticScalingSampler(1.0F));
        samplers.set(Difficulty.PEACEFUL.getId(), new StaticScalingSampler(1.3F));
        samplers.set(Difficulty.EASY.getId(), new StaticScalingSampler(1.15F));
        samplers.set(Difficulty.NORMAL.getId(), new StaticScalingSampler(1.0F));
        samplers.set(Difficulty.HARD.getId(), new StaticScalingSampler(0.75F));
        SETTINGS.put(EntityType.ZOMBIE, new DifficultyScalingSampler(samplers));
    }

    public void serialize(JsonObject json) {
        JsonObject scalingOverrides = new JsonObject();

        // sort settings by resource location
        List<Pair<ResourceLocation, ScalingSampler>> settings = SETTINGS.entrySet().parallelStream().map((entry) -> {
            ResourceLocation location = EntityType.getKey(entry.getKey());
            return new Pair<>(location, entry.getValue());
        }).sorted(Comparator.comparing(Pair::getFirst)).toList();

        // write to json
        settings.forEach((pair) -> {
            String location = pair.getFirst().toString();
            JsonElement element = pair.getSecond().serialize();
            scalingOverrides.add(location, element);
        });
        json.add(JSON_KEY, scalingOverrides);
    }

    public void deserialize(JsonObject jsonObject, Runnable setMakeBackup) {
        JsonObject json = GsonHelper.getAsJsonObject(jsonObject, JSON_KEY);
        Map<EntityType<?>, ScalingSampler> newSettings = new HashMap<>();
        json.entrySet().forEach((entry) -> {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            Optional<EntityType<?>> entityType = EntityType.byString(key);
            if (entityType.isEmpty()) {
                RandomMobSizes.LOGGER.error("Error loading config, skipping unknown entity: '{}'", key);
                setMakeBackup.run();
                return;
            }
            try {
                EntityType<?> type = entityType.get();
                ScalingSampler scalingSampler = ScalingSampler.deserializeSampler(value, key);
                newSettings.put(type, scalingSampler);
            } catch (Exception e) {
                RandomMobSizes.LOGGER.error("Error while parsing '{}', skipping scaling for entity '{}'", JSON_KEY, key);
                RandomMobSizes.LOGGER.error(e.getMessage());
                setMakeBackup.run();
            }
        });
        SETTINGS.clear();
        SETTINGS.putAll(newSettings);
    }

    public ScalingSampler getSampler(EntityType<?> entityType) {
        return SETTINGS.get(entityType);
    }

    public boolean setScalingSampler(EntityType<?> entityType, ScalingSampler scalingSampler) {
        if (!RandomMobSizes.isEntityTypeAllowed(entityType)) {
            return false;
        }
        SETTINGS.put(entityType, scalingSampler);
        return true;
    }

    public void removeScalingSampler(EntityType<?> entityType) {
        SETTINGS.remove(entityType);
    }

    public static Iterator<Map.Entry<EntityType<?>, ScalingSampler>> getIterator() {
        return SETTINGS.entrySet().iterator();
    }

}