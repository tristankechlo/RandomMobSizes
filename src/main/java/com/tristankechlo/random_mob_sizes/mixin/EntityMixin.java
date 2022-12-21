package com.tristankechlo.random_mob_sizes.mixin;

import com.tristankechlo.random_mob_sizes.mixin_access.MobMixinAddon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* reapply all EntityDimensions scaling factors, after vanilla changes them */
@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(at = @At("HEAD"), method = "getDimensions", cancellable = true)
    private void getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        // normally getDimensions() uses the dimensions from the entityType, so we need to apply scaling here
        if (((Entity) (Object) this) instanceof MobEntity) {
            EntityDimensions dimensions = cir.getReturnValue();
            float scaling = ((MobMixinAddon) this).getMobScaling();
            EntityDimensions scaledDimensions = dimensions.scaled(scaling);
            cir.setReturnValue(scaledDimensions);
        }
    }

    @Inject(at = @At("RETURN"), method = "onTrackedDataSet")
    private void onTrackedDataSet(TrackedData<?> data, CallbackInfo ci) {
        if (((Entity) (Object) this) instanceof MobEntity) {
            MobMixinAddon mob = (MobMixinAddon) this;
            //update bounding box, when the scale factor changes
            if (data.equals(mob.getTracker())) {
                ((Entity) (Object) this).calculateDimensions();
            }
        }
    }

    @Redirect(method = "calculateDimensions", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;getDimensions(Lnet/minecraft/entity/EntityPose;)Lnet/minecraft/entity/EntityDimensions;"))
    private EntityDimensions getDimensions(Entity entity, EntityPose pose) {
        if (((Entity) (Object) this) instanceof MobEntity) {
            MobMixinAddon mob = (MobMixinAddon) this;
            float scaling = mob.getMobScaling();
            float scaleFactor = ((LivingEntity) (Object) this).getScaleFactor();
            EntityDimensions dimensions = entity.getType().getDimensions();
            return dimensions.scaled(scaling).scaled(scaleFactor);
        }
        return entity.getDimensions(pose);
    }

}
