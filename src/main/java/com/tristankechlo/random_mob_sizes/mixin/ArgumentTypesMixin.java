package com.tristankechlo.random_mob_sizes.mixin;

import com.mojang.brigadier.arguments.ArgumentType;
import com.tristankechlo.random_mob_sizes.RandomMobSizesMod;
import com.tristankechlo.random_mob_sizes.commands.SamplerTypesArgumentType;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.command.argument.serialize.ConstantArgumentSerializer.of;

@Mixin(ArgumentTypes.class)
public abstract class ArgumentTypesMixin {

    @Inject(at = @At("HEAD"), method = "register(Lnet/minecraft/registry/Registry;)Lnet/minecraft/command/argument/serialize/ArgumentSerializer;")
    private static void registerArgumentTypes(Registry<ArgumentSerializer<?, ?>> registry, CallbackInfoReturnable<ArgumentSerializer<?, ?>> cir) {
        // register custom argument type
        register(registry, RandomMobSizesMod.MOD_ID + ":sampler_types", SamplerTypesArgumentType.class, of(SamplerTypesArgumentType::get));
    }

    @Shadow
    private static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> ArgumentSerializer<A, T> register(Registry<ArgumentSerializer<?, ?>> registry, String id, Class<? extends A> clazz, ArgumentSerializer<A, T> serializer) {
        throw new AssertionError("Mixin failed to shadow method");
    }

}
