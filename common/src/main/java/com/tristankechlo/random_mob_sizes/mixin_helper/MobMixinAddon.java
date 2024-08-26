package com.tristankechlo.random_mob_sizes.mixin_helper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public interface MobMixinAddon {

    float getMobScaling$RandomMobSizes();

    void setMobScaling$RandomMobSizes(float scale);

    EntityDataAccessor<Float> getTracker$RandomMobSizes(); //needed for accessing the tracker from EntityMixin

    boolean shouldScaleLoot$RandomMobSizes();

    void setShouldScaleLoot$RandomMobSizes(boolean shouldScale);

    boolean shouldScaleXP$RandomMobSizes();

    void setShouldScaleXP$RandomMobSizes(boolean shouldScale);

    // called by CaveSpiderMixin to apply scaling to CaveSpiders
    void doFinalizeSpawn$RandomMobSizes(ServerLevelAccessor level);

}
