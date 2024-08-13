package com.tristankechlo.random_mob_sizes.mixin.entity;

import com.tristankechlo.random_mob_sizes.mixin_helper.MobMixinAddon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Animal.class)
public abstract class AnimalMixin {

    @Inject(method = "spawnChildFromBreeding",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AgeableMob;moveTo(DDDFF)V", shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void spawnChildFromBreeding$RandomMobSizes(ServerLevel level, Animal $$1, CallbackInfo ci, AgeableMob $$2) {
        // also apply scaling to child mobs from breeding two parents
        ((MobMixinAddon) $$2).doFinalizeSpawn$RandomMobSizes(level);
    }

}
