package com.tristankechlo.random_mob_sizes.mixin_access;

import net.minecraft.entity.data.TrackedData;

public interface MobMixinAddon {

    float getScaleFactor();

    void setScaleFactor(Float scale);

    TrackedData<Float> getTracker(); //needed for accessing the tracker from EntityMixin

}
