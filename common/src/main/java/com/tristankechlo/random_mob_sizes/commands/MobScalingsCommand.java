package com.tristankechlo.random_mob_sizes.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.tristankechlo.random_mob_sizes.RandomMobSizes;
import com.tristankechlo.random_mob_sizes.config.ConfigManager;
import com.tristankechlo.random_mob_sizes.config.RandomMobSizesConfig;
import com.tristankechlo.random_mob_sizes.config.ScalingOverrides;
import com.tristankechlo.random_mob_sizes.sampler.SamplerTypes;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import static com.tristankechlo.random_mob_sizes.sampler.ScalingSampler.MAXIMUM_SCALING;
import static com.tristankechlo.random_mob_sizes.sampler.ScalingSampler.MINIMUM_SCALING;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class MobScalingsCommand {

    private static final String COMMAND_NAME = "mobscalings";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        LiteralArgumentBuilder<CommandSourceStack> command = literal(COMMAND_NAME).requires((source) -> source.hasPermission(3))
                .then(literal("set")
                        .then(argument("entity_type", ResourceArgument.resource(context, Registries.ENTITY_TYPE))
                                .then(argument("scaling_type", SamplerTypesArgumentType.get())
                                        .then(argument("data", CompoundTagArgument.compoundTag())
                                                .executes(MobScalingsCommand::setEntityScale)))
                                .then(argument("scale", DoubleArgumentType.doubleArg(MINIMUM_SCALING, MAXIMUM_SCALING))
                                        .executes(MobScalingsCommand::setEntityScaleStatic))))
                .then(literal("remove")
                        .then(argument("entity_type", ResourceArgument.resource(context, Registries.ENTITY_TYPE))
                                .executes(MobScalingsCommand::removeEntityScale)))
                .then(literal("show")
                        .then(argument("entity_type", ResourceArgument.resource(context, Registries.ENTITY_TYPE))
                                .executes(MobScalingsCommand::showEntityScale))
                        .executes(MobScalingsCommand::showAllEntityScales));
        dispatcher.register(command);
        RandomMobSizes.LOGGER.info("Command '/{}' registered", COMMAND_NAME);
    }

    private static int setEntityScale(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            //read values from command
            final EntityType<?> entityType = ResourceArgument.getEntityType(context, "entity_type").value();
            final SamplerTypes scalingType = context.getArgument("scaling_type", SamplerTypes.class);
            final CompoundTag data = CompoundTagArgument.getCompoundTag(context, "data");
            return setEntityScale(source, entityType, scalingType, data);
        } catch (Exception e) {
            return errorHandling(source, e, "An error occurred while setting the scale for the entity type!");
        }
    }

    private static int setEntityScaleStatic(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            //read values from command
            final EntityType<?> entityType = ResourceArgument.getEntityType(context, "entity_type").value();
            final double scale = DoubleArgumentType.getDouble(context, "scale");
            final CompoundTag nbt = new CompoundTag();
            nbt.putDouble("scaling", scale);
            return setEntityScale(source, entityType, SamplerTypes.STATIC, nbt);
        } catch (Exception e) {
            return errorHandling(source, e, "An error occurred while setting the static scale for the entity type!");
        }
    }

    private static int setEntityScale(CommandSourceStack source, EntityType<?> entityType, SamplerTypes scalingType, CompoundTag nbt) {
        //updating and saving config
        RandomMobSizes.LOGGER.info("Setting scale for entity type '{}' to '{}' with data '{}'", entityType, scalingType, nbt);
        ResourceLocation loc = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        ScalingSampler sampler = scalingType.fromNBT(nbt, loc.toString());
        boolean success = RandomMobSizesConfig.SCALING_OVERRIDES.setScalingSampler(entityType, sampler);
        if (success) {
            ConfigManager.saveConfig();
            ResponseHelper.sendSuccessScalingTypeSet(source, entityType, scalingType, nbt);
        } else {
            ResponseHelper.sendErrorScalingTypeSet(source, entityType);
        }
        return 1;
    }

    private static int removeEntityScale(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            //read values from command
            final EntityType<?> entityType = ResourceArgument.getEntityType(context, "entity_type").value();

            //updating and saving config
            RandomMobSizes.LOGGER.info("Removing scale for entity type '{}'", entityType);
            RandomMobSizesConfig.SCALING_OVERRIDES.removeScalingSampler(entityType);
            ConfigManager.saveConfig();
            ResponseHelper.sendSuccessScalingTypeRemoved(source, entityType);
            return 1;
        } catch (Exception e) {
            return errorHandling(source, e, "An error occurred while removing the scale for the entity type!");
        }
    }

    private static int showEntityScale(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            //read values from command
            final EntityType<?> entityType = ResourceArgument.getEntityType(context, "entity_type").value();
            final ScalingSampler scalingSampler = RandomMobSizesConfig.getScalingSampler(entityType);
            if (scalingSampler == null) {
                ResponseHelper.sendErrorScalingTypeNotSet(source, entityType);
                return 0;
            }
            ResponseHelper.sendSuccessScalingType(source, entityType, scalingSampler);
            return 1;
        } catch (Exception e) {
            return errorHandling(source, e, "An error occurred while showing the scale for the entity type!");
        }
    }

    private static int showAllEntityScales(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ScalingSampler defaultSampler = RandomMobSizesConfig.getDefaultSampler();
        ResponseHelper.sendSuccessScalingType(source, "DefaultScaling", defaultSampler);
        ScalingOverrides.getIterator().forEachRemaining(entry -> ResponseHelper.sendSuccessScalingType(source, entry.getKey(), entry.getValue()));
        return 1;
    }

    private static int errorHandling(CommandSourceStack source, Exception e, String text) {
        MutableComponent message = Component.literal(text).withStyle(ChatFormatting.RED);
        ResponseHelper.sendMessage(source, message, false);
        message = Component.literal(e.getMessage()).withStyle(ChatFormatting.RED);
        ResponseHelper.sendMessage(source, message, false);
        RandomMobSizes.LOGGER.error(text);
        RandomMobSizes.LOGGER.error(e.getMessage());
        return 0;
    }

}