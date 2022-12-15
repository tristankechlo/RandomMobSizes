package com.tristankechlo.random_mob_sizes.mixin;

import com.tristankechlo.random_mob_sizes.mixin_access.MobMixinAddon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* reapply all EntityDimensions scaling factors, after vanilla changes them */
@Mixin(Entity.class)
public abstract class EntityMixin {

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
