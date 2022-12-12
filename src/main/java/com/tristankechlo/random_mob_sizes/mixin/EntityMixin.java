package com.tristankechlo.random_mob_sizes.mixin;

import com.tristankechlo.random_mob_sizes.mixin_access.MobMixinAddon;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* reapply all EntityDimensions scaling factors, after vanilla changes them */
@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    private EntityDimensions dimensions;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void constructor(EntityType<?> type, Level level, CallbackInfo ci) {
        if (((Entity) (Object) this) instanceof Mob) {
            float scaleFactor = ((MobMixinAddon) this).getScaleFactor();
            this.dimensions = this.dimensions.scale(scaleFactor);
            this.eyeHeight = this.getEyeHeight(Pose.STANDING, dimensions);
        }
    }

    @Inject(method = "refreshDimensions",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;reapplyPosition()V", shift = At.Shift.BEFORE))
    private void refreshDimensions(CallbackInfo ci) {
        if (((Entity) (Object) this) instanceof Mob) {
            float scaleFactor = ((MobMixinAddon) this).getScaleFactor();
            this.dimensions = this.dimensions.scale(scaleFactor);
            Pose pose = this.getPose();
            this.eyeHeight = this.getEyeHeight(pose, dimensions);
        }
        //this.dimensions = this.dimensions.scale(2.0F);
    }

    @Inject(method = "fixupDimensions",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;getEyeHeight(Lnet/minecraft/world/entity/Pose;Lnet/minecraft/world/entity/EntityDimensions;)F",
                    shift = At.Shift.BEFORE))
    private void fixupDimensions(CallbackInfo ci) {
        if (((Entity) (Object) this) instanceof Mob) {
            float scaling = ((MobMixinAddon) this).getScaleFactor();
            this.dimensions = this.dimensions.scale(scaling);
        }
    }

    @Shadow
    public abstract Pose getPose();

    @Shadow
    private float eyeHeight;

    @Shadow
    protected abstract float getEyeHeight(Pose pose, EntityDimensions dimensions);

}
