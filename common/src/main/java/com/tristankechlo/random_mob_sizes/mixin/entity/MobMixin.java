package com.tristankechlo.random_mob_sizes.mixin.entity;

import com.tristankechlo.random_mob_sizes.RandomMobSizes;
import com.tristankechlo.random_mob_sizes.config.RandomMobSizesConfig;
import com.tristankechlo.random_mob_sizes.mixin_helper.MobMixinAddon;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import net.minecraft.core.Holder;
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

    private static final EntityDataAccessor<Boolean> SCALE_LOOT$RANDOM_MOB_SIZES = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SCALE_XP$RANDOM_MOB_SIZES = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BOOLEAN);

    @Override
    public boolean shouldScaleLoot$RandomMobSizes() {
        return ((Mob) (Object) this).getEntityData().get(SCALE_LOOT$RANDOM_MOB_SIZES);
    }

    @Override
    public void setShouldScaleLoot$RandomMobSizes(boolean shouldScale) {
        ((Mob) (Object) this).getEntityData().set(SCALE_LOOT$RANDOM_MOB_SIZES, shouldScale);
    }

    @Override
    public boolean shouldScaleXP$RandomMobSizes() {
        return ((Mob) (Object) this).getEntityData().get(SCALE_XP$RANDOM_MOB_SIZES);
    }

    @Override
    public void setShouldScaleXP$RandomMobSizes(boolean shouldScale) {
        ((Mob) (Object) this).getEntityData().set(SCALE_XP$RANDOM_MOB_SIZES, shouldScale);
    }

    @Override
    public void doFinalizeSpawn$RandomMobSizes(ServerLevelAccessor level) {
        if (level.isClientSide()) {
            return;
        }
        EntityType<?> type = ((Mob) (Object) this).getType();
        ScalingSampler sampler = RandomMobSizesConfig.getScalingSampler(type);
        double scaling = 1.0F;
        if (sampler != null) {
            scaling = sampler.sample(level.getRandom(), level.getDifficulty());

            if (sampler.shouldScaleHealth()) {
                double healthScaling = sampler.getHealthScaler().apply(scaling);
                // only sets the new max possible health
                double maxHealth = this.addModifier$RandomMobSizes(Attributes.MAX_HEALTH, healthScaling, true);
                // adjust the actual health as well
                ((LivingEntity) (Object) this).setHealth((float) maxHealth);
            }
            if (sampler.shouldScaleDamage()) {
                double damageScaling = sampler.getDamageScaler().apply(scaling);
                this.addModifier$RandomMobSizes(Attributes.ATTACK_DAMAGE, damageScaling, true);
            }
            if (sampler.shouldScaleSpeed()) {
                double speedScaling = sampler.getSpeedScaler().apply(scaling);
                this.addModifier$RandomMobSizes(Attributes.MOVEMENT_SPEED, speedScaling, false);
            }
        }
        boolean shouldScaleLoot = sampler == null || sampler.shouldScaleLoot();
        this.setShouldScaleLoot$RandomMobSizes(shouldScaleLoot);
        boolean shouldScaleXP = sampler == null || sampler.shouldScaleXP();
        this.setShouldScaleXP$RandomMobSizes(shouldScaleXP);
        this.addModifier$RandomMobSizes(Attributes.SCALE, scaling, false);
    }

    @Inject(at = @At("TAIL"), method = "finalizeSpawn")
    private void finalizeSpawn$RandomMobSizes(ServerLevelAccessor level, DifficultyInstance $$1, MobSpawnType $$2, SpawnGroupData $$3, CallbackInfoReturnable<SpawnGroupData> cir) {
        this.doFinalizeSpawn$RandomMobSizes(level);
    }

    @Override
    public double addModifier$RandomMobSizes(Holder<Attribute> attribute, double scaling, boolean ceil) {
        AttributeInstance instance = ((LivingEntity) (Object) this).getAttribute(attribute);
        if (instance != null) {
            double baseValue = instance.getBaseValue();
            double newValue = (ceil ? Math.ceil(baseValue * scaling) : baseValue * scaling);
            instance.setBaseValue(newValue);
            return newValue;
        }
        return 1.0F;
    }

    @Inject(at = @At("TAIL"), method = "defineSynchedData")
    private void defineSyncedData$RandomMobSizes(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(SCALE_LOOT$RANDOM_MOB_SIZES, true);
        builder.define(SCALE_XP$RANDOM_MOB_SIZES, true);
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
    private void readAdditionalSaveData$RandomMobSizes(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("ScaleLoot")) {
            this.setShouldScaleLoot$RandomMobSizes(tag.getBoolean("ScaleLoot"));
        }
        if (tag.contains("ScaleExperience")) {
            this.setShouldScaleXP$RandomMobSizes(tag.getBoolean("ScaleExperience"));
        }
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
    private void addAdditionalSaveData$RandomMobSizes(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("ScaleLoot", this.shouldScaleLoot$RandomMobSizes());
        tag.putBoolean("ScaleExperience", this.shouldScaleXP$RandomMobSizes());
    }

    @Inject(at = @At("TAIL"), method = "convertTo")
    private <T extends Mob> void convertTo$RandomMobSizes(EntityType<T> type, boolean $$1, CallbackInfoReturnable<T> cir) {
        if (!RandomMobSizesConfig.keepScalingOnConversion() || !RandomMobSizes.isEntityTypeAllowed(type)) {
            return;
        }
        float scaling = ((LivingEntity) (Object) this).getScale();
        boolean scaleLoot = ((MobMixinAddon) this).shouldScaleLoot$RandomMobSizes();
        boolean scaleXP = ((MobMixinAddon) this).shouldScaleXP$RandomMobSizes();
        T entity = cir.getReturnValue();
        this.scaleAttributes$RandomMobSizes((MobMixinAddon) entity, scaling);
        ((MobMixinAddon) entity).setShouldScaleLoot$RandomMobSizes(scaleLoot);
        ((MobMixinAddon) entity).setShouldScaleXP$RandomMobSizes(scaleXP);
        RandomMobSizes.LOGGER.info("Converted '{}' to '{}' with scaling '{}'", this.getClass().getSimpleName(), entity.getClass().getSimpleName(), scaling);
    }

    private void scaleAttributes$RandomMobSizes(MobMixinAddon other, double scaling) {
        double maxHealth = other.addModifier$RandomMobSizes(Attributes.MAX_HEALTH, scaling, true);
        ((LivingEntity) other).setHealth((float) maxHealth);
        other.addModifier$RandomMobSizes(Attributes.MOVEMENT_SPEED, scaling, false);
        other.addModifier$RandomMobSizes(Attributes.ATTACK_DAMAGE, scaling, true);
        other.addModifier$RandomMobSizes(Attributes.SCALE, scaling, false);
    }

}
