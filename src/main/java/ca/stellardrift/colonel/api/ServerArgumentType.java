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
package ca.stellardrift.colonel.api;

import ca.stellardrift.colonel.impl.ServerArgumentTypes;
import com.google.auto.value.AutoValue;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.util.Identifier;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Function;

/**
 * An argument type that only needs to be known on the server
 *
 * @param <T> argument type
 */
@AutoValue
public abstract class ServerArgumentType<T extends ArgumentType<?>> {

    public static <T extends ArgumentType<?>> Builder<T> builder(final Identifier id) {
        return new AutoValue_ServerArgumentType.Builder<T>()
                .id(id)
                .fallbackSuggestions(SuggestionProviders.ASK_SERVER);
    }

    /**
     * The unique identifier for this argument type.
     *
     * @return identifier
     */
    public abstract Identifier id();

    /**
     * The argument type class to register
     *
     * @return argument type
     */
    public abstract Class<? super T> type();

    /**
     * The type's argument serializer. This will only be used on clients who have this argument attached.
     *
     * @see net.minecraft.command.argument.serialize.ConstantArgumentSerializer for a simple implementation
     * @return serializer
     */
    public abstract ArgumentSerializer<T> serializer();

    /**
     * A function to transform an argument of your type into one understandable by the Vanilla client.
     *
     * <p>This is currently unvalidated -- but generally, anything in {@link ArgumentTypes} should be acceptable</p>
     *
     * @return argument transformer
     */
    public abstract Function<T, ArgumentType<?>> fallbackProvider();

    /**
     * Add an override for suggestions.
     *
     * @return fallback suggestion provider
     */
    public abstract @Nullable SuggestionProvider<?> fallbackSuggestions();

    /**
     * A builder for {@link ServerArgumentType}s
     *
     * <p>All values except for {@link #fallbackSuggestions()} are required.</p>
     *
     * @param <T> type of argument type
     */
    @AutoValue.Builder
    public static abstract class Builder<T extends ArgumentType<?>> {

        /**
         * Argument identifier, for registration and via protocol.
         *
         * @param id The ID
         * @return this
         */
        abstract Builder<T> id(Identifier id);

        /**
         * Set the native argument type.
         *
         * <p>A superclass is accepted within the type parameter to allow for parameterized argument types.
         * This does not allow for extensions types.</p>
         *
         * @param type Native argument type
         * @return this
         */
        public abstract Builder<T> type(final Class<? super T> type);

        /**
         * Set the serializer for the native argument type
         *
         * @param serial serializer
         * @return this
         */
        public abstract Builder<T> serializer(final ArgumentSerializer<T> serial);

        /**
         * Set the provider to be sent to clients without this argument type.
         *
         * <p>The returned argument type may be provided as </p>
         *
         * @param provider function taking own argument type and creating a
         * @return this
         */
        public abstract Builder<T> fallbackProvider(final Function<T, ArgumentType<?>> provider);

        /**
         * Set the suggestion provider that will be set to clients that don't have this argument type.
         *
         * <p>By default, this is {@link SuggestionProviders#ASK_SERVER}, in order to use the full argument type's
         * suggestions. However, if the fallback type provides its own suggestions that meet requirements, this can be explicitly set to null</p>
         *
         * @param suggestions Provider for suggestions that will be sent to the client.
         * @return this
         */
        public abstract Builder<T> fallbackSuggestions(final @Nullable SuggestionProvider<?> suggestions);

        abstract ServerArgumentType<T> build();

        /**
         * Complete the builder and register the argument with the Vanilla {@link ArgumentTypes} registry.
         *
         * @return the constructed argument type data.
         */
        @SuppressWarnings("unchecked")
        public ServerArgumentType<T> register() {
            final ServerArgumentType<T> value = build();
            ArgumentTypes.register(value.id().toString(), (Class<T>) value.type(), value.serializer());
            ServerArgumentTypes.register(value);
            return value;
        }

    }
}
