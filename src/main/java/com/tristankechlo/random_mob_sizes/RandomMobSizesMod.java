package com.tristankechlo.random_mob_sizes;

import com.tristankechlo.random_mob_sizes.commands.MobScalingsCommand;
import com.tristankechlo.random_mob_sizes.commands.RandomMobSizesCommand;
import com.tristankechlo.random_mob_sizes.config.ConfigManager;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(RandomMobSizesMod.MOD_ID)
public class RandomMobSizesMod {

    public static final String MOD_NAME = "RandomMobSizes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final String MOD_ID = "random_mob_sizes";
    public static final String GITHUB_URL = "https://github.com/tristankechlo/RandomMobSizes";
    public static final String GITHUB_ISSUE_URL = GITHUB_URL + "/issues";
    public static final String GITHUB_WIKI_URL = GITHUB_URL + "/wiki";
    public static final String DISCORD_URL = "https://discord.gg/bhUaWhq";
    public static final String CURSEFORGE_URL = "https://curseforge.com/minecraft/mc-mods/random-mob-sizes";
    public static final String MODRINTH_URL = "https://modrinth.com/mod/random-mob-sizes";
    public static final EntityDataAccessor<Float> SCALING = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.FLOAT);

    public RandomMobSizesMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ConfigManager.loadAndVerifyConfig();
    }

    private void registerCommands(final RegisterCommandsEvent event) {
        RandomMobSizesCommand.register(event.getDispatcher());
        MobScalingsCommand.register(event.getDispatcher(), event.getBuildContext());
    }

}
