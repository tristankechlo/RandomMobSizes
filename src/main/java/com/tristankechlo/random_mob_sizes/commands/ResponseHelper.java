package com.tristankechlo.random_mob_sizes.commands;

import com.tristankechlo.random_mob_sizes.RandomMobSizesMod;
import com.tristankechlo.random_mob_sizes.config.ConfigManager;
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

}
