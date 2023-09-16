package com.tristankechlo.random_mob_sizes.mixin_access;

import net.minecraft.network.syncher.EntityDataAccessor;

public interface MobMixinAddon {

    float getMobScaling$RandomMobSizes();

    void setMobScaling$RandomMobSizes(float scale);

    EntityDataAccessor<Float> getTracker$RandomMobSizes(); //needed for accessing the tracker from EntityMixin

}
