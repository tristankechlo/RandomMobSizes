package com.tristankechlo.random_mob_sizes.mixin.entity;

import com.tristankechlo.random_mob_sizes.mixin_helper.MobMixinAddon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CaveSpider.class)
public abstract class CaveSpiderMixin {

    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    private void finalizeSpawn$RandomMobSizes(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, SpawnGroupData groupData, CompoundTag nbt, CallbackInfoReturnable<SpawnGroupData> cir) {
        // finalizeSpawn in CaveSpider.class does not call super method where scaling would be applied
        // call custom doFinalizeSpawn instead
        ((MobMixinAddon) this).doFinalizeSpawn$RandomMobSizes(level, difficulty, spawnType, groupData, nbt);
    }

}