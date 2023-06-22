package com.tristankechlo.random_mob_sizes.mixin.entities;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Silverfish;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Silverfish.class)
public abstract class SilverfishMixin {

    @Inject(method = "getStandingEyeHeight", at = @At("RETURN"), cancellable = true)
    private void getStandingEyeHeight$RandomMobSizes(Pose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(dimensions.height * 0.4333F);
    }

}
