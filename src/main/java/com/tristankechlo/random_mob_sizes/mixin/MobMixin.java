package com.tristankechlo.random_mob_sizes.mixin;

import com.tristankechlo.random_mob_sizes.config.RandomMobSizesConfig;
import com.tristankechlo.random_mob_sizes.mixin_access.MobMixinAddon;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* Adds another EntityDataAccessor to the Mob class to store the scale factor */
@Mixin(MobEntity.class)
public abstract class MobMixin implements MobMixinAddon {

    private static final TrackedData<Float> SCALING = DataTracker.registerData(MobEntity.class, TrackedDataHandlerRegistry.FLOAT);

    @Override
    public float getScaleFactor() {
        return ((MobEntity) (Object) this).getDataTracker().get(SCALING);
    }

    @Override
    public void setScaleFactor(Float scale) {
        ((MobEntity) (Object) this).getDataTracker().set(SCALING, scale);
    }

    @Override
    public TrackedData<Float> getTracker() {
        return SCALING;
    }

    @Inject(at = @At("RETURN"), method = "initialize")
    private void onFinalizeSpawn(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir) {
        EntityType<?> type = ((MobEntity) (Object) this).getType();
        ScalingSampler sampler = RandomMobSizesConfig.SETTINGS.get(type);
        float scaling = 1.0F;
        if (sampler != null) {
            scaling = sampler.sample();
        }
        this.setScaleFactor(scaling);
    }

    @Inject(at = @At("TAIL"), method = "initDataTracker")
    private void defineSynchedData(CallbackInfo ci) {
        ((MobEntity) (Object) this).getDataTracker().startTracking(SCALING, 1.0F);
    }

    @Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
    private void readAdditionalSaveData(NbtCompound tag, CallbackInfo ci) {
        if (tag.contains("ScaleFactor")) {
            this.setScaleFactor(tag.getFloat("ScaleFactor"));
        }
    }

    @Inject(at = @At("TAIL"), method = "writeCustomDataToNbt")
    private void addAdditionalSaveData(NbtCompound tag, CallbackInfo ci) {
        tag.putFloat("ScaleFactor", this.getScaleFactor());
    }

}
