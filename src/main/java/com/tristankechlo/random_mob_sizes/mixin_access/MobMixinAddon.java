package com.tristankechlo.random_mob_sizes.mixin_access;

import net.minecraft.entity.data.TrackedData;

public interface MobMixinAddon {

    float getMobScaling();

    void setMobScaling(float scale);

    TrackedData<Float> getTracker(); //needed for accessing the tracker from EntityMixin

}
