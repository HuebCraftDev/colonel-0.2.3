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

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;

/**
 * A packet sent client to server, to let the server know which optional argument types are available on the server.
 *
 * <p>This packet is sent by players on join, before the command tree is sent to the client.</p>
 */
@AutoValue
public abstract class RegisteredArgumentTypesC2SPacket {
    public static final Identifier ID = Colonel.id("registered-args");

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ID, (server, player, handler, buffer, responder) -> {
            final RegisteredArgumentTypesC2SPacket pkt = RegisteredArgumentTypesC2SPacket.of(buffer);
            server.execute(() -> { // on main thread
                ServerArgumentTypes.setKnownArgumentTypes(player, pkt.idents());
            });
        });
    }

    public static RegisteredArgumentTypesC2SPacket of(final Set<Identifier> idents) {
        return new AutoValue_RegisteredArgumentTypesC2SPacket(ImmutableSet.copyOf(idents));
    }

    public static RegisteredArgumentTypesC2SPacket of(final @NonNull PacketByteBuf buf) {
        final int length = buf.readVarInt();
        final ImmutableSet.Builder<Identifier> items = ImmutableSet.builder();
        for(int i = 0; i < length; ++i) {
            items.add(buf.readIdentifier());
        }
        return of(items.build());
    }

    /**
     * Get the registered identifiers.
     *
     * <p>Every identifier represents an argument type registered in {@link ServerArgumentTypes}</p>
     *
     * @return an unmodifiable list of argument type identifiers
     */
    public abstract Set<Identifier> idents();

    public final void toPacket(final PacketByteBuf buffer) {
        buffer.writeVarInt(idents().size());
        for (Identifier id : idents()) {
            buffer.writeIdentifier(id);
        }
    }

    /**
     * Send the client's list of identifiers to the server.
     */
    public final void sendTo(final PacketSender sender) {
        if (ClientPlayNetworking.canSend(ID)) {
            final PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer(idents().size() * 8));
            toPacket(buffer);
            sender.sendPacket(ID, buffer);
        }
    }
}
