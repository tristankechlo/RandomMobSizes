package com.tristankechlo.random_mob_sizes;

import com.tristankechlo.random_mob_sizes.commands.MobScalingsCommand;
import com.tristankechlo.random_mob_sizes.commands.RandomMobSizesCommand;
import com.tristankechlo.random_mob_sizes.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomMobSizesMod implements ModInitializer {

    public static final String MOD_NAME = "RandomMobSizes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final String MOD_ID = "random_mob_sizes";
    public static final String GITHUB_URL = "https://github.com/tristankechlo/RandomMobSizes";
    public static final String GITHUB_ISSUE_URL = GITHUB_URL + "/issues";
    public static final String GITHUB_WIKI_URL = GITHUB_URL + "/wiki";
    public static final String DISCORD_URL = "https://discord.gg/bhUaWhq";
    public static final String CURSEFORGE_URL = "https://curseforge.com/minecraft/mc-mods/random-mob-sizes";
    public static final String MODRINTH_URL = "https://modrinth.com/mod/random-mob-sizes";

    @Override
    public void onInitialize() {
        // setup configs
        ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
            ConfigManager.loadAndVerifyConfig();
        });

        //register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            RandomMobSizesCommand.register(dispatcher);
            MobScalingsCommand.register(dispatcher, registryAccess);
        });
    }

}
