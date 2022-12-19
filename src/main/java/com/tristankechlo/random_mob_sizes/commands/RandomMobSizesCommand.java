package com.tristankechlo.random_mob_sizes.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.tristankechlo.random_mob_sizes.RandomMobSizesMod;
import com.tristankechlo.random_mob_sizes.config.ConfigManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public final class RandomMobSizesCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> command = literal(RandomMobSizesMod.MOD_ID)
                .then(literal("config").requires((source) -> source.hasPermissionLevel(3))
                        .then(literal("reload").executes(RandomMobSizesCommand::configReload))
                        .then(literal("show").executes(RandomMobSizesCommand::configShow))
                        .then(literal("reset").executes(RandomMobSizesCommand::configReset)))
                .then(literal("github").executes(RandomMobSizesCommand::github))
                .then(literal("issue").executes(RandomMobSizesCommand::issue))
                .then(literal("wiki").executes(RandomMobSizesCommand::wiki))
                .then(literal("discord").executes(RandomMobSizesCommand::discord))
                .then(literal("curseforge").executes(RandomMobSizesCommand::curseforge))
                .then(literal("modrinth").executes(RandomMobSizesCommand::modrinth));
        dispatcher.register(command);
        RandomMobSizesMod.LOGGER.info("Command '/{}' registered", RandomMobSizesMod.MOD_ID);
    }

    private static int configReload(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ConfigManager.reloadConfig();
        ResponseHelper.sendMessageConfigReload(source);
        return 1;
    }

    private static int configShow(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ResponseHelper.sendMessageConfigShow(source);
        return 1;
    }

    private static int configReset(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ConfigManager.resetConfig();
        ResponseHelper.sendMessageConfigReset(source);
        return 1;
    }

    private static int github(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MutableText link = ResponseHelper.clickableLink(RandomMobSizesMod.GITHUB_URL);
        MutableText message = Text.literal("Check out the source code on GitHub: ").formatted(Formatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int issue(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MutableText link = ResponseHelper.clickableLink(RandomMobSizesMod.GITHUB_ISSUE_URL);
        MutableText message = Text.literal("If you found an issue, submit it here: ").formatted(Formatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int wiki(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MutableText link = ResponseHelper.clickableLink(RandomMobSizesMod.GITHUB_WIKI_URL);
        MutableText message = Text.literal("The wiki can be found here: ").formatted(Formatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int discord(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MutableText link = ResponseHelper.clickableLink(RandomMobSizesMod.DISCORD_URL);
        MutableText message = Text.literal("Join the Discord here: ").formatted(Formatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int curseforge(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MutableText link = ResponseHelper.clickableLink(RandomMobSizesMod.CURSEFORGE_URL);
        MutableText message = Text.literal("Check out the CurseForge page here: ").formatted(Formatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int modrinth(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MutableText link = ResponseHelper.clickableLink(RandomMobSizesMod.MODRINTH_URL);
        MutableText message = Text.literal("Check out the Modrinth page here: ").formatted(Formatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

}
