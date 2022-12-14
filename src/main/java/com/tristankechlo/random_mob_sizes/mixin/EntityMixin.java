package com.tristankechlo.random_mob_sizes.mixin;

import com.tristankechlo.random_mob_sizes.RandomMobSizesMod;
import com.tristankechlo.random_mob_sizes.mixin_access.MobMixinAddon;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* reapply all EntityDimensions scaling factors, after vanilla changes them */
@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(at = @At("RETURN"), method = "getDimensions", cancellable = true)
    private void getDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        // normally getDimensions() uses the dimensions from the entityType, so we need to apply scaling here
        if (((Entity) (Object) this) instanceof Mob) {
            float scaleFactor = ((MobMixinAddon) this).getScaleFactor();
            cir.setReturnValue(cir.getReturnValue().scale(scaleFactor));
        }
    }

    @Inject(at = @At("RETURN"), method = "onSyncedDataUpdated")
    private void onSyncedDataUpdated(EntityDataAccessor<?> data, CallbackInfo ci) {
        if (((Entity) (Object) this) instanceof Mob) {
            if (data.equals(RandomMobSizesMod.SCALING)) {
                ((Mob) (Object) this).refreshDimensions();
            }
        }
    }

}
