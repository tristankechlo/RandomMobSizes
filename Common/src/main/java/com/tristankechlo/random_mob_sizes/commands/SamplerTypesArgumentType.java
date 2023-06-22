package com.tristankechlo.random_mob_sizes.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TextComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class SamplerTypesArgumentType implements ArgumentType<SamplerTypes> {

    private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType((o) -> new TextComponent("Invalid sampler type: " + o));
    private static final Collection<String> EXAMPLES = Stream.of(SamplerTypes.values()).map(SamplerTypes::getSerializedName).toList();
    private static final SamplerTypes[] VALUES = SamplerTypes.values();

    @Override
    public SamplerTypes parse(StringReader reader) throws CommandSyntaxException {
        String s = reader.readUnquotedString();
        SamplerTypes type = SamplerTypes.byName(s, null);
        if (type == null) {
            throw ERROR_INVALID.createWithContext(reader, s);
        } else {
            return type;
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof SharedSuggestionProvider) {
            return SharedSuggestionProvider.suggest(Arrays.stream(VALUES).map(SamplerTypes::getSerializedName), builder);
        }
        return Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static SamplerTypesArgumentType get() {
        return new SamplerTypesArgumentType();
    }

    public static SamplerTypes getSamplerType(CommandContext<?> context, String name) {
        return context.getArgument(name, SamplerTypes.class);
    }

}
