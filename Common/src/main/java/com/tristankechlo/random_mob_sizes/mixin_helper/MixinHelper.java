package com.tristankechlo.random_mob_sizes.mixin_helper;

import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;

import java.util.HashMap;
import java.util.Map;

public final class MixinHelper {

    public static final Map<Class<?>, Float> EYE_HEIGHTS = new HashMap<>();

    static {
        // AbstractSkeleton.class => 0.8743F
        EYE_HEIGHTS.put(Skeleton.class, 0.8743F);
        EYE_HEIGHTS.put(Stray.class, 0.8743F);
        EYE_HEIGHTS.put(WitherSkeleton.class, 0.875F);

        // AbstractSkeleton.class => 0.8743F
        EYE_HEIGHTS.put(Villager.class, 0.8307F);
        EYE_HEIGHTS.put(WanderingTrader.class, 0.8307F);

        // zombies
        EYE_HEIGHTS.put(Zombie.class, 0.8923F);
        EYE_HEIGHTS.put(Drowned.class, 0.8923F);
        EYE_HEIGHTS.put(Husk.class, 0.8923F);
        EYE_HEIGHTS.put(ZombifiedPiglin.class, 0.8923F);
        EYE_HEIGHTS.put(ZombieVillager.class, 0.9179F);

        // spider
        EYE_HEIGHTS.put(Spider.class, 0.7222F);
        EYE_HEIGHTS.put(CaveSpider.class, 0.9F);

        // cow
        EYE_HEIGHTS.put(Cow.class, 0.95F);
        EYE_HEIGHTS.put(MushroomCow.class, 0.95F);

        // other entities
        EYE_HEIGHTS.put(Dolphin.class, 0.5F);
        EYE_HEIGHTS.put(EnderMan.class, 0.8793F);
        EYE_HEIGHTS.put(Endermite.class, 0.4333F);
        EYE_HEIGHTS.put(Fox.class, 0.85F);
        EYE_HEIGHTS.put(Ghast.class, 0.65F);
        EYE_HEIGHTS.put(Piglin.class, 0.9179F);
        EYE_HEIGHTS.put(Shulker.class, 0.5F);
        EYE_HEIGHTS.put(Silverfish.class, 0.4333F);
        EYE_HEIGHTS.put(SnowGolem.class, 0.8947F);
        EYE_HEIGHTS.put(Witch.class, 0.8307F);
    }

}
