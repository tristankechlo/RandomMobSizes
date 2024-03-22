package com.tristankechlo.random_mob_sizes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tristankechlo.random_mob_sizes.mixin_helper.MobMixinAddon;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import java.util.function.Consumer;

public class RandomMobSizesLootModifier extends LootModifier {

    public static final Codec<? extends LootModifier> CODEC = RecordCodecBuilder.create(inst ->
            LootModifier.codecStart(inst).apply(inst, RandomMobSizesLootModifier::new));

    protected RandomMobSizesLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!context.hasParam(LootContextParams.THIS_ENTITY)) {
            return generatedLoot;
        }
        Entity entity = context.getParam(LootContextParams.THIS_ENTITY);
        if (entity instanceof MobMixinAddon mob) {
            if (!mob.shouldScaleLoot$RandomMobSizes()) {
                return generatedLoot;
            }
            float scaling = mob.getMobScaling$RandomMobSizes();
            ObjectArrayList<ItemStack> newLoot = new ObjectArrayList<>();
            Consumer<ItemStack> stackSplitter = LootTable.createStackSplitter(context, newLoot::add);
            for (ItemStack stack : generatedLoot) {
                RandomMobSizes.handleLoot(scaling, context.getRandom(), stack, stackSplitter);
            }
            return newLoot;
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

}
