package com.tristankechlo.random_mob_sizes;

import com.google.auto.service.AutoService;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

@AutoService(IPlatformHelper.class)
public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

}
