package com.tristankechlo.random_mob_sizes.commands;

import com.tristankechlo.random_mob_sizes.RandomMobSizesMod;
import com.tristankechlo.random_mob_sizes.config.ConfigManager;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.StaticScalingSampler;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class ResponseHelper {

    public static void sendMessageConfigShow(ServerCommandSource source) {
        MutableText clickableFile = clickableConfig();
        MutableText message = Text.literal("Config-file can be found here: ").append(clickableFile);
        sendMessage(source, message.formatted(Formatting.WHITE), false);
    }

    public static void sendMessageConfigReload(ServerCommandSource source) {
        MutableText message = Text.literal("Config was successfully reloaded.");
        sendMessage(source, message.formatted(Formatting.WHITE), true);
    }

    public static void sendMessageConfigReset(ServerCommandSource source) {
        MutableText message = Text.literal("Config was successfully set to default.");
        sendMessage(source, message.formatted(Formatting.WHITE), true);
    }

    public static MutableText start() {
        return Text.literal("[" + RandomMobSizesMod.MOD_NAME + "] ").formatted(Formatting.GOLD);
    }

    public static void sendMessage(ServerCommandSource source, MutableText message, boolean broadcastToOps) {
        MutableText start = start().append(message);
        source.sendFeedback(start, broadcastToOps);
    }

    public static MutableText clickableConfig() {
        String fileName = ConfigManager.FILE_NAME;
        String filePath = ConfigManager.getConfigPath();
        MutableText mutableComponent = Text.literal(fileName);
        mutableComponent.formatted(Formatting.GREEN, Formatting.UNDERLINE);
        mutableComponent.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, filePath)));
        return mutableComponent;
    }

    public static MutableText clickableLink(String url, String displayText) {
        MutableText mutableComponent = Text.literal(displayText);
        mutableComponent.formatted(Formatting.GREEN, Formatting.UNDERLINE);
        mutableComponent.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
        return mutableComponent;
    }

    public static MutableText clickableLink(String url) {
        return clickableLink(url, url);
    }

    public static void sendSuccessScalingTypeSet(ServerCommandSource source, EntityType<?> entityType, SamplerTypes samplerTypes, float min_scaling, float max_scaling) {
        MutableText message = Text.literal("Scaling for ").formatted(Formatting.WHITE)
                .append(Text.literal(entityType.getName().getString()).formatted(Formatting.GREEN))
                .append(Text.literal(" was set to ").formatted(Formatting.WHITE))
                .append(Text.literal(samplerTypes.toString()).formatted(Formatting.GREEN))
                .append(Text.literal(" with min_scaling ").formatted(Formatting.WHITE))
                .append(Text.literal(String.valueOf(min_scaling)).formatted(Formatting.GREEN))
                .append(Text.literal(" and max_scaling ").formatted(Formatting.WHITE))
                .append(Text.literal(String.valueOf(max_scaling)).formatted(Formatting.GREEN));
        sendMessage(source, message, true);
    }

    public static void sendErrorScalingTypeSet(ServerCommandSource source, EntityType<?> entityType) {
        MutableText message = Text.literal("Scaling for ").formatted(Formatting.RED)
                .append(Text.literal(entityType.getName().getString()).formatted(Formatting.DARK_RED))
                .append(Text.literal(" is now allowed!").formatted(Formatting.RED));
        sendMessage(source, message, true);
    }

    public static void sendSuccessStaticScalingTypeSet(ServerCommandSource source, EntityType<?> entityType, float scaling) {
        MutableText message = Text.literal("Scaling for ").formatted(Formatting.WHITE)
                .append(Text.literal(entityType.getName().getString()).formatted(Formatting.GREEN))
                .append(Text.literal(" was set to static scaling of ").formatted(Formatting.WHITE))
                .append(Text.literal(String.valueOf(scaling)).formatted(Formatting.GREEN));
        sendMessage(source, message, true);
    }

    public static void sendSuccessScalingTypeRemoved(ServerCommandSource source, EntityType<?> entityType) {
        MutableText message = Text.literal("Scaling for ").formatted(Formatting.WHITE)
                .append(Text.literal(entityType.getName().getString()).formatted(Formatting.GREEN))
                .append(Text.literal(" was removed from the config.").formatted(Formatting.WHITE));
        sendMessage(source, message, true);
    }

    public static void sendErrorScalingTypeNotSet(ServerCommandSource source, EntityType<?> entityType) {
        MutableText message = Text.literal("Scaling for ").formatted(Formatting.WHITE)
                .append(Text.literal(entityType.getName().getString()).formatted(Formatting.GREEN))
                .append(Text.literal(" is not set in the config.").formatted(Formatting.WHITE));
        sendMessage(source, message, false);
    }

    public static void sendSuccessScalingType(ServerCommandSource source, EntityType<?> entityType, ScalingSampler sampler) {
        String result = (sampler instanceof StaticScalingSampler) ? String.valueOf(sampler.sample()) : sampler.serialize().toString();
        MutableText message = Text.literal("Scaling for ").formatted(Formatting.WHITE)
                .append(Text.literal(entityType.getName().getString()).formatted(Formatting.GREEN))
                .append(Text.literal(" is set to ").formatted(Formatting.WHITE))
                .append(Text.literal(result).formatted(Formatting.GREEN));
        sendMessage(source, message, false);
    }

}
