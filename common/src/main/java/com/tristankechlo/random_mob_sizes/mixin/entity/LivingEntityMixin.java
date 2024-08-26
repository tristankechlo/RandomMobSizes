package com.tristankechlo.random_mob_sizes.mixin.entity;

import com.tristankechlo.random_mob_sizes.mixin_helper.MobMixinAddon;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    /* adjust the amount of dropped loot according to the scaling of the mob */
    @Redirect(method = "dropFromLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;JLjava/util/function/Consumer;)V"))
    private void dropFromLootTable$RandomMobSizes(LootTable instance, LootParams params, long seed, Consumer<ItemStack> spawnAtLocation) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // not supported entity, drop default loot
        if (!(entity instanceof MobMixinAddon) || !RandomMobSizes.isEntityTypeAllowed(entity.getType())) {
            instance.getRandomItems(context, seed, spawnAtLocation);
            return;
        }
        MobMixinAddon mob = (MobMixinAddon) entity;

        // loot manipulation disabled for this mob, drop default loot
        if (!mob.shouldScaleLoot$RandomMobSizes()) {
            instance.getRandomItems(context, seed, spawnAtLocation);
            return;
        }
        float scaling = mob.getMobScaling$RandomMobSizes();
        RandomSource random = params.getLevel().getRandom();
        Consumer<ItemStack> stackSplitter = LootTable.createStackSplitter(params.getLevel(), spawnAtLocation);
        instance.getRandomItems(params, seed, (stack) -> handleLoot$RandomMobSizes(scaling, random, stack, stackSplitter));
    }

    private void handleLoot$RandomMobSizes(float scaling, RandomSource random, ItemStack stack, Consumer<ItemStack> stackSplitter) {
        if (scaling <= 1.0F && stack.getCount() == 1) {
            // special case where the scaling will be used as a chance to determine if the item should be dropped
            if (random.nextDouble() <= scaling) {
                stackSplitter.accept(stack);
            }
            return;
        }
        int count = Math.round(stack.getCount() * scaling);
        if (count == 0 || stack.getCount() == 0) {
            return;
        }
        stack.setCount(count);
        // let minecraft handle the splitting of the stacks to their max stack size
        stackSplitter.accept(stack);
    }

    /* adjust the amount of dropped xp points according to the scaling of the mob */
    @ModifyArg(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"), index = 2)
    private int calculateExperienceOnDeath$RandomMobSizes(int xp) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // not supported entity, drop default xp
        if (!(entity instanceof MobMixinAddon) || !RandomMobSizes.isEntityTypeAllowed(entity.getType())) {
            return xp;
        }
        MobMixinAddon mob = (MobMixinAddon) entity;

        // xp manipulation disabled for this mob, drop default xp
        if (!mob.shouldScaleXP$RandomMobSizes()) {
            return xp;
        }
        float scaling = mob.getMobScaling$RandomMobSizes();
        return Math.round(xp * scaling);
    }

}
