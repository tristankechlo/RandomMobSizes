package com.tristankechlo.random_mob_sizes.mixin;

import com.tristankechlo.random_mob_sizes.RandomMobSizesMod;
import com.tristankechlo.random_mob_sizes.config.RandomMobSizesConfig;
import com.tristankechlo.random_mob_sizes.mixin_access.MobMixinAddon;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* Adds another EntityDataAccessor to the Mob class to store the scale factor */
@Mixin(Mob.class)
public abstract class MobMixin implements MobMixinAddon {

    @Override
    public float getScaleFactor() {
        return ((Mob) (Object) this).getEntityData().get(RandomMobSizesMod.SCALING);
    }

    @Override
    public void setScaleFactor(Float scale) {
        ((Mob) (Object) this).getEntityData().set(RandomMobSizesMod.SCALING, scale);
    }

    @Inject(at = @At("RETURN"), method = "finalizeSpawn")
    private void onFinalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType mobSpawnType, SpawnGroupData data, CompoundTag nbt, CallbackInfoReturnable<SpawnGroupData> cir) {
        EntityType<?> type = ((Mob) (Object) this).getType();
        ScalingSampler sampler = RandomMobSizesConfig.SETTINGS.get(type);
        float scaling = 1.0F;
        if (sampler != null) {
            scaling = sampler.sample();
        }
        this.setScaleFactor(scaling);
    }

    @Inject(at = @At("TAIL"), method = "defineSynchedData")
    private void defineSyncedData(CallbackInfo ci) {
        ((Mob) (Object) this).getEntityData().define(RandomMobSizesMod.SCALING, 1.0F);
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
    private void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("ScaleFactor")) {
            this.setScaleFactor(tag.getFloat("ScaleFactor"));
        }
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
    private void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        tag.putFloat("ScaleFactor", this.getScaleFactor());
    }

}
