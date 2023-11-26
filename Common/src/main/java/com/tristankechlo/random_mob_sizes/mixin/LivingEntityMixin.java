package com.tristankechlo.random_mob_sizes.mixin;

import com.tristankechlo.random_mob_sizes.mixin_access.MobMixinAddon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    /* adjust the amount of dropped loot according to the scaling of the mob */
    @Redirect(method = "dropFromLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;JLjava/util/function/Consumer;)V"))
    private void dropFromLootTable$RandomMobSizes(LootTable instance, LootParams params, long seed, Consumer<ItemStack> spawnAtLocation) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof Mob)) {
            return;
        }
        MobMixinAddon mob = (MobMixinAddon) entity;
        if (!mob.shouldScaleLoot$RandomMobSizes()) { // loot manipulation disabled for this mob
            return;
        }
        float scaling = mob.getMobScaling$RandomMobSizes();
        RandomSource random = params.getLevel().getRandom();
        instance.getRandomItems(params, seed, (stack) -> handleLoot$RandomMobSizes(scaling, random, stack, spawnAtLocation));
    }

    /* adjust the amount of dropped xp points according to the scaling of the mob */
    @Redirect(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"))
    private void dropExperience$RandomMobSizes(ServerLevel level, Vec3 pos, int xp) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof Mob)) {
            return;
        }
        MobMixinAddon mob = (MobMixinAddon) entity;
        if (!mob.shouldScaleLoot$RandomMobSizes()) { // xp manipulation disabled for this mob
            return;
        }
        float scaling = mob.getMobScaling$RandomMobSizes();
        int newExp = Math.round(xp * scaling);
        ExperienceOrb.award(level, pos, newExp);
    }

    private void handleLoot$RandomMobSizes(float scaling, RandomSource random, ItemStack stack, Consumer<ItemStack> spawnAtLocation) {
        if (scaling <= 1.0F && stack.getCount() == 1) {
            // special case where the scaling will be used as a chance to determine if the item should be dropped
            if (random.nextDouble() <= scaling) {
                spawnAtLocation.accept(stack);
            }
            return;
        }
        int count = Math.round(stack.getCount() * scaling);
        if (count == 0 || stack.getCount() == 0) {
            return;
        }
        int maxStackSize = stack.getMaxStackSize();

        // calculated stack size is still smaller than maxStackSize, so spawn the stack normally
        if (count <= maxStackSize) {
            stack.setCount(count);
            spawnAtLocation.accept(stack);
            return;
        }

        // spawn multiple itemstacks with maxStackSize or the remaining count
        Item item = stack.getItem();
        do {
            // either use maxStackSize, or the remaining count when smaller than maxStackSize
            int tempCount = Math.min(count, maxStackSize);
            ItemStack temp = new ItemStack(item, tempCount);
            spawnAtLocation.accept(temp);
            count -= maxStackSize;
        }
        while (count > maxStackSize && count > 0);
    }

}
