package com.tristankechlo.random_mob_sizes.mixin_access;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;

public interface EyeHeightAddon {

    float getStandingEyeHeight(Pose pose, EntityDimensions dimensions);

}
