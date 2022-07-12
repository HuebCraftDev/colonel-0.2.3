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

import ca.stellardrift.colonel.api.ServerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.checkerframework.checker.nullness.qual.NonNull;

import static ca.stellardrift.colonel.test.EnumArgumentType.enumerated;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ServerArgumentTester implements ModInitializer {

    private static Identifier id(final @NonNull String path) {
        return new Identifier("colonel-testmod", path);
    }

    @Override
    public void onInitialize() {
        ServerArgumentType.<EnumArgumentType<?>>builder(id("enum"))
                .type(EnumArgumentType.class)
                .serializer(new EnumArgumentType.Serializer())
                .fallbackProvider(arg -> StringArgumentType.word())
                .register();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            final LiteralCommandNode<ServerCommandSource> original = dispatcher.register(literal("test-enum").then(argument("element", enumerated(TestEnum.class)).executes(ctx -> {
                final TestEnum value = EnumArgumentType.getEnumerated("element", TestEnum.class, ctx);
                ctx.getSource().sendFeedback(new LiteralText("You've found the enum value ")
                        .append(new LiteralText(value.name()).styled(s -> s.withBold(true))), false);
                return 1;
            })));
            dispatcher.register(literal("tenum").redirect(original));
        });
    }

    public enum TestEnum {
        ONE,
        TWO,
        THREE,
        FOUR_OPTIONS;
    }
}
