package com.tristankechlo.random_mob_sizes.mixin;

import com.mojang.brigadier.arguments.ArgumentType;
import com.tristankechlo.random_mob_sizes.RandomMobSizes;
import com.tristankechlo.random_mob_sizes.commands.SamplerTypesArgumentType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArgumentTypes.class)
public abstract class ArgumentTypesMixin {

    @Inject(at = @At("HEAD"), method = "bootStrap")
    private static void bootstrap$RandomMobSizes(CallbackInfo ci) {
        // register custom argument type
        register(RandomMobSizes.MOD_ID + ":sampler_types", SamplerTypesArgumentType.class, new EmptyArgumentSerializer<>(SamplerTypesArgumentType::get));
    }

    @Shadow
    public static <T extends ArgumentType<?>> void register(String $$0, Class<T> $$1, ArgumentSerializer<T> $$2) {
        throw new AssertionError();
    }

}
