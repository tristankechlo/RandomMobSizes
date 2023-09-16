package com.tristankechlo.random_mob_sizes.mixin;

import com.tristankechlo.random_mob_sizes.RandomMobSizes;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* Adds another EntityDataAccessor to the Mob class to store the scale factor */
@Mixin(Mob.class)
public abstract class MobMixin implements MobMixinAddon {

    private static final EntityDataAccessor<Float> SCALING$RANDOM_MOB_SIZES = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.FLOAT);

    @Override
    public float getMobScaling$RandomMobSizes() {
        return ((Mob) (Object) this).getEntityData().get(SCALING$RANDOM_MOB_SIZES);
    }

    @Override
    public void setMobScaling$RandomMobSizes(float scale) {
        ((Mob) (Object) this).getEntityData().set(SCALING$RANDOM_MOB_SIZES, scale);
    }

    @Override
    public EntityDataAccessor<Float> getTracker$RandomMobSizes() {
        return SCALING$RANDOM_MOB_SIZES;
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void constructor$RandomMobSizes(EntityType<?> type, Level level, CallbackInfo ci) {
        if (level.isClientSide) {
            return;
        }
        ScalingSampler sampler = RandomMobSizesConfig.getScalingSampler(type);
        float scaling = 1.0F;
        if (sampler != null) {
            scaling = sampler.sample(level.random);
        }
        this.setMobScaling(scaling);
    }

    @Inject(at = @At("TAIL"), method = "defineSynchedData")
    private void defineSyncedData$RandomMobSizes(CallbackInfo ci) {
        ((Mob) (Object) this).getEntityData().define(SCALING$RANDOM_MOB_SIZES, 1.0F);
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
    private void readAdditionalSaveData$RandomMobSizes(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("ScaleFactor")) {
            this.setMobScaling$RandomMobSizes(tag.getFloat("ScaleFactor"));
        }
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
    private void addAdditionalSaveData$RandomMobSizes(CompoundTag tag, CallbackInfo ci) {
        tag.putFloat("ScaleFactor", this.getMobScaling$RandomMobSizes());
    }

    @Inject(at = @At("TAIL"), method = "convertTo")
    private <T extends Mob> void convertTo$RandomMobSizes(EntityType<T> type, boolean $$1, CallbackInfoReturnable<T> cir) {
        if (!RandomMobSizesConfig.keepScalingOnConversion()) {
            return;
        }
        float scaling = ((MobMixinAddon) this).getMobScaling$RandomMobSizes();
        T entity = cir.getReturnValue();
        ((MobMixinAddon) entity).setMobScaling$RandomMobSizes(scaling);
        RandomMobSizes.LOGGER.info("Converted {} to {} with scaling {}", this.getClass().getSimpleName(), entity.getClass().getSimpleName(), scaling);
    }

}
