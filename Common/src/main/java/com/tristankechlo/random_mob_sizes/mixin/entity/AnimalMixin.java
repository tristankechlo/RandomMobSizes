package com.tristankechlo.random_mob_sizes.mixin.entity;

import com.tristankechlo.random_mob_sizes.mixin_helper.MobMixinAddon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalMixin {

    @Inject(method = "finalizeSpawnChildFromBreeding", at = @At(value = "HEAD"))
    private void finalizeSpawnChildFromBreeding$RandomMobSizes(ServerLevel level, Animal parent, AgeableMob child, CallbackInfo ci) {
        // also apply scaling to child mobs from breeding two parents
        ((MobMixinAddon) child).doFinalizeSpawn$RandomMobSizes(level);
    }

}
