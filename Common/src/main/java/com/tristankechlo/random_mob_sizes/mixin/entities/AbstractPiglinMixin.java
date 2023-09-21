package com.tristankechlo.random_mob_sizes.mixin.entities;

import com.tristankechlo.random_mob_sizes.mixin_access.EyeHeightAddon;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractPiglin.class)
public abstract class AbstractPiglinMixin implements EyeHeightAddon {

    @Override
    public float getStandingEyeHeight$RandomMobSizes(Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.9179F;
    }

}
