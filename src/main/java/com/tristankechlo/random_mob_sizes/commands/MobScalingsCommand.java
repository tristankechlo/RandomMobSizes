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
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.tristankechlo.random_mob_sizes.sampler.ScalingSampler.MAXIMUM_SCALING;
import static com.tristankechlo.random_mob_sizes.sampler.ScalingSampler.MINIMUM_SCALING;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class MobScalingsCommand {

    private static final String COMMAND_NAME = "mobScalings";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access) {
        LiteralArgumentBuilder<ServerCommandSource> command = literal(COMMAND_NAME).requires((source) -> source.hasPermissionLevel(3))
                .then(literal("set")
                        .then(argument("entity_type", RegistryEntryArgumentType.registryEntry(access, RegistryKeys.ENTITY_TYPE))
                                .then(argument("scaling_type", SamplerTypesArgumentType.get())
                                        .then(argument("min_scaling", FloatArgumentType.floatArg(MINIMUM_SCALING, MAXIMUM_SCALING))
                                                .then(argument("max_scaling", FloatArgumentType.floatArg(MINIMUM_SCALING, MAXIMUM_SCALING))
                                                        .executes(MobScalingsCommand::setEntityScale))))
                                .then(argument("scale", FloatArgumentType.floatArg(MINIMUM_SCALING, MAXIMUM_SCALING))
                                        .executes(MobScalingsCommand::setEntityScaleStatic))))
                .then(literal("remove")
                        .then(argument("entity_type", RegistryEntryArgumentType.registryEntry(access, RegistryKeys.ENTITY_TYPE))
                                .executes(MobScalingsCommand::removeEntityScale)))
                .then(literal("show")
                        .then(argument("entity_type", RegistryEntryArgumentType.registryEntry(access, RegistryKeys.ENTITY_TYPE))
                                .executes(MobScalingsCommand::showEntityScale))
                        .executes(MobScalingsCommand::showAllEntityScales));
        dispatcher.register(command);
        RandomMobSizesMod.LOGGER.info("Command '/{}' registered", COMMAND_NAME);
    }

    private static int setEntityScale(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        try {
            //read values from command
            final EntityType<?> entityType = RegistryEntryArgumentType.getEntityType(context, "entity_type").value();
            final SamplerTypes scalingType = SamplerTypesArgumentType.getSamplerType(context, "scaling_type");
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
            return errorHandling(source, e, "An error occurred while setting the scale for the entity type");
        }
    }

    private static int setEntityScaleStatic(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        try {
            //read values from command
            final EntityType<?> entityType = RegistryEntryArgumentType.getEntityType(context, "entity_type").value();
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

    private static int removeEntityScale(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        try {
            //read values from command
            final EntityType<?> entityType = RegistryEntryArgumentType.getEntityType(context, "entity_type").value();

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

    private static int showEntityScale(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        try {
            //read values from command
            final EntityType<?> entityType = RegistryEntryArgumentType.getEntityType(context, "entity_type").value();
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

    private static int showAllEntityScales(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        RandomMobSizesConfig.getIterator().forEachRemaining(entry -> ResponseHelper.sendSuccessScalingType(source, entry.getKey(), entry.getValue()));
        return 1;
    }

    private static int errorHandling(ServerCommandSource source, Exception e, String text) {
        MutableText message = Text.literal(text).formatted(Formatting.RED);
        ResponseHelper.sendMessage(source, message, false);
        RandomMobSizesMod.LOGGER.error(message.getString(), e);
        return 0;
    }

}
