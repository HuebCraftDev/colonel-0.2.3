/*
 * Colonel -- a brigadier expansion library
 * Copyright (C) zml and Colonel contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.stellardrift.colonel.test;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;

/**
 * Test argument type getting case-insensitive values from an enum.
 *
 * <p>Probably best used as a base argument type</p>
 *
 * @param <T> Argument type
 */
public final class EnumArgumentType<T extends Enum<T>> implements ArgumentType<T> {
    private static final Dynamic2CommandExceptionType ERROR_UNKNOWN_VALUE = new Dynamic2CommandExceptionType((value, clazz) ->
            new LiteralText("Unknown value '").append(new LiteralText(String.valueOf(value)).styled(s -> s.withItalic(true)))
            .append(new LiteralText("' in ")).append(new LiteralText(((Class<?>) clazz).getSimpleName()).styled(s -> s.withItalic(true))));

    public static <T extends Enum<T>> EnumArgumentType<T> enumerated(final Class<T> enumClass) {
        return new EnumArgumentType<>(requireNonNull(enumClass, "enumClass"));
    }

    public static <T extends Enum<T>> T getEnumerated(final String argKey, final Class<T> enumClazz, final CommandContext<?> ctx) {
        return ctx.getArgument(argKey, enumClazz);
    }

    private final Class<T> enumClazz;
    private final Map<String, T> values = new HashMap<>();
    private final Set<String> examples ;

    private EnumArgumentType(Class<T> enumClazz) {
        this.enumClazz = enumClazz;
        int valueCount = 0;
        final Set<String> examples = new HashSet<>();
        for (T value : enumClazz.getEnumConstants()) {
            final String name = value.name().toLowerCase(Locale.ROOT);
            if(valueCount < 2) {
                examples.add(name);
            }
            this.values.put(name, value);
            ++valueCount;
        }
        this.examples = Collections.unmodifiableSet(examples);
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        final String name = reader.readUnquotedString().toLowerCase(Locale.ROOT);
        final T value = this.values.get(name);
        if (value == null) {
            throw ERROR_UNKNOWN_VALUE.createWithContext(reader, name, this.enumClazz);
        }
        return value;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(this.values.keySet(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return this.examples;
    }

    static final class Serializer implements ArgumentSerializer<EnumArgumentType<?>> {
        private static final int MAX_LEN = 1024;

        @Override
        public void toPacket(EnumArgumentType<?> argumentType, PacketByteBuf packetByteBuf) {
            packetByteBuf.writeString(argumentType.enumClazz.getName(), MAX_LEN);
        }

        @Override
        @SuppressWarnings("unchecked")
        public EnumArgumentType<?> fromPacket(PacketByteBuf packetByteBuf) {
            final String className = packetByteBuf.readString(MAX_LEN);
            try {
                Class<?> value = Class.forName(className);
                if (!value.isEnum()) {
                    throw new IllegalArgumentException("Class " + value + " is not an enum!");
                }
                return new EnumArgumentType<>(value.asSubclass(Enum.class));
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public void toJson(EnumArgumentType<?> argumentType, JsonObject jsonObject) {
            jsonObject.addProperty("enum", argumentType.getClass().getName());
        }
    }
}
