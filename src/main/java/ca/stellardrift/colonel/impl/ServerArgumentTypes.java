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
package ca.stellardrift.colonel.impl;

import ca.stellardrift.colonel.api.ServerArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

public class ServerArgumentTypes {
    private static final Map<Class<?>, ServerArgumentType<?>> BY_TYPE = new HashMap<>();
    private static final Map<Identifier, ServerArgumentType<?>> BY_ID = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends ArgumentType<?>> ServerArgumentType<T> byClass(final Class<T> clazz) {
        return (ServerArgumentType<T>)BY_TYPE.get(requireNonNull(clazz, "clazz"));
    }

    public static void register(final ServerArgumentType<?> type) {
        BY_TYPE.put(type.type(), type);
        BY_ID.put(type.id(), type);
    }

    public static Set<Identifier> getIds() {
        return Collections.unmodifiableSet(BY_ID.keySet());
    }

    public static void setKnownArgumentTypes(final PlayerEntity player, final Set<Identifier> ids) {
        if(player instanceof ServerPlayerEntity) {
            final ServerPlayerEntity spe = (ServerPlayerEntity) player;
            ((ServerPlayerBridge) player).colonel$knownArguments(ids);
            if (!ids.isEmpty()) { // TODO: Avoid resending the whole command tree, find a way to receive the packet before sending?
                spe.server.getPlayerManager().sendCommandTree(spe);
            }
        }
    }

    public static Set<Identifier> getKnownArgumentTypes(final ServerPlayerEntity player) {
        return ((ServerPlayerBridge) player).colonel$knownArguments();
    }
}
