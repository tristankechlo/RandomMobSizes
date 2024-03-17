package com.tristankechlo.random_mob_sizes;

import com.tristankechlo.random_mob_sizes.commands.MobScalingsCommand;
import com.tristankechlo.random_mob_sizes.commands.RandomMobSizesCommand;
import com.tristankechlo.random_mob_sizes.config.ConfigManager;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(RandomMobSizes.MOD_ID)
public class NeoforgeRandomMobSizes {

    public NeoforgeRandomMobSizes() {
        NeoForge.EVENT_BUS.addListener(this::onServerStart);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
    }

    private void onServerStart(final ServerStartingEvent event) {
        ConfigManager.loadAndVerifyConfig();
    }

    private void registerCommands(final RegisterCommandsEvent event) {
        RandomMobSizesCommand.register(event.getDispatcher());
        MobScalingsCommand.register(event.getDispatcher(), event.getBuildContext());
    }
}
