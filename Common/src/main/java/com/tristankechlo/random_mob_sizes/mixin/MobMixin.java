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
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* Adds another EntityDataAccessor to the Mob class to store the scale factor */
@Mixin(Mob.class)
public abstract class MobMixin implements MobMixinAddon {

    private static final EntityDataAccessor<Float> SCALING = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.FLOAT);

    @Override
    public float getMobScaling() {
        return ((Mob) (Object) this).getEntityData().get(SCALING);
    }

    @Override
    public void setMobScaling(float scale) {
        ((Mob) (Object) this).getEntityData().set(SCALING, scale);
    }

    @Override
    public EntityDataAccessor<Float> getTracker() {
        return SCALING;
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void constructor(EntityType<?> type, Level world, CallbackInfo ci) {
        if (world.isClientSide) {
            return;
        }
        ScalingSampler sampler = RandomMobSizesConfig.getScalingSampler(type);
        float scaling = 1.0F;
        if (sampler != null) {
            scaling = sampler.sample();
        }
        this.setMobScaling(scaling);
    }

    @Inject(at = @At("TAIL"), method = "defineSynchedData")
    private void defineSyncedData(CallbackInfo ci) {
        ((Mob) (Object) this).getEntityData().define(SCALING, 1.0F);
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
    private void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("ScaleFactor")) {
            this.setMobScaling(tag.getFloat("ScaleFactor"));
        }
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
    private void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        tag.putFloat("ScaleFactor", this.getMobScaling());
    }

}