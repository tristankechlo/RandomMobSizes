package com.tristankechlo.random_mob_sizes.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(CompoundTag.class)
public interface CompoundTagAccessor {

    @Accessor("tags")
    public Map<String, Tag> getEntries();

}
