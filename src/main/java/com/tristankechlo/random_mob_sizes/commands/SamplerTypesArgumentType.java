package com.tristankechlo.random_mob_sizes.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;

public class SamplerTypesArgumentType extends EnumArgumentType<SamplerTypes> {

    private SamplerTypesArgumentType() {
        super(SamplerTypes.CODEC, SamplerTypes::values);
    }

    public static EnumArgumentType<SamplerTypes> get() {
        return new SamplerTypesArgumentType();
    }

    public static SamplerTypes getSamplerType(CommandContext<ServerCommandSource> context, String name) {
        return context.getArgument(name, SamplerTypes.class);
    }

}
