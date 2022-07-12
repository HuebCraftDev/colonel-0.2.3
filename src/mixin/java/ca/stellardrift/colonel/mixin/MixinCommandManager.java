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
package ca.stellardrift.colonel.mixin;

import ca.stellardrift.colonel.api.ServerArgumentType;
import ca.stellardrift.colonel.impl.ServerArgumentTypes;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Mixin(CommandManager.class)
public class MixinCommandManager {

    @SuppressWarnings({"rawtypes", "unchecked"}) // argument type generics
    @Inject(method = "makeTreeForSource", locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            at = @At(value = "INVOKE", target = "com.mojang.brigadier.builder.RequiredArgumentBuilder.getSuggestionsProvider()Lcom/mojang/brigadier/suggestion/SuggestionProvider;", remap = false, ordinal = 0)
            /*slice = @Slice(from = @At(value = "INVOKE_ASSIGN", target = "RequiredArgumentBuilder.executes(Lcom/mojang/brigadier/Command;)Lcom/mojang/brigadier/builder/ArgumentBuilder;", remap = false),
                    to = @At(value = "INVOKE", target = "RequiredArgumentBuilder.getRedirect()Lcom/mojang/brigadier/tree/CommandNode;", remap = false))*/)
    public <T> void colonel$replaceArgumentType(CommandNode<ServerCommandSource> tree, CommandNode<CommandSource> result, ServerCommandSource source, Map<CommandNode<ServerCommandSource>, CommandNode<CommandSource>> nodes,
                                        final CallbackInfo ci, final Iterator<?> it,
                                        final CommandNode<ServerCommandSource> current, final ArgumentBuilder<?, ?> unused, final RequiredArgumentBuilder<?, T> builder) throws CommandSyntaxException {
        ServerArgumentType<ArgumentType<T>> type = ServerArgumentTypes.byClass((Class) builder.getType().getClass());
        final Set<Identifier> knownExtraCommands = ServerArgumentTypes.getKnownArgumentTypes(source.getPlayer()); // throws an exception, we can ignore bc this is always a player
        // If we have a replacement and the arg type isn't known to the client, change the argument type
        // This is super un-typesafe, but as long as the returned CommandNode is only used for serialization we are fine.
        // Repeat as long as a type is replaceable -- that way you can have a hierarchy of argument types.
        while (type != null && !knownExtraCommands.contains(type.id())) {
            ((AccessorRequiredArgumentBuilder) builder).accessor$type(type.fallbackProvider().apply(builder.getType()));
            if (type.fallbackSuggestions() != null) {
                builder.suggests((SuggestionProvider) type.fallbackSuggestions());
            }
            type = ServerArgumentTypes.byClass((Class) builder.getType().getClass());
        }

    }
}
