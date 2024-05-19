package com.tristankechlo.random_mob_sizes.commands;

import com.tristankechlo.random_mob_sizes.RandomMobSizes;
import com.tristankechlo.random_mob_sizes.config.ConfigManager;
import com.tristankechlo.random_mob_sizes.sampler.SamplerTypes;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;

public final class ResponseHelper {

    public static void sendMessageConfigShow(CommandSourceStack source) {
        MutableComponent clickableFile = clickableConfig();
        MutableComponent message = Component.literal("Config-file can be found here: ").append(clickableFile);
        sendMessage(source, message.withStyle(ChatFormatting.WHITE), false);
        MutableComponent message2 = Component.literal("Click on the underlined filename to copy the full path to your clipboard.");
        sendMessage(source, message2.withStyle(ChatFormatting.WHITE), false);
    }

    public static void sendMessageConfigReload(CommandSourceStack source, boolean success) {
        String text = success ? "Config was successfully reloaded." : "Error while reloading config. Check the logs for further details.";
        MutableComponent message = Component.literal(text).withStyle(ChatFormatting.WHITE);
        sendMessage(source, message, true);
    }

    public static void sendMessageConfigReset(CommandSourceStack source, boolean success) {
        String text = success ? "Config was successfully reset." : "Error while saving the default config.";
        MutableComponent message = Component.literal(text).withStyle(ChatFormatting.WHITE);
        sendMessage(source, message, true);
    }

    public static MutableComponent start() {
        return Component.literal("[" + RandomMobSizes.MOD_NAME + "] ").withStyle(ChatFormatting.GOLD);
    }

    public static void sendMessage(CommandSourceStack source, Component message, boolean broadcastToOps) {
        MutableComponent start = start().append(message);
        source.sendSuccess(() -> start, broadcastToOps);
    }

    public static MutableComponent clickableConfig() {
        String fileName = ConfigManager.FILE_NAME;
        String filePath = ConfigManager.getConfigPath();
        MutableComponent mutableComponent = Component.literal(fileName);
        mutableComponent.withStyle(ChatFormatting.GREEN, ChatFormatting.UNDERLINE);
        mutableComponent.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, filePath)));
        return mutableComponent;
    }

    public static MutableComponent clickableLink(String url, String displayText) {
        MutableComponent mutableComponent = Component.literal(displayText);
        mutableComponent.withStyle(ChatFormatting.GREEN, ChatFormatting.UNDERLINE);
        mutableComponent.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
        return mutableComponent;
    }

    public static MutableComponent clickableLink(String url) {
        return clickableLink(url, url);
    }

    public static void sendSuccessScalingTypeSet(CommandSourceStack source, EntityType<?> entityType, SamplerTypes samplerTypes, CompoundTag data) {
        MutableComponent message = Component.literal("Scaling for ").withStyle(ChatFormatting.WHITE)
                .append(Component.literal(entityType.getDescription().getString()).withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" was set to ").withStyle(ChatFormatting.WHITE))
                .append(Component.literal(samplerTypes.toString()).withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" with data ").withStyle(ChatFormatting.WHITE))
                .append(Component.literal(data.toString()).withStyle(ChatFormatting.GREEN));
        sendMessage(source, message, true);
    }

    public static void sendErrorScalingTypeSet(CommandSourceStack source, EntityType<?> entityType) {
        MutableComponent message = Component.literal("Scaling for ").withStyle(ChatFormatting.RED)
                .append(Component.literal(entityType.getDescription().getString()).withStyle(ChatFormatting.DARK_RED))
                .append(Component.literal(" is now allowed!").withStyle(ChatFormatting.RED));
        sendMessage(source, message, true);
    }

    public static void sendSuccessScalingTypeRemoved(CommandSourceStack source, EntityType<?> entityType) {
        MutableComponent message = Component.literal("Scaling for ").withStyle(ChatFormatting.WHITE)
                .append(Component.literal(entityType.getDescription().getString()).withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" was removed from the config.").withStyle(ChatFormatting.WHITE));
        sendMessage(source, message, true);
    }

    public static void sendErrorScalingTypeNotSet(CommandSourceStack source, EntityType<?> entityType) {
        MutableComponent message = Component.literal("Scaling for ").withStyle(ChatFormatting.WHITE)
                .append(Component.literal(entityType.getDescription().getString()).withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" is not set in the config.").withStyle(ChatFormatting.WHITE));
        sendMessage(source, message, false);
    }

    public static void sendSuccessScalingType(CommandSourceStack source, EntityType<?> entityType, ScalingSampler sampler) {
        sendSuccessScalingType(source, entityType.getDescription().getString(), sampler);
    }

    public static void sendSuccessScalingType(CommandSourceStack source, String entityType, ScalingSampler sampler) {
        MutableComponent entity = Component.literal(entityType).withStyle(ChatFormatting.GREEN);
        MutableComponent result = Component.literal(sampler.serialize().toString()).withStyle(ChatFormatting.GREEN);
        MutableComponent message = Component.translatable("Scaling for %s is set to %s", entity, result).withStyle(ChatFormatting.WHITE);
        sendMessage(source, message, false);
    }

}
