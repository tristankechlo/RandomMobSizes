package com.tristankechlo.random_mob_sizes;

import com.tristankechlo.random_mob_sizes.commands.MobScalingsCommand;
import com.tristankechlo.random_mob_sizes.commands.RandomMobSizesCommand;
import com.tristankechlo.random_mob_sizes.config.ConfigManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(RandomMobSizes.MOD_ID)
public class ForgeRandomMobSizes {

    public ForgeRandomMobSizes() {
        ServerStartingEvent.BUS.addListener(this::onServerStart);
        RegisterCommandsEvent.BUS.addListener(this::registerCommands);
    }

    private void onServerStart(final ServerStartingEvent event) {
        ConfigManager.loadAndVerifyConfig();
    }

    private void registerCommands(final RegisterCommandsEvent event) {
        RandomMobSizesCommand.register(event.getDispatcher());
        MobScalingsCommand.register(event.getDispatcher(), event.getBuildContext());
    }

}
