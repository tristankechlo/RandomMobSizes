package com.tristankechlo.random_mob_sizes;

import com.google.gson.JsonObject;
import com.tristankechlo.random_mob_sizes.mixin_helper.MobMixinAddon;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import java.util.List;
import java.util.function.Consumer;

public class RandomMobSizesLootModifier extends LootModifier {

    protected RandomMobSizesLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
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
            Consumer<ItemStack> stackSplitter = LootTable.createStackSplitter(newLoot::add);
            for (ItemStack stack : generatedLoot) {
                RandomMobSizes.handleLoot(scaling, context.getRandom(), stack, stackSplitter);
            }
            return newLoot;
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<RandomMobSizesLootModifier> {

        @Override
        public RandomMobSizesLootModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] ailootcondition) {
            return new RandomMobSizesLootModifier(ailootcondition);
        }

        @Override
        public JsonObject write(RandomMobSizesLootModifier instance) {
            return makeConditions(instance.conditions);
        }

    }

}