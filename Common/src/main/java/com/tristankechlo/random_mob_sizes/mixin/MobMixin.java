package com.tristankechlo.random_mob_sizes.mixin;

import com.tristankechlo.random_mob_sizes.RandomMobSizes;
import com.tristankechlo.random_mob_sizes.config.RandomMobSizesConfig;
import com.tristankechlo.random_mob_sizes.mixin_access.MobMixinAddon;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ServerLevelAccessor;
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

    @Inject(at = @At("TAIL"), method = "finalizeSpawn")
    private void finalizeSpawn$RandomMobSizes(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, SpawnGroupData data, CompoundTag nbt, CallbackInfoReturnable<SpawnGroupData> cir) {
        if (level.isClientSide()) {
            return;
        }
        EntityType<?> type = ((Mob) (Object) this).getType();
        ScalingSampler sampler = RandomMobSizesConfig.getScalingSampler(type);
        float scaling = 1.0F;
        if (sampler != null) {
            scaling = sampler.sample(level.getRandom(), level.getDifficulty());

            if (sampler.shouldScaleHealth()) {
                float healthScaling = sampler.getHealthScaler().apply(scaling);
                this.addModifier$RandomMobSizes(Attributes.MAX_HEALTH, healthScaling, true);
            }
            if (sampler.shouldScaleDamage()) {
                float damageScaling = sampler.getDamageScaler().apply(scaling);
                this.addModifier$RandomMobSizes(Attributes.ATTACK_DAMAGE, damageScaling, true);
            }
            if (sampler.shouldScaleSpeed()) {
                float speedScaling = sampler.getSpeedScaler().apply(scaling);
                this.addModifier$RandomMobSizes(Attributes.MOVEMENT_SPEED, speedScaling, false);
            }
        }
        this.setMobScaling$RandomMobSizes(scaling);
    }

    private void addModifier$RandomMobSizes(Attribute attribute, float scaling, boolean ceil) {
        AttributeInstance instance = ((LivingEntity) (Object) this).getAttribute(attribute);
        if (instance != null) {
            double baseValue = instance.getBaseValue();
            float newValue = (float) (ceil ? Math.ceil(baseValue * scaling) : baseValue * scaling);
            instance.setBaseValue(newValue);
            //RandomMobSizes.LOGGER.info("Scaled '{}' of '{}' from '{}' to '{}'", attribute.getDescriptionId(), this.getClass().getSimpleName(), baseValue, instance.getBaseValue());
        }
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
        RandomMobSizes.LOGGER.info("Converted '{}' to '{}' with scaling '{}'", this.getClass().getSimpleName(), entity.getClass().getSimpleName(), scaling);
    }

}
