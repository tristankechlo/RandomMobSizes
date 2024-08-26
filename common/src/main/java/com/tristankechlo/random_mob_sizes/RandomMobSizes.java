package com.tristankechlo.random_mob_sizes;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

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
        if(allowedMisc == null) {
            allowedMisc = Arrays.asList(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER);
        }
        //disallow all entities from the group "misc", except for golems, villagers
        return type.getCategory() != MobCategory.MISC || allowedMisc.contains(type);
    }

}
