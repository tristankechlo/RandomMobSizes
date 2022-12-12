package com.tristankechlo.random_mob_sizes.mixin;

import com.tristankechlo.random_mob_sizes.config.RandomMobSizesConfig;
import com.tristankechlo.random_mob_sizes.mixin_access.MobMixinAddon;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* Adds another EntityDataAccessor to the Mob class to store the scale factor */
@Mixin(Mob.class)
public abstract class MobMixin implements MobMixinAddon {

    private static final EntityDataAccessor<Float> SCALING = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.FLOAT);

    @Override
    public float getScaleFactor() {
        return ((Mob) (Object) this).getEntityData().get(SCALING);
    }

    @Override
    public void setScaleFactor(Float scale) {
        ((Mob) (Object) this).getEntityData().set(SCALING, scale);
    }

    @Inject(at = @At("TAIL"), method = "defineSynchedData")
    private void defineSynchedData(CallbackInfo ci) {
        EntityType<?> type = ((Mob) (Object) this).getType();
        ScalingSampler sampler = RandomMobSizesConfig.SETTINGS.get(type);
        float scaling = 1.0F;
        if (sampler != null) {
            scaling = sampler.sample();
        }
        ((Mob) (Object) this).getEntityData().define(SCALING, scaling);
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
    private void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("ScaleFactor")) {
            this.setScaleFactor(tag.getFloat("ScaleFactor"));
        }
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
    private void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        tag.putFloat("ScaleFactor", ((Mob) (Object) this).getEntityData().get(SCALING));
    }

}
