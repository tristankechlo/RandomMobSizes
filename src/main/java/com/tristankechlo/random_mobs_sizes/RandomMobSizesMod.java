package com.tristankechlo.random_mobs_sizes;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(RandomMobSizesMod.MODID)
public class RandomMobSizesMod {

    public static final String MODID = "random_mob_sizes";
    private static final Logger LOGGER = LogUtils.getLogger();

    public RandomMobSizesMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
    }

}
