package com.tristankechlo.random_mob_sizes.mixin;

import com.tristankechlo.random_mob_sizes.mixin_access.MobMixinAddon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* reapply all EntityDimensions scaling factors, after vanilla changes them */
@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(at = @At("RETURN"), method = "getDimensions", cancellable = true)
    private void getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        // normally getDimensions() uses the dimensions from the entityType, so we need to apply scaling here
        if (((Entity) (Object) this) instanceof MobEntity) {
            float scaleFactor = ((MobMixinAddon) this).getScaleFactor();
            cir.setReturnValue(cir.getReturnValue().scaled(scaleFactor));
        }
    }

    @Inject(at = @At("RETURN"), method = "onTrackedDataSet")
    private void onSyncedDataUpdated(TrackedData<?> data, CallbackInfo ci) {
        if (((Entity) (Object) this) instanceof MobEntity) {
            MobMixinAddon mob = (MobMixinAddon) this;
            if (data.equals(mob.getTracker())) {
                ((MobEntity) (Object) this).calculateDimensions();
            }
        }
    }

}
