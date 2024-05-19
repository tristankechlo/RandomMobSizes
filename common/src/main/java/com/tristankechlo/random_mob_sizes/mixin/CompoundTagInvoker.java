package com.tristankechlo.random_mob_sizes.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(CompoundTag.class)
public interface CompoundTagInvoker {

    @Invoker("entries")
    public Map<String, Tag> getEntries();

}
