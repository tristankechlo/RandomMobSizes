package com.tristankechlo.random_mob_sizes.mixin_helper;

import net.minecraft.network.syncher.EntityDataAccessor;
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
