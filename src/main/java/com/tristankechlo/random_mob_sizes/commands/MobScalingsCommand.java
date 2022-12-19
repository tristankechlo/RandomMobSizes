package com.tristankechlo.random_mob_sizes.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.tristankechlo.random_mob_sizes.RandomMobSizesMod;
import com.tristankechlo.random_mob_sizes.config.ConfigManager;
import com.tristankechlo.random_mob_sizes.config.RandomMobSizesConfig;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.StaticScalingSampler;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.server.command.EnumArgument;

import static com.tristankechlo.random_mob_sizes.sampler.ScalingSampler.MAXIMUM_SCALING;
import static com.tristankechlo.random_mob_sizes.sampler.ScalingSampler.MINIMUM_SCALING;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class MobScalingsCommand {

    private static final String COMMAND_NAME = "mobScalings";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        LiteralArgumentBuilder<CommandSourceStack> command = literal(COMMAND_NAME).requires((source) -> source.hasPermission(3))
                .then(literal("set")
                        .then(argument("entity_type", ResourceArgument.resource(context, Registries.ENTITY_TYPE))
                                .then(argument("scaling_type", EnumArgument.enumArgument(SamplerTypes.class))
                                        .then(argument("min_scaling", FloatArgumentType.floatArg(MINIMUM_SCALING, MAXIMUM_SCALING))
                                                .then(argument("max_scaling", FloatArgumentType.floatArg(MINIMUM_SCALING, MAXIMUM_SCALING))
                                                        .executes(MobScalingsCommand::setEntityScale))))
                                .then(argument("scale", FloatArgumentType.floatArg(MINIMUM_SCALING, MAXIMUM_SCALING))
                                        .executes(MobScalingsCommand::setEntityScaleStatic))))
                .then(literal("remove")
                        .then(argument("entity_type", ResourceArgument.resource(context, Registries.ENTITY_TYPE))
                                .executes(MobScalingsCommand::removeEntityScale)))
                .then(literal("show")
                        .then(argument("entity_type", ResourceArgument.resource(context, Registries.ENTITY_TYPE))
                                .executes(MobScalingsCommand::showEntityScale))
                        .executes(MobScalingsCommand::showAllEntityScales));
        dispatcher.register(command);
        RandomMobSizesMod.LOGGER.info("Command '/{}' registered", COMMAND_NAME);
    }

    private static int setEntityScale(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            //read values from command
            final EntityType<?> entityType = ResourceArgument.getEntityType(context, "entity_type").value();
            final SamplerTypes scalingType = context.getArgument("scaling_type", SamplerTypes.class);
            final float minScale = FloatArgumentType.getFloat(context, "min_scaling");
            final float maxScale = FloatArgumentType.getFloat(context, "max_scaling");

            //updating and saving config
            RandomMobSizesMod.LOGGER.info("Setting scale for entity type '{}' to '{}' with min scale '{}' and max scale '{}'", entityType, scalingType, minScale, maxScale);
            boolean success = RandomMobSizesConfig.setScalingSampler(entityType, scalingType.create(minScale, maxScale));
            if (success) {
                ConfigManager.saveConfig();
                ResponseHelper.sendSuccessScalingTypeSet(source, entityType, scalingType, minScale, maxScale);
            } else {
                ResponseHelper.sendErrorScalingTypeSet(source, entityType);
            }
            return 1;
        } catch (Exception e) {
            return errorHandling(source, e, "An error occurred while setting the scale for the entity type!");
        }
    }

    private static int setEntityScaleStatic(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            //read values from command
            final EntityType<?> entityType = ResourceArgument.getEntityType(context, "entity_type").value();
            final float scale = FloatArgumentType.getFloat(context, "scale");

            //updating and saving config
            RandomMobSizesMod.LOGGER.info("Setting scale for entity type '{}' to static scale of '{}'", entityType, scale);
            boolean success = RandomMobSizesConfig.setScalingSampler(entityType, new StaticScalingSampler(scale));
            if (success) {
                ConfigManager.saveConfig();
                ResponseHelper.sendSuccessStaticScalingTypeSet(source, entityType, scale);
            } else {
                ResponseHelper.sendErrorScalingTypeSet(source, entityType);
            }
            return 1;
        } catch (Exception e) {
            return errorHandling(source, e, "An error occurred while setting the static scale for the entity type!");
        }
    }

    private static int removeEntityScale(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            //read values from command
            final EntityType<?> entityType = ResourceArgument.getEntityType(context, "entity_type").value();

            //updating and saving config
            RandomMobSizesMod.LOGGER.info("Removing scale for entity type '{}'", entityType);
            RandomMobSizesConfig.removeScalingSampler(entityType);
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
        RandomMobSizesConfig.getIterator().forEachRemaining(entry -> ResponseHelper.sendSuccessScalingType(source, entry.getKey(), entry.getValue()));
        return 1;
    }

    private static int errorHandling(CommandSourceStack source, Exception e, String text) {
        MutableComponent message = Component.literal(text).withStyle(ChatFormatting.RED);
        ResponseHelper.sendMessage(source, message, false);
        RandomMobSizesMod.LOGGER.error(message.getString(), e);
        return 0;
    }

}