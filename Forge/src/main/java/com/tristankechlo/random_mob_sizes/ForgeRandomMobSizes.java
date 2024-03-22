package com.tristankechlo.random_mob_sizes;

import com.tristankechlo.random_mob_sizes.commands.MobScalingsCommand;
import com.tristankechlo.random_mob_sizes.commands.RandomMobSizesCommand;
import com.tristankechlo.random_mob_sizes.config.ConfigManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(RandomMobSizes.MOD_ID)
public class ForgeRandomMobSizes {

    private static final DeferredRegister<GlobalLootModifierSerializer<?>> GLM = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, RandomMobSizes.MOD_ID);
    private static final RegistryObject<GlobalLootModifierSerializer<?>> SCALE_LOOT = GLM.register("scale_loot", RandomMobSizesLootModifier.Serializer::new);

    public ForgeRandomMobSizes() {
        GLM.register(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.addListener(this::onServerStart);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onServerStart(final ServerStartingEvent event) {
        ConfigManager.loadAndVerifyConfig();
    }

    private void registerCommands(final RegisterCommandsEvent event) {
        RandomMobSizesCommand.register(event.getDispatcher());
        MobScalingsCommand.register(event.getDispatcher());
    }

}
