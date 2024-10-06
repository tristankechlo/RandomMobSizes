package com.tristankechlo.random_mob_sizes;

import com.google.auto.service.AutoService;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

@AutoService(IPlatformHelper.class)
public class NeoforgePlatformHelper implements IPlatformHelper {

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }

}
