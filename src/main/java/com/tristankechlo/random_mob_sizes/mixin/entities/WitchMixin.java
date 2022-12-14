package com.tristankechlo.random_mob_sizes.mixin.entities;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.mob.WitchEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WitchEntity.class)
public abstract class WitchMixin {

    @Inject(method = "getActiveEyeHeight", at = @At("RETURN"), cancellable = true)
    private void getStandingEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(dimensions.height * 0.8307F);
    }

}
