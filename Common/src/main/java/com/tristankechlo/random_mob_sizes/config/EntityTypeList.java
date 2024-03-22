package com.tristankechlo.random_mob_sizes.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.tristankechlo.random_mob_sizes.IPlatformHelper;
import com.tristankechlo.random_mob_sizes.RandomMobSizes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public final class EntityTypeList implements Supplier<List<EntityType<?>>> {

    private final String key;
    private ImmutableList<String> parsedValues;
    private ImmutableList<EntityType<?>> cachedValue;
    private final ImmutableList<String> defaultValue;

    public EntityTypeList(String key, List<String> stringValues) {
        this(key, stringValues, parseList(stringValues, key, () -> {}));
    }

    public EntityTypeList(String key, EntityType<?>... entityTypes) {
        this(key, Arrays.stream(entityTypes).map(EntityType::getKey).map(ResourceLocation::toString).toList(), List.of(entityTypes));
    }

    private EntityTypeList(String key, List<String> stringValues, List<EntityType<?>> entityTypes) {
        this.key = key;
        this.cachedValue = ImmutableList.copyOf(entityTypes);
        this.defaultValue = ImmutableList.copyOf(stringValues);
        this.parsedValues = ImmutableList.copyOf(stringValues);
    }

    public void setToDefault() {
        this.cachedValue = parseList(defaultValue, key, () -> {});
        this.parsedValues = ImmutableList.copyOf(defaultValue);
    }

    public void serialize(JsonObject json) {
        JsonArray array = new JsonArray();
        for (String value : parsedValues) {
            array.add(new JsonPrimitive(value));
        }
        json.add(key, array);
    }

    public void deserialize(JsonObject json, Runnable setMakeBackup) {
        try {
            JsonArray array = GsonHelper.getAsJsonArray(json, key);
            Iterator<JsonElement> elements = array.iterator();
            ImmutableList.Builder<String> builder = ImmutableList.builder();
            while (elements.hasNext()) {
                JsonElement element = elements.next();
                String value = GsonHelper.convertToString(element, key);
                builder.add(value);
            }
            this.parsedValues = builder.build();
            this.cachedValue = parseList(parsedValues, key, setMakeBackup);
        } catch (Exception e) {
            RandomMobSizes.LOGGER.error("Error while parsing config value '{}', using default value", key);
            RandomMobSizes.LOGGER.error(e.getMessage());
            setToDefault();
            setMakeBackup.run();
        }
    }

    @Override
    public List<EntityType<?>> get() {
        return cachedValue;
    }

    private static ImmutableList<EntityType<?>> parseList(List<String> values, String key, Runnable setMakeBackup) {
        ImmutableList.Builder<EntityType<?>> builder = ImmutableList.builder();
        for (String value : values) {
            if (value.endsWith(":*")) {
                //add all entities of the given namespace
                String namespace = value.substring(0, value.length() - 2);
                if (!IPlatformHelper.INSTANCE.isModLoaded(namespace)) {
                    RandomMobSizes.LOGGER.error("Skipping unknown wildcard: '{}' of config value '{}'", namespace, key);
                    setMakeBackup.run();
                    continue;
                }
                Registry.ENTITY_TYPE.stream()
                        .filter(entityType -> EntityType.getKey(entityType).getNamespace().equals(namespace))
                        .filter(RandomMobSizes::isEntityTypeAllowed)
                        .forEach(builder::add);
            } else {
                // parse as normal entity type
                Optional<EntityType<?>> type = EntityType.byString(value);
                if (type.isPresent()) {
                    if (RandomMobSizes.isEntityTypeAllowed(type.get())) {
                        builder.add(type.get());
                    } else {
                        RandomMobSizes.LOGGER.error("Skipping disabled EntityType: '{}' of config value '{}'", value, key);
                        setMakeBackup.run();
                    }
                } else {
                    RandomMobSizes.LOGGER.error("Skipping unknown EntityType: '{}' of config value '{}'", value, key);
                    setMakeBackup.run();
                }
            }
        }
        return builder.build();
    }

}
