package com.tristankechlo.random_mob_sizes.mixin_access;

import net.minecraft.network.syncher.EntityDataAccessor;

public interface MobMixinAddon {

    float getMobScaling();

    void setMobScaling(float scale);

    EntityDataAccessor<Float> getTracker(); //needed for accessing the tracker from EntityMixin

}
