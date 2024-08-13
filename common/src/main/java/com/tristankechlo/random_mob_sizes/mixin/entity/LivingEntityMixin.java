package com.tristankechlo.random_mob_sizes.mixin.entity;

import com.tristankechlo.random_mob_sizes.RandomMobSizes;
import com.tristankechlo.random_mob_sizes.mixin_helper.MobMixinAddon;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;
import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    /**
     * adjust the amount of dropped loot according to the scaling of the mob<br/>
     * <br/>
     * this mixin will fail to apply for forge, since they modify how the loot is loaded<br/>
     * special handling for forge is done in RandomMobSizesLootModifier
     */
    @Redirect(method = "dropFromLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V"))
    private void dropFromLootTable$RandomMobSizes(LootTable instance, LootContext context, Consumer<ItemStack> spawnAtLocation) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof Mob)) { // not supported entity, drop default loot
            instance.getRandomItems(context, spawnAtLocation);
            return;
        }
        MobMixinAddon mob = (MobMixinAddon) entity;
        if (!mob.shouldScaleLoot$RandomMobSizes()) { // loot manipulation disabled for this mob, drop default loot
            instance.getRandomItems(context, spawnAtLocation);
            return;
        }
        float scaling = mob.getMobScaling$RandomMobSizes();
        Random random = context.getRandom();
        Consumer<ItemStack> stackSplitter = LootTable.createStackSplitter(spawnAtLocation);
        instance.getRandomItems(context, (stack) -> RandomMobSizes.handleLoot(scaling, random, stack, stackSplitter));
    }

    /* adjust the amount of dropped xp points according to the scaling of the mob */
    @ModifyArg(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"), index = 2)
    private int calculateExperienceOnDeath$RandomMobSizes(int xp) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof Mob)) {
            return xp;
        }
        MobMixinAddon mob = (MobMixinAddon) entity;
        if (!mob.shouldScaleLoot$RandomMobSizes()) { // xp manipulation disabled for this mob
            return xp;
        }
        float scaling = mob.getMobScaling$RandomMobSizes();
        return Math.round(xp * scaling);
    }

}
