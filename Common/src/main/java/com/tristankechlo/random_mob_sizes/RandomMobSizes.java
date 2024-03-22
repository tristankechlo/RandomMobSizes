package com.tristankechlo.random_mob_sizes;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class RandomMobSizes {

    public static final String MOD_NAME = "RandomMobSizes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final String MOD_ID = "random_mob_sizes";
    public static final String GITHUB_URL = "https://github.com/tristankechlo/RandomMobSizes";
    public static final String GITHUB_ISSUE_URL = GITHUB_URL + "/issues";
    public static final String GITHUB_WIKI_URL = GITHUB_URL + "/wiki";
    public static final String DISCORD_URL = "https://discord.gg/bhUaWhq";
    public static final String CURSEFORGE_URL = "https://curseforge.com/minecraft/mc-mods/random-mob-sizes";
    public static final String MODRINTH_URL = "https://modrinth.com/mod/random-mob-sizes";

    private static List<EntityType<?>> allowedMisc = null;

    public static boolean isEntityTypeAllowed(EntityType<?> type) {
        if (allowedMisc == null) {
            allowedMisc = Arrays.asList(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER);
        }
        //disallow all entities from the group "misc", except for golems, villagers
        return type.getCategory() != MobCategory.MISC || allowedMisc.contains(type);
    }

    public static void handleLoot(float scaling, Random random, ItemStack stack, Consumer<ItemStack> stackSplitter) {
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

}
