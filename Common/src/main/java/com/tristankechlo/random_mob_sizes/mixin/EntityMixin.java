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

/* reapply all EntityDimensions scaling factors, after vanilla changes them */
@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(at = @At("RETURN"), method = "getDimensions", cancellable = true)
    private void getDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        // normally getDimensions() uses the dimensions from the entityType, so we need to apply scaling here
        if (((Entity) (Object) this) instanceof Mob) {
            EntityDimensions dimensions = cir.getReturnValue();
            float scaling = ((MobMixinAddon) this).getMobScaling();
            EntityDimensions scaledDimensions = dimensions.scale(scaling);
            cir.setReturnValue(scaledDimensions);
        }
    }

    @Inject(at = @At("RETURN"), method = "onSyncedDataUpdated(Lnet/minecraft/network/syncher/EntityDataAccessor;)V")
    private void onSyncedDataUpdated(EntityDataAccessor<?> data, CallbackInfo ci) {
        if (((Entity) (Object) this) instanceof Mob) {
            MobMixinAddon mob = (MobMixinAddon) this;
            //update bounding box, when the scale factor changes
            if (data.equals(mob.getTracker())) {
                ((Entity) (Object) this).refreshDimensions();
            }
        }
    }

    @Redirect(method = "refreshDimensions", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;getDimensions(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/entity/EntityDimensions;"))
    private EntityDimensions getDimensions(Entity entity, Pose pose) {
        if (((Entity) (Object) this) instanceof Mob) {
            MobMixinAddon mob = (MobMixinAddon) this;
            float scaling = mob.getMobScaling();
            float scaleFactor = ((LivingEntity) (Object) this).getScale();
            EntityDimensions dimensions = entity.getType().getDimensions();
            return dimensions.scale(scaling).scale(scaleFactor);
        }
        return entity.getDimensions(pose);
    }

}
