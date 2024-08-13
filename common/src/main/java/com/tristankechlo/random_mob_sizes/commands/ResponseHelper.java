package com.tristankechlo.random_mob_sizes.commands;

import com.tristankechlo.random_mob_sizes.RandomMobSizes;
import com.tristankechlo.random_mob_sizes.config.ConfigManager;
import com.tristankechlo.random_mob_sizes.sampler.SamplerTypes;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.EntityType;

public final class ResponseHelper {

    public static void sendMessageConfigShow(CommandSourceStack source) {
        MutableComponent clickableFile = clickableConfig();
        MutableComponent message = new TextComponent("Config-file can be found here: ").append(clickableFile);
        sendMessage(source, message.withStyle(ChatFormatting.WHITE), false);
    }

    public static void sendMessageConfigReload(CommandSourceStack source, boolean success) {
        String text = success ? "Config was successfully reloaded." : "Error while reloading config. Using default config.";
        MutableComponent message = new TextComponent(text).withStyle(ChatFormatting.WHITE);
        sendMessage(source, message, true);
    }

    public static void sendMessageConfigReset(CommandSourceStack source, boolean success) {
        String text = success ? "Config was successfully reset." : "Error while saving the default config.";
        MutableComponent message = new TextComponent(text).withStyle(ChatFormatting.WHITE);
        sendMessage(source, message, true);
    }

    public static MutableComponent start() {
        return new TextComponent("[" + RandomMobSizes.MOD_NAME + "] ").withStyle(ChatFormatting.GOLD);
    }

    public static void sendMessage(CommandSourceStack source, Component message, boolean broadcastToOps) {
        MutableComponent start = start().append(message);
        source.sendSuccess(start, broadcastToOps);
    }

    public static MutableComponent clickableConfig() {
        String fileName = ConfigManager.FILE_NAME;
        String filePath = ConfigManager.getConfigPath();
        MutableComponent mutableComponent = new TextComponent(fileName);
        mutableComponent.withStyle(ChatFormatting.GREEN, ChatFormatting.UNDERLINE);
        mutableComponent.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, filePath)));
        return mutableComponent;
    }

    public static MutableComponent clickableLink(String url, String displayText) {
        MutableComponent mutableComponent = new TextComponent(displayText);
        mutableComponent.withStyle(ChatFormatting.GREEN, ChatFormatting.UNDERLINE);
        mutableComponent.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
        return mutableComponent;
    }

    public static MutableComponent clickableLink(String url) {
        return clickableLink(url, url);
    }

    public static void sendSuccessScalingTypeSet(CommandSourceStack source, EntityType<?> entityType, SamplerTypes samplerTypes, CompoundTag data) {
        MutableComponent message = new TextComponent("Scaling for ").withStyle(ChatFormatting.WHITE)
                .append(new TextComponent(entityType.getDescription().getString()).withStyle(ChatFormatting.GREEN))
                .append(new TextComponent(" was set to ").withStyle(ChatFormatting.WHITE))
                .append(new TextComponent(samplerTypes.toString()).withStyle(ChatFormatting.GREEN))
                .append(new TextComponent(" with data ").withStyle(ChatFormatting.WHITE))
                .append(new TextComponent(data.toString()).withStyle(ChatFormatting.GREEN));
        sendMessage(source, message, true);
    }

    public static void sendErrorScalingTypeSet(CommandSourceStack source, EntityType<?> entityType) {
        MutableComponent message = new TextComponent("Scaling for ").withStyle(ChatFormatting.RED)
                .append(new TextComponent(entityType.getDescription().getString()).withStyle(ChatFormatting.DARK_RED))
                .append(new TextComponent(" is now allowed!").withStyle(ChatFormatting.RED));
        sendMessage(source, message, true);
    }

    public static void sendSuccessScalingTypeRemoved(CommandSourceStack source, EntityType<?> entityType) {
        MutableComponent message = new TextComponent("Scaling for ").withStyle(ChatFormatting.WHITE)
                .append(new TextComponent(entityType.getDescription().getString()).withStyle(ChatFormatting.GREEN))
                .append(new TextComponent(" was removed from the config.").withStyle(ChatFormatting.WHITE));
        sendMessage(source, message, true);
    }

    public static void sendErrorScalingTypeNotSet(CommandSourceStack source, EntityType<?> entityType) {
        MutableComponent message = new TextComponent("Scaling for ").withStyle(ChatFormatting.WHITE)
                .append(new TextComponent(entityType.getDescription().getString()).withStyle(ChatFormatting.GREEN))
                .append(new TextComponent(" is not set in the config.").withStyle(ChatFormatting.WHITE));
        sendMessage(source, message, false);
    }

    public static void sendSuccessScalingType(CommandSourceStack source, EntityType<?> entityType, ScalingSampler sampler) {
        sendSuccessScalingType(source, entityType.getDescription().getString(), sampler);
    }

    public static void sendSuccessScalingType(CommandSourceStack source, String entityType, ScalingSampler sampler) {
        MutableComponent entity = new TextComponent(entityType).withStyle(ChatFormatting.GREEN);
        MutableComponent result = new TextComponent(sampler.serialize().toString()).withStyle(ChatFormatting.GREEN);
        MutableComponent message = new TranslatableComponent("Scaling for %s is set to %s", entity, result).withStyle(ChatFormatting.WHITE);
        sendMessage(source, message, false);
    }

}
