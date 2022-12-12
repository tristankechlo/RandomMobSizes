package com.tristankechlo.random_mob_sizes;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(RandomMobSizesMod.MOD_ID)
public class RandomMobSizesMod {

    public static final String MOD_ID = "random_mob_sizes";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RandomMobSizesMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
    }

}
