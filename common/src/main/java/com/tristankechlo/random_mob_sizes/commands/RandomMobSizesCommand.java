package com.tristankechlo.random_mob_sizes.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.tristankechlo.random_mob_sizes.RandomMobSizes;
import com.tristankechlo.random_mob_sizes.config.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import static net.minecraft.commands.Commands.literal;

public final class RandomMobSizesCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command = literal(RandomMobSizes.MOD_ID)
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
        RandomMobSizes.LOGGER.info("Command '/{}' registered", RandomMobSizes.MOD_ID);
    }

    private static int configReload(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        boolean success = ConfigManager.reloadConfig();
        ResponseHelper.sendMessageConfigReload(source, success);
        return 1;
    }

    private static int configShow(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ResponseHelper.sendMessageConfigShow(source);
        return 1;
    }

    private static int configReset(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        boolean success = ConfigManager.resetConfig();
        ResponseHelper.sendMessageConfigReset(source, success);
        return 1;
    }

    private static int github(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Component link = ResponseHelper.clickableLink(RandomMobSizes.GITHUB_URL);
        Component message = new TextComponent("Check out the source code on GitHub: ").withStyle(ChatFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int issue(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Component link = ResponseHelper.clickableLink(RandomMobSizes.GITHUB_ISSUE_URL);
        Component message = new TextComponent("If you found an issue, submit it here: ").withStyle(ChatFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int wiki(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Component link = ResponseHelper.clickableLink(RandomMobSizes.GITHUB_WIKI_URL);
        Component message = new TextComponent("The wiki can be found here: ").withStyle(ChatFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int discord(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Component link = ResponseHelper.clickableLink(RandomMobSizes.DISCORD_URL);
        Component message = new TextComponent("Join the Discord here: ").withStyle(ChatFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int curseforge(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Component link = ResponseHelper.clickableLink(RandomMobSizes.CURSEFORGE_URL);
        Component message = new TextComponent("Check out the CurseForge page here: ").withStyle(ChatFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int modrinth(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Component link = ResponseHelper.clickableLink(RandomMobSizes.MODRINTH_URL);
        Component message = new TextComponent("Check out the Modrinth page here: ").withStyle(ChatFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

}
