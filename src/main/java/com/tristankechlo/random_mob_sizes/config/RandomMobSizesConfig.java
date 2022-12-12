package com.tristankechlo.random_mob_sizes.config;

import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public final class RandomMobSizesConfig {

    public static Map<EntityType<?>, ScalingSampler> SETTINGS = new HashMap<>(); //TODO read from config file

}
