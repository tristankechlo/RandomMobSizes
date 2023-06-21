package com.tristankechlo.random_mob_sizes.commands;

import com.tristankechlo.random_mob_sizes.RandomMobSizes;
import com.tristankechlo.random_mob_sizes.config.ConfigManager;
import com.tristankechlo.random_mob_sizes.sampler.ScalingSampler;
import com.tristankechlo.random_mob_sizes.sampler.StaticScalingSampler;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;

public final class ResponseHelper {

    public static void sendMessageConfigShow(CommandSourceStack source) {
        MutableComponent clickableFile = clickableConfig();
        MutableComponent message = new TextComponent("Config-file can be found here: ").append(clickableFile);
        sendMessage(source, message.withStyle(ChatFormatting.WHITE), false);
    }

    public static void sendMessageConfigReload(CommandSourceStack source) {
        MutableComponent message = new TextComponent("Config was successfully reloaded.");
        sendMessage(source, message.withStyle(ChatFormatting.WHITE), true);
    }

    public static void sendMessageConfigReset(CommandSourceStack source) {
        MutableComponent message = new TextComponent("Config was successfully set to default.");
        sendMessage(source, message.withStyle(ChatFormatting.WHITE), true);
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

    public static void sendSuccessScalingTypeSet(CommandSourceStack source, EntityType<?> entityType, SamplerTypes samplerTypes, float min_scaling, float max_scaling) {
        MutableComponent message = new TextComponent("Scaling for ").withStyle(ChatFormatting.WHITE)
                .append(new TextComponent(entityType.getDescription().getString()).withStyle(ChatFormatting.GREEN))
                .append(new TextComponent(" was set to ").withStyle(ChatFormatting.WHITE))
                .append(new TextComponent(samplerTypes.toString()).withStyle(ChatFormatting.GREEN))
                .append(new TextComponent(" with min_scaling ").withStyle(ChatFormatting.WHITE))
                .append(new TextComponent(String.valueOf(min_scaling)).withStyle(ChatFormatting.GREEN))
                .append(new TextComponent(" and max_scaling ").withStyle(ChatFormatting.WHITE))
                .append(new TextComponent(String.valueOf(max_scaling)).withStyle(ChatFormatting.GREEN));
        sendMessage(source, message, true);
    }

    public static void sendErrorScalingTypeSet(CommandSourceStack source, EntityType<?> entityType) {
        MutableComponent message = new TextComponent("Scaling for ").withStyle(ChatFormatting.RED)
                .append(new TextComponent(entityType.getDescription().getString()).withStyle(ChatFormatting.DARK_RED))
                .append(new TextComponent(" is now allowed!").withStyle(ChatFormatting.RED));
        sendMessage(source, message, true);
    }

    public static void sendSuccessStaticScalingTypeSet(CommandSourceStack source, EntityType<?> entityType, float scaling) {
        MutableComponent message = new TextComponent("Scaling for ").withStyle(ChatFormatting.WHITE)
                .append(new TextComponent(entityType.getDescription().getString()).withStyle(ChatFormatting.GREEN))
                .append(new TextComponent(" was set to static scaling of ").withStyle(ChatFormatting.WHITE))
                .append(new TextComponent(String.valueOf(scaling)).withStyle(ChatFormatting.GREEN));
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
        String result = (sampler instanceof StaticScalingSampler) ? String.valueOf(sampler.sample()) : sampler.serialize().toString();
        MutableComponent message = new TextComponent("Scaling for ").withStyle(ChatFormatting.WHITE)
                .append(new TextComponent(entityType.getDescription().getString()).withStyle(ChatFormatting.GREEN))
                .append(new TextComponent(" is set to ").withStyle(ChatFormatting.WHITE))
                .append(new TextComponent(result).withStyle(ChatFormatting.GREEN));
        sendMessage(source, message, false);
    }

}
