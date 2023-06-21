package com.tristankechlo.random_mob_sizes;

import com.tristankechlo.random_mob_sizes.commands.MobScalingsCommand;
import com.tristankechlo.random_mob_sizes.commands.RandomMobSizesCommand;
import com.tristankechlo.random_mob_sizes.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class FabricRandomMobSizes implements ModInitializer {

    @Override
    public void onInitialize() {
        // setup configs
        ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
            ConfigManager.loadAndVerifyConfig();
        });

        //register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            RandomMobSizesCommand.register(dispatcher);
            MobScalingsCommand.register(dispatcher);
        });
    }

}
