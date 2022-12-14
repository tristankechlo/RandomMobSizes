package com.tristankechlo.random_mob_sizes.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.tristankechlo.random_mob_sizes.RandomMobSizesMod;
import com.tristankechlo.random_mob_sizes.config.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.literal;

public final class RandomMobSizesCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command = literal(RandomMobSizesMod.MOD_ID)
                .then(literal("config").requires((source) -> source.hasPermission(3))
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

    private static int configReload(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ConfigManager.reloadConfig();
        ResponseHelper.sendMessageConfigReload(source);
        return 1;
    }

    private static int configShow(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ResponseHelper.sendMessageConfigShow(source);
        return 1;
    }

    private static int configReset(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ConfigManager.resetConfig();
        ResponseHelper.sendMessageConfigReset(source);
        return 1;
    }

    private static int github(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Component link = ResponseHelper.clickableLink(RandomMobSizesMod.GITHUB_URL);
        Component message = Component.literal("Check out the source code on GitHub: ").withStyle(ChatFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int issue(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Component link = ResponseHelper.clickableLink(RandomMobSizesMod.GITHUB_ISSUE_URL);
        Component message = Component.literal("If you found an issue, submit it here: ").withStyle(ChatFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int wiki(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Component link = ResponseHelper.clickableLink(RandomMobSizesMod.GITHUB_WIKI_URL);
        Component message = Component.literal("The wiki can be found here: ").withStyle(ChatFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int discord(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Component link = ResponseHelper.clickableLink(RandomMobSizesMod.DISCORD_URL);
        Component message = Component.literal("Join the Discord here: ").withStyle(ChatFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int curseforge(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Component link = ResponseHelper.clickableLink(RandomMobSizesMod.CURSEFORGE_URL);
        Component message = Component.literal("Check out the CurseForge page here: ").withStyle(ChatFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int modrinth(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Component link = ResponseHelper.clickableLink(RandomMobSizesMod.MODRINTH_URL);
        Component message = Component.literal("Check out the Modrinth page here: ").withStyle(ChatFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

}
