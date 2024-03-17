package com.tristankechlo.random_mob_sizes.mixin;

import com.tristankechlo.random_mob_sizes.mixin_access.MobMixinAddon;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(at = @At("RETURN"), method = "getDimensions", cancellable = true)
    private void getDimensions$RandomMobSizes(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        // normally getDimensions() uses the dimensions from the entityType, so we need to apply scaling here
        if (((Entity) (Object) this) instanceof Mob) {
            EntityDimensions dimensions = cir.getReturnValue();
            float scaling = ((MobMixinAddon) this).getMobScaling$RandomMobSizes();
            EntityDimensions scaledDimensions = dimensions.scale(scaling);
            cir.setReturnValue(scaledDimensions);
        }
    }

    @Inject(at = @At("RETURN"), method = "onSyncedDataUpdated(Lnet/minecraft/network/syncher/EntityDataAccessor;)V")
    private void onSyncedDataUpdated$RandomMobSizes(EntityDataAccessor<?> data, CallbackInfo ci) {
        if (((Entity) (Object) this) instanceof Mob) {
            MobMixinAddon mob = (MobMixinAddon) this;
            //update bounding box, when the scale factor changes
            if (data.equals(mob.getTracker$RandomMobSizes())) {
                ((Entity) (Object) this).refreshDimensions();
            }
        }
    }

}
